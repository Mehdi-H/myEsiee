#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Author: mehdi
# @Date:   2015-05-20 09:16:35
# @Last Modified by:   Mehdi-H
# @Last Modified time: 2015-06-04 09:23:42

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

	assert "Login ou mot de passe invalide" not in driver.page_source, "Connexion echouee, mauvais mot de passe ou mauvais identifiant"

	return True

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


# /**
#  * Fonction qui accède à la page des notes de l'annee courante d'un etudiant et les renvoie au format html.
#  */
def fetch_grades_html():
	driver = mypkg.getOrCreateWebdriver()

	# Identification du bouton vers les notes, et click
	assert "Mes Notes" in driver.page_source, "Le bouton vers Mes Notes n'existe peut-etre plus sur Aurion."

	notes = driver.find_element_by_link_text('Mes Notes')
	notes.click()

	# Focus sur le tableau des notes
	elem = driver.find_element_by_id("form:scrollTable")
	# On recupère ensuite un code source purge de tous les attributs html qui alourdissent le code et ralentiront son parsing.
	source_code = parsing_bs4.clean_html(BeautifulSoup(elem.get_attribute('innerHTML'))).prettify().encode('utf-8')

	return source_code

# /**
#  * Fonction qui accède à la page des notes des années précédentes et les renvoie au format html pour parsing.
#  */
def fetch_old_grades_html():
	driver = mypkg.getOrCreateWebdriver()

	assert "Archives" in driver.page_source, "Le bouton vers les Archives n existe peut-etre plus sur Aurion."

	# archives = driver.find_element_by_xpath('//*[@id="form:Sidebar:sidebar"]/div[2]/div[2]/div')
	archives = driver.find_element_by_xpath("(html/body[@class='templatePrincipal']/form[@id='form']/div[@class='cadreMiseEnPage']/div[@class='sidebar']/div[@class='box']/div[@class='body']/div)[2]")
	hover = ActionChains(driver).move_to_element(archives)#.click(archives)
	hover.perform()

	driver.save_screenshot('out.png')

	#notes_archives = driver.find_element_by_xpath('//*[@id="form:Sidebar:j_idt78"]/span[2]')
	notes_archives = driver.find_element_by_xpath("(html/body[@class='templatePrincipal']/form[@id='form']/div[@class='cadreMiseEnPage']/div[@class='sidebar']/div[@class='box']/div[@class='body']/div/div[@class='rf-ddm-pos']/div[@class='rf-ddm-lst'])[2]/div[@class='rf-ddm-lst-bg']/div[1]/span")
	notes_archives.click()

	driver.save_screenshot('out.png')

	# Bouton de recherche générale pour afficher toutes les notes des années précédentes
	searchAll = driver.find_element_by_id('form:idSearch')
	searchAll.click()

	driver.save_screenshot('out.png')

	assert "recherche" in driver.page_source, "L acces aux notes d'archives a peut-etre echoue"

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

	return html

# /**
#  * Fonction qui accède à la page des absences de l'annee courante d'un etudiant et les stocke dans un fichier au format html
#  */
def fetch_absences_html():
	driver = mypkg.getOrCreateWebdriver()

	# Identification du bouton vers les notes, et click
	assert "Mes Absences" in driver.page_source, "Le bouton vers \"Mes Absences\" n'existe peut-être plus sur Aurion."
	absences = driver.find_element_by_link_text('Mes Absences')
	absences.click()

	# Focus sur le tableau des notes
	elem = driver.find_element_by_id("form:scrollTable")
	# On recupère ensuite un code source purge de tous les attributs html qui alourdissent le code et ralentiront son parsing.
	source_code = parsing_bs4.clean_html(BeautifulSoup(elem.get_attribute('innerHTML'))).prettify().encode('utf-8')

	return source_code

# /**
#  * Fonction qui accède à la page des absences des années précédentes et les renvoie au format html pour parsing.
#  */
def fetch_old_absences_html():
	driver = mypkg.getOrCreateWebdriver()

	assert "Archives" in driver.page_source, "Le bouton vers les Archives n existe peut-etre plus sur Aurion."

	# archives = driver.find_element_by_xpath('//*[@id="form:Sidebar:sidebar"]/div[2]/div[2]/div')
	archives = driver.find_element_by_xpath("(html/body[@class='templatePrincipal']/form[@id='form']/div[@class='cadreMiseEnPage']/div[@class='sidebar']/div[@class='box']/div[@class='body']/div)[2]")
	hover = ActionChains(driver).move_to_element(archives)#.click(archives)
	hover.perform()

	driver.save_screenshot('out.png')

	#notes_archives = driver.find_element_by_xpath('//*[@id="form:Sidebar:j_idt78"]/span[2]')
	absences_archives = driver.find_element_by_xpath("(html/body[@class='templatePrincipal']/form[@id='form']/div[@class='cadreMiseEnPage']/div[@class='sidebar']/div[@class='box']/div[@class='body']/div/div[@class='rf-ddm-pos']/div[@class='rf-ddm-lst'])[2]/div[@class='rf-ddm-lst-bg']/div[3]/span")
	absences_archives.click()

	driver.save_screenshot('out.png')

	# Bouton de recherche générale pour afficher toutes les notes des années précédentes
	searchAll = driver.find_element_by_id('form:idSearch')
	searchAll.click()

	driver.save_screenshot('out.png')

	assert "recherche" in driver.page_source, "L acces aux notes d'archives a peut-etre echoue."

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

	
	return html