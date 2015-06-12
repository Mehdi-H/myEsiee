#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Author: Mehdi-H
# @Date:   2015-06-05 14:38:13
# @Last Modified by:   Mehdi-H
# @Last Modified time: 2015-06-09 19:11:55


from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.action_chains import ActionChains

from bs4 import BeautifulSoup
import parsing_bs4

import mypkg

driver = None

# /**
#  * Fonction qui initialise le webdriver selenium avec PhantomJS, avec les bons arguments, 
#  * et qui verifie qu'Aurion est accessible à "https://aurionprd.esiee.fr".
#  */
def init_selenium_browser(urlAurion = "https://aurionprd.esiee.fr"):

	# Config webbrowser
	driver = mypkg.getOrCreateWebdriver()

	# Tentative accès aurionprd.esiee.fr
	driver.get(urlAurion)
	assert "Page de connexion" in driver.title, "'Page de connexion' devrait apparaitre dans le <title> tag de la page HTML, le site a peut-etre change."
	return True


# /**
#  * Pour quitter le navigateur.
#  */
def quit_selenium_browser():
	driver = mypkg.getOrCreateWebdriver()
	driver.quit()

# /**
#  * Pour retourner à la page d'accueil entre deux extractions
#  */
def back_to_home_page():
	driver = mypkg.getOrCreateWebdriver()

	assert 'Accueil' in driver.page_source, 'Le bouton \'Accueil\' ne semble pas visible par le navigateur'

	homeBtn = driver.find_element_by_link_text('Accueil')

	homeBtn.click()

	return True 

# /**
#  * Fonction qui accède au formulaire de connexion de la page, le remplit, et l'envoie.
#  */
def aurion_connection(l, pwd):
	# Initialisation selenium
	driver = mypkg.getOrCreateWebdriver()

	# Identification des elements de la page à remplir
	form = driver.find_element_by_class_name('form')
	loginBar = driver.find_element_by_id('j_username')
	passwordBar = driver.find_element_by_id('j_password')

	# Remplissage du formulaire et submit
	loginBar.send_keys(l)
	passwordBar.send_keys(pwd)
	form.submit()

	if "Mon compte" in driver.page_source:
		return True
	else:
		return False

	assert "Login ou mot de passe invalide" not in driver.page_source, "Connexion echouee, mauvais mot de passe ou mauvais identifiant"

	return False

# /**
#  * Fonction qui retourne le nombre de page de résultats pour les données d'archives.
#  */
def nb_page_results(s):
	cpt_pages = 0

	# On convertit le tout en objet _soup pour pouvoir appliquer nos traitements
	soup = BeautifulSoup(''.join(s))

	table_notes = soup.find_all(id="form:haut")
	for t in table_notes[0]:
		cpt_pages+=1

	# Il y a 4 boutons de navigation en plus des boutons de numéro
	if (cpt_pages - 4) > 0:
		return cpt_pages - 4
	else:
		return 0

def go_to_data(func):
	driver = mypkg.getOrCreateWebdriver()

	assertions = {
		'grades'            : 'Mes Notes',
		'absences'          : 'Mes Absences',
		'appreciations'     : 'Mes Appréciations',
		'old_grades'        : 'Archives',
		'old_absences'      : 'Archives',
		'old_appreciations' : 'Archives'
	}

	assert assertions[func] in driver.page_source, "Le bouton vers " + assertions[func] + "est introuvable."

	if func == 'old_absences':
		position = str(3)
	elif func == 'old_grades':
		position = str(1)
	elif func == 'old_appreciations':
		position = str(2)

	if 'old' in func:
		archives = driver.find_element_by_xpath("(html/body[@class='templatePrincipal']/form[@id='form']/div[@class='cadreMiseEnPage']/div[@class='sidebar']/div[@class='box']/div[@class='body']/div)[2]")
		hover = ActionChains(driver).move_to_element(archives)
		hover.perform()

		driver.save_screenshot('out.png')

		archivesBtn = driver.find_element_by_xpath("(html/body[@class='templatePrincipal']/form[@id='form']/div[@class='cadreMiseEnPage']/div[@class='sidebar']/div[@class='box']/div[@class='body']/div/div[@class='rf-ddm-pos']/div[@class='rf-ddm-lst'])[2]/div[@class='rf-ddm-lst-bg']/div[" + position + "]/span")
		archivesBtn.click()

		# Bouton de recherche générale pour afficher toutes les notes des années précédentes
		searchAll = driver.find_element_by_id('form:idSearch')
		searchAll.click()

		assert "recherche" in driver.page_source, "L'accès aux données d'archives a peut-etre échoué."

		driver.save_screenshot('out.png')

		return True

	else:
		dataBtn = driver.find_element_by_link_text(assertions[func])
		dataBtn.click()
		return True

def data_to_html():

	driver = mypkg.getOrCreateWebdriver()

	# Stockage du code source pour analyser le nombre de pages de résultats
	elem = driver.page_source
	source_code = elem
	nb_page_archives = nb_page_results(source_code)

	# Ayant le nombre de pages, on peut commencer l'extraction des données
	
	# Extraction du tableau de données de la page #1
	elem = driver.find_element_by_id('form:scrollTable')
	source_code = elem.get_attribute('innerHTML')

	# Variable html pour y ranger tous les tableaux de données
	html = source_code

	# Parcours de la pagination de la page 2 à n
	for page in range(2,nb_page_archives+1):
		# Click sur la page suivante
		btn_page = driver.find_element_by_id('form:haut_ds_next')
		btn_page.click()
		# Extraction du tableau de données
		elem = driver.find_element_by_id('form:scrollTable')
		source_code1 = elem.get_attribute('innerHTML')
		# /!\ Le tableau se met à jour en Ajax, ce qui peut etre tres long a charger donc on reste dans une boucle d attente pour ne pas recuperer des doublons de tableau
		while source_code1 == source_code:
			elem = driver.find_element_by_id('form:scrollTable')
			source_code1 = elem.get_attribute('innerHTML')

		#Tableau de donnees definitif => ajout a la variable html
		source_code = source_code1
		source_code = elem.get_attribute('innerHTML')
		html += source_code

	return parsing_bs4.clean_html(BeautifulSoup(html)).prettify().encode('utf-8')