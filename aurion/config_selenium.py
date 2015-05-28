#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Author: mehdi
# @Date:   2015-05-20 09:16:35
# @Last Modified by:   Mehdi
# @Last Modified time: 2015-05-28 13:30:56

from selenium import webdriver
from bs4 import BeautifulSoup
import parsing_bs4

import logging
logging.basicConfig(filename='log.txt',
						filemode='a',
						format='%(asctime)s,%(msecs)d %(name)s %(levelname)s %(filename)s:%(lineno)-4d: %(message)s',
						datefmt='%H:%M:%S',
						level=logging.INFO)


# /**
#  * Fonction qui initialise le webdriver selenium avec PhantomJS, avec les bons arguments, 
#  * et qui vérifie qu'Aurion est accessible à "https://aurionprd.esiee.fr".
#  */
def init_selenium_browser(urlAurion = "https://aurionprd.esiee.fr"):
	logging.info("script_aurion::config_selenium::init_selenium_browser()")
	# Config webbrowser
	logging.info("Configuration de selenium avec PhantomJS")
	# driver = webdriver.PhantomJS(service_args=['--ignore-ssl-errors=true', '--ssl-protocol=tlsv1'])
	driver = webdriver.Firefox()	#  Phase de test : utilisation de Firefox pour voir ce qu'il se passe
	logging.info("Navigateur configuré")

	# Tentative accès aurionprd.esiee.fr
	driver.get(urlAurion)
	logging.info("Tentative d'accès à Aurion")
	assert "Page de connexion" in driver.title, "'Page de connexion' devrait apparaître dans le <title> tag de la page HTML, le site a peut-etre changé."
	logging.info("Accès à Aurion")
	return driver

# /**
#  * A finir (récup du driver courant..)
#  */
def quit_selenium_browser():
	driver.quit()

# /**
#  * Fonction qui accède au formulaire de connexion de la page, le remplit, et l'envoie.
#  */
def aurion_connection(l, pwd):
	logging.info("script_aurion::config_selenium::aurion_connection()")
	# Initialisation selenium
	driver = init_selenium_browser()

	# Identification des éléments de la page à remplir
	form = driver.find_element_by_class_name('form')
	logging.info("Formulaire de connexion accédé")
	loginBar = driver.find_element_by_id('j_username')
	logging.info("Barre d'identifiant accédé")
	passwordBar = driver.find_element_by_id('j_password')
	logging.info("Barre de mot de passe accédé")

	# Remplissage du formulaire et submit
	loginBar.send_keys(l)
	logging.info("Identifiant rentré")
	passwordBar.send_keys(pwd)
	logging.info("Mot de passe rentré")
	form.submit()
	logging.info("Formulaire de connexion envoyé")

	assert "Login ou mot de passe invalide" not in driver.page_source, "Connexion échouée, mauvais mot de passe ou mauvais identifiant"
	return driver

# /**
#  * Fonction qui accède à la page des notes de l'année courante d'un étudiant et les stocke dans un fichier au format html
#  */
def fetch_grades_html(l,pwd):
	logging.info("script_aurion::config_selenium::fetch_grades_html()")
	driver = aurion_connection(l,pwd)

	# Identification du bouton vers les notes, et click
	assert "Mes Notes" in driver.page_source, "Le bouton vers \"Mes Notes\" n'existe peut-être plus sur Aurion."
	notes = driver.find_element_by_link_text('Mes Notes')
	notes.click()

	# Focus sur le tableau des notes
	elem = driver.find_element_by_id("form:scrollTable")
	# On récupère ensuite un code source purgé de tous les attributs html qui alourdissent le code et ralentiront son parsing.
	source_code = str(parsing_bs4.clean_html(BeautifulSoup(elem.get_attribute('innerHTML'))).prettify())

	# Stockage des notes dans un fichier notes.html
	f = open('notes.html', 'w')
	f.write(source_code)
	f.close()


# if __name__ == '__main__':
#     if(init_selenium_browser()):
#     	aurion_connection()