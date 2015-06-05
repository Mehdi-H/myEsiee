#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Author: Mehdi-H
# @Date:   2015-06-05 14:12:28
# @Last Modified by:   Mehdi-H
# @Last Modified time: 2015-06-05 15:16:17

from bs4 import BeautifulSoup

import pickle
import sys
import json
import codecs

import config_selenium


# /**
#  * Fonction qui permet de nettoyer le code HTML de la pléthore d'attributs id/class/data- etc... dans le code source d'aurion.
#  * Le code est allégé et plus lisible pour le debbug.
#  */
def clean_html(soup):
	for tag in soup.findAll(True): 
		tag.attrs = {}
	return soup


# /**
#  * Fonction pour analyser le code source des données extraites.
#  */
def data_parsing(data_html):
	""" parsing_bs4.data_parsing(data_html):
		Analyse du code source HTML des données extraites.
		@data_html: code source html extrait d'Aurion.
	"""

	html     = []  # code source récupéré d'aurion
	ensemble = []  # ensemble des données
	piece    = []  # une partie des données qui sera ajoutée à l'ensemble des données

	html = data_html

	soup = BeautifulSoup(html)  # conversion du html en objet à parser

	for table_ensemble in soup.find_all('table'):  # Dans la liste de toutes les <table>
		for tbody in table_ensemble.find_all('tbody'):  # dans la balise tbody contenue dans la balise table,
			for tr in tbody.find_all('tr'):  # pour chaque ligne du tableau de données
				piece = []	# réinitialisation du tableau de données unitaire
				for td in tr.find_all('td'):  # pour chaque cellule
					if not td.span:  # si la cellule est vide
						piece.append('non renseignée')
					else:
						for span in td.find_all('span'):  # pour chaque balise span qui va contenir une donnée
							piece.append(td.text)  # on récupère une donnée
				ensemble.append(piece)  # une fois la donnée complétée, on l'insère dans la liste de toutes les données
		ensemble.pop()  # la balise contient une dernière ligne vide, on l'enlève

	return ensemble

# /**
#  * Transformation d'une liste de listes de données extraites HTML en liste JSON d'objets.
#  */
def data_to_json(parsed_data, func):
	""" parsing_bs4.data_to_json(parsed_data, func):
		A partir de données parsées sous forme de liste de listes de données,
		produit un fichier parsed_data.json.
		Les données d'en-têtes dépendent du paramètre func: (old_)grades, (old_)absences.
		@parsed_data: liste de listes au retour de la fonction parsing_bs4.data_parsing().
		@func: paramètre de séléction des données d'en-têtes et de nommage du fichier json.
	"""

	headings = {
		'grades'       : ['annee', 'unite', 'libelle', 'note','compensation','credit'],
		'old_grades'   : ['annee', 'unite', 'libelle', 'note','compensation','credit'],
		'absences'     : ['date','creneau','code','unite','intervenant','activite','nb_heures','motif'],
		'old_absences' : ['date','creneau','code','unite','intervenant','activite','nb_heures','motif']
	}

	heading    = headings[func]
	json_list  = []  # future liste JSON
	json_piece = {}  # futur objet JSON

	#grades = current_grades_parsing()  # On récupère la liste de listes de notes

	# Boucle de parcours des notes pour obtenir une liste d'objets JSON
	for j in range(len(parsed_data)):
		for k in range(len(parsed_data[j])):
			if parsed_data[j][k]:  # les méthodes strip() ne s'appliquent pas aux objets nuls si None est choisi pour définir les cellules vides au moment du parsing
				json_piece[heading[k]] = parsed_data[j][k].lstrip('\n').rstrip('\n').strip()
			else:
				json_piece[heading[k]] = parsed_data[j][k]
		json_list.append(json_piece)
		json_piece = {}

	del json_piece
	
	# Ecriture avec le module codecs pour assurer l'encodage UTF-8 de l'objet JSON pour PHP.
	with codecs.open(func+'.json', 'w', 'utf-8') as f:
		f.write(json.dumps(json_list, sort_keys=True, indent=5, separators=(',', ': '), ensure_ascii=False))

	return True