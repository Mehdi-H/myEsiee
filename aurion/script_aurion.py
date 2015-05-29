#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Author: mehdi
# @Date:   2015-05-20 09:16:35
# @Last Modified by:   Mehdi-H
# @Last Modified time: 2015-05-28 18:47:33


import os,sys

import logging
logging.basicConfig(filename='log.txt',
						filemode='a',
						format='%(asctime)s,%(msecs)d %(name)s %(levelname)s %(filename)s:%(lineno)-4d: %(message)s',
						datefmt='%H:%M:%S',
						level=logging.INFO)

import config_selenium
import parsing_bs4


# /**
#  * Fonction qui permet de récupérer les identifiants d'un étudiant en ligne de commande.
#  */
def fetch_login():
	logging.info("script_aurion::fetch_login()")
	if len(sys.argv) < 3:
		print("2 arguments sont attendus : le login ESIEE puis le mot de passe.")
		return False
	else:
		logging.info("Récupération du login et du mot de passe")
		login = sys.argv[1]
		password = sys.argv[2]
		id = {"login" : login, "password" : password}
		return id

if __name__ == '__main__':
	logging.info("=== START ===")
	# On récupère les identifiants passé en paramètre
	id = fetch_login()
	logging.info("=== fetch_login() OK ===")

	# On initialise la connexion du navigateur pour Aurion
	config_selenium.init_selenium_browser(urlAurion = "https://aurionprd.esiee.fr")
	logging.info("=== config_selenium.init_selenium_browser() OK ===")

	# On se connecte avec id et mdp
	config_selenium.aurion_connection(id["login"],id["password"])
	logging.info("=== config_selenium.aurion_connection() OK ===")

	# On extrait les notes
	config_selenium.fetch_grades_html()
	logging.info("=== config_selenium.fetch_grades_html() OK ===")
	parsing_bs4.current_grades_parsing('notes.html')
	logging.info("=== parsing_bs4.current_grades_parsing() OK ===")
	parsing_bs4.grades_to_json('current_grades.pickle')
	logging.info("=== parsing_bs4.grades_to_json() OK ===")

	# Retour à la page d'accueil
	config_selenium.back_to_home_page()
	logging.info("=== config_selenium.back_to_home_page() OK ===")

	# On extrait les absences
	config_selenium.fetch_absences_html()
	logging.info("=== config_selenium.fetch_absences_html() OK ===")
	parsing_bs4.current_absences_parsing('absences.html')
	logging.info("=== parsing_bs4.current_absences_parsing() OK ===")
	parsing_bs4.absences_to_json('current_absences.pickle')
	logging.info("=== parsing_bs4.absences_to_json() OK ===")

	# Fin du script
	config_selenium.quit_selenium_browser()
	os.system('grades.json')
	os.system('absences.json')
	logging.info("=== FINISH ===")
