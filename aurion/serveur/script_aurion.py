#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Author: mehdi
# @Date:   2015-05-20 09:16:35
# @Last Modified by:   Mehdi-H
# @Last Modified time: 2015-05-29 17:17:24


import os,sys
import config_selenium
import parsing_bs4


# /**
#  * Fonction qui permet de récupérer les identifiants d'un étudiant en ligne de commande.
#  */
def fetch_login():
	if len(sys.argv) < 3:
		print("2 arguments sont attendus : le login ESIEE puis le mot de passe.")
		return False
	else:
		login = sys.argv[1]
		password = sys.argv[2]
		id = {"login" : login, "password" : password}
		return id

if __name__ == '__main__':
	print("=== START ===")

	# On récupère les identifiants passé en paramètre
	id = fetch_login()
	print(id)
	print("=== fetch_login() OK ===")

	# On initialise la connexion du navigateur pour Aurion
	config_selenium.init_selenium_browser(urlAurion = "https://aurionprd.esiee.fr")
	print("=== config_selenium.init_selenium_browser() OK ===")

	# On se connecte avec id et mdp
	config_selenium.aurion_connection(id["login"],id["password"])
	print("=== config_selenium.aurion_connection() OK ===")

	# On extrait les notes
	# config_selenium.fetch_grades_html()
	print("=== config_selenium.fetch_grades_html() OK ===")
	parsing_bs4.current_grades_parsing()
	print(parsing_bs4.grades_to_json('current_grades.pickle'))

	# Retour à la page d'accueil
	config_selenium.back_to_home_page()

	# On extrait les absences
	# config_selenium.fetch_absences_html()
	# parsing_bs4.current_absences_parsing('absences.html')
	# parsing_bs4.absences_to_json('current_absences.pickle')

	# Fin du script
	config_selenium.quit_selenium_browser()
	#os.system('grades.json')
	#os.system('absences.json')