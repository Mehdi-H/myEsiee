#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Author: mehdi
# @Date:   2015-05-20 09:16:35
# @Last Modified by:   Mehdi-H
# @Last Modified time: 2015-05-28 18:17:35


import sys

import logging
logging.basicConfig(filename='log.txt',
						filemode='a',
						format='%(asctime)s,%(msecs)d %(name)s %(levelname)s %(filename)s:%(lineno)-4d: %(message)s',
						datefmt='%H:%M:%S',
						level=logging.INFO)

import config_selenium


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
	config_selenium.init_selenium_browser(urlAurion = "https://aurionprd.esiee.fr")
	# On stocke les notes au format html
	config_selenium.aurion_connection(id["login"],id["password"])
	config_selenium.fetch_grades_html()
	logging.info("=== config_selenium.aurion_connection() OK ===")
	config_selenium.quit_selenium_browser()
	logging.info("=== FINISH ===")
