#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Author: mehdi
# @Date:   2015-05-20 09:16:35
# @Last Modified by:   Mehdi-H
# @Last Modified time: 2015-06-04 09:12:44


import os,sys
import config_selenium
import parsing_bs4


# /**
#  * Fonction qui permet de récupérer les identifiants d'un étudiant en ligne de commande.
#  */
def fetch_login():
	login = sys.argv[1]
	password = sys.argv[2]
	id = {"login" : login, "password" : password}
	return id

def aurion_grades():

	id = fetch_login()

	# On initialise la connexion du navigateur pour Aurion
	config_selenium.init_selenium_browser(urlAurion = "https://aurionprd.esiee.fr")

	# On se connecte avec id et mdp
	config_selenium.aurion_connection(id["login"],id["password"])

	# On extrait les notes
	parsing_bs4.grades_to_json(parsing_bs4.current_grades_parsing())

	# Fin du script
	config_selenium.quit_selenium_browser()

def old_aurion_grades():

	id = fetch_login()

	# On initialise la connexion du navigateur pour Aurion
	config_selenium.init_selenium_browser(urlAurion = "https://aurionprd.esiee.fr")

	# On se connecte avec id et mdp
	config_selenium.aurion_connection(id["login"],id["password"])

	# On extrait les notes
	parsing_bs4.grades_to_json(parsing_bs4.old_grades_parsing())

	# Fin du script
	config_selenium.quit_selenium_browser()

def aurion_absences():

	id = fetch_login()

	# On initialise la connexion du navigateur pour Aurion
	config_selenium.init_selenium_browser(urlAurion = "https://aurionprd.esiee.fr")

	# On se connecte avec id et mdp
	config_selenium.aurion_connection(id["login"],id["password"])

	# On extrait les absences
	parsing_bs4.absences_to_json(parsing_bs4.current_absences_parsing())

	# Fin du script
	config_selenium.quit_selenium_browser()

def old_aurion_absences():

	id = fetch_login()

	# On initialise la connexion du navigateur pour Aurion
	config_selenium.init_selenium_browser(urlAurion = "https://aurionprd.esiee.fr")

	# On se connecte avec id et mdp
	config_selenium.aurion_connection(id["login"],id["password"])

	# On extrait les notes
	parsing_bs4.absences_to_json(parsing_bs4.old_absences_parsing())

	# Fin du script
	config_selenium.quit_selenium_browser()

if __name__ == '__main__':

	if len(sys.argv) < 4:
		print("3 arguments sont attendus : le login ESIEE puis le mot de passe puis la fonction.")
	else:
		if sys.argv[3] == 'grades':
			aurion_grades()
		elif sys.argv[3] == 'absences':
			aurion_absences()
		elif sys.argv[3] == 'old_grades':
			old_aurion_grades()
		elif sys.argv[3] == 'old_absences':
			old_aurion_absences()
		else:
			print(sys.argv[3] + ' : erreur de fonction non reconnue.')