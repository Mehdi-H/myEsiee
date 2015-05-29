#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Author: mehdi
# @Date:   2015-05-23 11:10:28
# @Last Modified by:   Mehdi-H
# @Last Modified time: 2015-05-29 17:17:13

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
#  * Analyse du code source extrait d'aurion afin d'en extraire les notes.
#  */
def current_grades_parsing():

	html = []  # code source récupéré d'aurion
	heading = []  # données d'en-tête : Année, nom d'unité, libellé, etc...
	grades = []  # ensemble des notes
	grade = []  # une note qui sera ajoutée à l'ensemble des notes

	html = config_selenium.fetch_grades_html()

	soup = BeautifulSoup(html)  # conversion du html en objet à parser

	table_grades = soup.find('table')  # On identifie dans la soupe le table tag #d'id form:dataTableFavori avec toutes les notes et l'en-tête
	
	for th in table_grades.find_all('th'):  # pour chaque balise th de l'en-tête,
		heading.append(th.text.rstrip())  # on récupère le contenu textuel débarassé d'espaces possibles à sa droite (ex: "Année      " au lieu de "Année")
	#heading = [info.lower().replace('é','e').replace(' ','_') for info in heading]  # on réarrange l'en-tête si besoin

	for tbody in table_grades.find_all('tbody'):  # dans la balise tbody contenue dans la balise table,
		for tr in tbody.find_all('tr'):  # pour chaque ligne du tableau de notes
			grade = []	# réinitialisation du tableau de note unitaire
			for td in tr.find_all('td'):  # pour chaque cellule
				if not td.text:  # on détecte ainsi les cellules vides sur aurion si elles n'ont pas été remplies par oubli ou qu'elles n'ont pas lieu d'être remplies (ex: compensation)
					grade.append(None)
				else:
					for span in td.find_all('span'):  # pour chaque balise span qui va contenir une donnée qui compose la note courante
						grade.append(span.text)  # on récupère une donnée de la note
			grades.append(grade)  # une fois la note complétée, on l'insère dans la liste de toutes les notes
	grades.pop()  # la balise contient une dernière ligne vide, on l'enlève

	with open('current_grades.pickle', 'wb') as grades_pkl:
		# pickle.dump(heading,grades_pkl)
		pickle.dump(grades,grades_pkl)

	return True

def grades_to_json(file='current_grades.pickle'):

	heading = ['Année', 'Unités', 'Libellé', 'Notes', 'Note compensée', 'Nombre de crédits']
	grades = []
	json_list = []
	grade = {}

	with open(file, 'rb') as grades_pkl:
		# heading = pickle.load(grades_pkl)
		grades = pickle.load(grades_pkl)

	for j in range(len(grades)):
		for k in range(len(grades[j])):
			grade[heading[k%len(heading)]] = grades[j][k].lstrip('\n').rstrip('\n').strip()
		json_list.append(grade)
		grade = {}

	del grade
	
	# with open('grades.json','w') as f:
	# 	f.write(json.dumps(json_list, sort_keys=True, indent=5, separators=(',', ': '), ensure_ascii=False))
	
	with codecs.open('grades.json', 'w', 'utf-8') as f:
		f.write(json.dumps(json_list, sort_keys=True, indent=5, separators=(',', ': '), ensure_ascii=False))

	return True

# /**
#  * Analyse du code source extrait d'aurion afin d'en extraire les notes.
#  */
def current_absences_parsing(file='absences.html'):

	html = []  # code source récupéré d'aurion
	heading = []  # données d'en-tête : Année, nom d'unité, libellé, etc...
	absences = []  # ensemble des notes
	absence = []  # une note qui sera ajoutée à l'ensemble des notes

	with open(file,'r') as f:
		for line in f:
			html.append(line.strip())
		html = ''.join(html)

	soup = BeautifulSoup(html)  # conversion du html en objet à parser

	table_absences = soup.find('table')  # On identifie dans la soupe le table tag #d'id form:dataTableFavori avec toutes les notes et l'en-tête
	
	for th in table_absences.find_all('th'):  # pour chaque balise th de l'en-tête,
		heading.append(th.text.rstrip())  # on récupère le contenu textuel débarassé d'espaces possibles à sa droite (ex: "Année      " au lieu de "Année")
	#heading = [info.lower().replace('é','e').replace(' ','_') for info in heading]  # on réarrange l'en-tête si besoin

	for tbody in table_absences.find_all('tbody'):  # dans la balise tbody contenue dans la balise table,
		for tr in tbody.find_all('tr'):  # pour chaque ligne du tableau de notes
			absence = []	# réinitialisation du tableau de note unitaire
			for td in tr.find_all('td'):  # pour chaque cellule
				if not td.text:  # on détecte ainsi les cellules vides sur aurion si elles n'ont pas été remplies par oubli ou qu'elles n'ont pas lieu d'être remplies (ex: compensation)
					absence.append(None)
				else:
					for span in td.find_all('span'):  # pour chaque balise span qui va contenir une donnée qui compose la note courante
						absence.append(span.text)  # on récupère une donnée de la note
			absences.append(absence)  # une fois la note complétée, on l'insère dans la liste de toutes les notes
	absences.pop()  # la balise contient une dernière ligne vide, on l'enlève

	with open('current_absences.pickle', 'wb') as absences_pkl:
		pickle.dump(heading,absences_pkl)
		pickle.dump(absences,absences_pkl)

	return True

def absences_to_json(file='current_absences.pickle'):

	heading = []
	absences = []
	json_list = []
	absence = {}

	with open(file, 'rb') as absences_pkl:
		heading = pickle.load(absences_pkl)
		absences = pickle.load(absences_pkl)

	for j in range(len(absences)):
		for k in range(len(absences[j])):
			absence[heading[k%len(heading)]] = absences[j][k]
		json_list.append(absence)
		absence = {}

	del absence
	
	with open('absences.json','w') as f:
		f.write(json.dumps(json_list, sort_keys=True, indent=5, separators=(',', ': '), ensure_ascii=False))

	return True

if __name__ == '__main__':
	if current_grades_parsing('notes.html') == True:
		# with open('current_grades.pickle', 'rb') as g:
		# 	heading = pickle.load(g)
		# 	grades = pickle.load(g)
		# print("Liste d'en-tête:", heading)
		# print('\n')
		# print("Liste des notes:")
		# for grade in grades:
		# 	print(grade)
		grades_to_json('current_grades.pickle')
	if current_absences_parsing('absences.html') == True:
		absences_to_json('current_absences.pickle')
