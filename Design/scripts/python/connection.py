#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Author: Mehdi-H
# @Date:   2015-06-04 13:33:25
# @Last Modified by:   Mehdi-H
# @Last Modified time: 2015-06-04 14:47:36

import sys
from selenium import webdriver
import json
# /**
#  * Fonction qui permet de récupérer les identifiants d'un étudiant en ligne de commande.
#  */
def fetch_login():
	login = sys.argv[1]
	password = sys.argv[2]
	id = {"login" : login, "password" : password}
	return id

def authentification_aurion(l,pwd):

	# Configuration du webdriver en français pour accéder au Aurion fr.
	webdriver.DesiredCapabilities.PHANTOMJS['phantomjs.page.customHeaders.Accept-Language'] = 'fr-FR'

	# Instanciation du webdriver avec les paramètres nécessaires pour avoir la permission d'accéder à Aurion.
	driver = webdriver.PhantomJS(service_args=['--ignore-ssl-errors=true', '--ssl-protocol=tlsv1'])
	driver.set_window_size(1124, 850)

	# Identification des elements de la page à remplir
	driver.get("https://aurionprd.esiee.fr")
	form = driver.find_element_by_class_name('form')
	loginBar = driver.find_element_by_id('j_username')
	passwordBar = driver.find_element_by_id('j_password')

	# Remplissage du formulaire et submit
	loginBar.send_keys(l)
	passwordBar.send_keys(pwd)
	form.submit()

	if "Mon compte" in driver.page_source:
		print('ok')
	else:
		print('no')

if __name__ == '__main__':
	id = fetch_login()
	authentification_aurion(id["login"],id["password"])