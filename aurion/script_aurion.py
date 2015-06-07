#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Author: Mehdi-H
# @Date:   2015-06-05 14:35:29
# @Last Modified by:   Mehdi-H
# @Last Modified time: 2015-06-05 15:13:45

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

def aurion_data(func):

	id = fetch_login()

	# On initialise la connexion du navigateur pour Aurion
	config_selenium.init_selenium_browser(urlAurion = "https://aurionprd.esiee.fr")

	# On se connecte avec id et mdp
	if config_selenium.aurion_connection(id["login"],id["password"]) == True:

		# On se rend sur la bonne page
		config_selenium.go_to_data(func)

		# On extrait les notes
		html = config_selenium.data_to_html()
		parsed_data = parsing_bs4.data_parsing(html)
		parsing_bs4.data_to_json(parsed_data,func)

	else:
		print('Connexion echouee, mauvais mot de passe ou mauvais identifiant')
		config_selenium.quit_selenium_browser()
		return False
	# Fin du script
	config_selenium.quit_selenium_browser()
	return True

if __name__ == '__main__':

	if len(sys.argv) < 4:
		print("3 arguments sont attendus : le login ESIEE puis le mot de passe puis la fonction.")
	else:
		if sys.argv[3] == 'grades' or sys.argv[3] == 'absences' or sys.argv[3] == 'old_grades' or sys.argv[3] == 'old_absences':
			aurion_data(sys.argv[3])
		else:
			print(sys.argv[3] + ' : erreur de fonction non reconnue.')