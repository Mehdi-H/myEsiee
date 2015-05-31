#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Author: mehdi
# @Date:   2015-05-20 09:16:35
# @Last Modified by:   Mehdi-H
# @Last Modified time: 2015-05-31 17:57:51

from selenium import webdriver
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
#  * Fonction qui accède à la page des notes de l'annee courante d'un etudiant et les stocke dans un fichier au format html
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