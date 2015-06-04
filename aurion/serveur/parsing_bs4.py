#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Author: mehdi
# @Date:   2015-05-23 11:10:28
# @Last Modified by:   Mehdi-H
# @Last Modified time: 2015-06-04 09:26:51

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
	grades = []  # ensemble des notes
	grade = []  # une note qui sera ajoutée à l'ensemble des notes

	html = config_selenium.fetch_grades_html()

	soup = BeautifulSoup(html)  # conversion du html en objet à parser

	table_grades = soup.find('table')  # On identifie dans la soupe le table tag #d'id form:dataTableFavori avec toutes les notes et l'en-tête

	for tbody in table_grades.find_all('tbody'):  # dans la balise tbody contenue dans la balise table,
		for tr in tbody.find_all('tr'):  # pour chaque ligne du tableau de notes
			grade = []	# réinitialisation du tableau de note unitaire
			for td in tr.find_all('td'):  # pour chaque cellule
				if not td.span:  # on détecte ainsi les cellules vides sur aurion si elles n'ont pas été remplies par oubli ou si elles n'ont pas lieu d'être remplies (ex: compensation)
					grade.append(None)
				else:
					for span in td.find_all('span'):  # pour chaque balise span qui va contenir une donnée qui compose la note courante
						grade.append(td.text)  # on récupère une donnée de la note
			grades.append(grade)  # une fois la note complétée, on l'insère dans la liste de toutes les notes
	grades.pop()  # la balise contient une dernière ligne vide, on l'enlève

	return grades

# /**
#  * Fonction pour analyser le code source des notes archivees
#  */
def old_grades_parsing():
	html = []  # code source récupéré d'aurion
	grades = []  # ensemble des notes
	grade = []  # une note qui sera ajoutée à l'ensemble des notes

	html = config_selenium.fetch_old_grades_html()

	soup = BeautifulSoup(html)  # conversion du html en objet à parser

	for table_grades in soup.find_all('table'):
		for tbody in table_grades.find_all('tbody'):  # dans la balise tbody contenue dans la balise table,
			for tr in tbody.find_all('tr'):  # pour chaque ligne du tableau de notes
				grade = []	# réinitialisation du tableau de note unitaire
				for td in tr.find_all('td'):  # pour chaque cellule
					if not td.span:  # on détecte ainsi les cellules vides sur aurion si elles n'ont pas été remplies par oubli ou si elles n'ont pas lieu d'être remplies (ex: compensation)
						grade.append(None)
					else:
						for span in td.find_all('span'):  # pour chaque balise span qui va contenir une donnée qui compose la note courante
							grade.append(td.text)  # on récupère une donnée de la note
				grades.append(grade)  # une fois la note complétée, on l'insère dans la liste de toutes les notes
		grades.pop()  # la balise contient une dernière ligne vide, on l'enlève

	return grades

# /**
#  * Transformation d'une liste de listes de notes en liste JSON d'objets de notes
#  */
def grades_to_json(grades):

	heading = ['annee', 'unite', 'libelle', 'note','compensation','credit']
	json_list = []  # future liste JSON
	grade = {}  # futur objet JSON

	#grades = current_grades_parsing()  # On récupère la liste de listes de notes

	# Boucle de parcours des notes pour obtenir une liste d'objets JSON
	for j in range(len(grades)):
		for k in range(len(grades[j])):
			if grades[j][k]:
				grade[heading[k]] = grades[j][k].lstrip('\n').rstrip('\n').strip()
			else:
				grade[heading[k]] = grades[j][k]
		json_list.append(grade)
		grade = {}

	del grade
	
	# Ecriture obligatoire avec le module codecs pour assurer l'encodage UTF-8 de l'objet JSON pour PHP.
	with codecs.open('grades.json', 'w', 'utf-8') as f:
		f.write(json.dumps(json_list, sort_keys=True, indent=5, separators=(',', ': '), ensure_ascii=False))

	return True

# /**
#  * Analyse du code source extrait d'aurion afin d'en extraire les notes.
#  */
def current_absences_parsing():

	html = []  # code source récupéré d'aurion
	absences = []  # ensemble des absences
	absence = []  # une absence (avec ses infos) qui sera ajoutée à l'ensemble des absences
	
	html = config_selenium.fetch_absences_html()

	soup = BeautifulSoup(html)  # conversion du html en objet à parser

	table_absences = soup.find('table')  # On identifie dans la soupe le table tag #d'id form:dataTableFavori avec toutes les notes et l'en-tête

	for tbody in table_absences.find_all('tbody'):  # dans la balise tbody contenue dans la balise table,
		for tr in tbody.find_all('tr'):  # pour chaque ligne du tableau de notes
			absence = []	# réinitialisation du tableau de note unitaire
			for td in tr.find_all('td'):  # pour chaque cellule
				if not td.span:  # on détecte ainsi les cellules vides sur aurion si elles n'ont pas été remplies par oubli ou qu'elles n'ont pas lieu d'être remplies (ex: compensation)
					absence.append(None)
				else:
					for span in td.find_all('span'):  # pour chaque balise span qui va contenir une donnée qui compose la note courante
						absence.append(span.text)  # on récupère une donnée de la note
			absences.append(absence)  # une fois la note complétée, on l'insère dans la liste de toutes les notes
	absences.pop()  # la balise contient une dernière ligne vide, on l'enlève

	return absences

# /**
#  * Analyse du code source extrait d'aurion afin d'en extraire les absences archivees.
#  */
def old_absences_parsing():

	# html = []  # code source récupéré d'aurion
	absences = []  # ensemble des absences
	absence = []  # une absence (avec ses infos) qui sera ajoutée à l'ensemble des absences
	
	html = config_selenium.fetch_old_absences_html()

	soup = BeautifulSoup(html)  # conversion du html en objet à parser

	for table_absences in soup.find_all('table'):  # On identifie dans la soupe le table tag #d'id form:dataTableFavori avec toutes les notes et l'en-tête
		for tbody in table_absences.find_all('tbody'):  # dans la balise tbody contenue dans la balise table,
			for tr in tbody.find_all('tr'):  # pour chaque ligne du tableau de notes
				absence = []	# réinitialisation du tableau de note unitaire
				for td in tr.find_all('td'):  # pour chaque cellule
					if not td.span:  # on détecte ainsi les cellules vides sur aurion si elles n'ont pas été remplies par oubli ou qu'elles n'ont pas lieu d'être remplies (ex: compensation)
						absence.append(None)
					else:
						for span in td.find_all('span'):  # pour chaque balise span qui va contenir une donnée qui compose la note courante
							absence.append(span.text)  # on récupère une donnée de la note
				absences.append(absence)  # une fois la note complétée, on l'insère dans la liste de toutes les notes
		absences.pop()  # la balise contient une dernière ligne vide, on l'enlève

	return absences

def absences_to_json(absences):

	heading = ['date','creneau','code','unite','intervenant','activite','nb_heures','motif']
	json_list = []  # Liste JSON
	absence = {}  # Objet JSON d'absence

	#absences = current_absences_parsing()  # on récupère la liste de listes d'absences

	# Boucle de parcours des absences à JSONifié
	for j in range(len(absences)):
		for k in range(len(absences[j])):
			if absences[j][k]:
				absence[heading[k%len(heading)]] = absences[j][k].lstrip('\n').rstrip('\n').strip()
			else:
				absence[heading[k%len(heading)]] = absences[j][k]
		json_list.append(absence)
		absence = {}

	del absence
	
	# Passage UTF-8
	with codecs.open('absences.json', 'w', 'utf-8') as f:
		f.write(json.dumps(json_list, sort_keys=True, indent=5, separators=(',', ': '), ensure_ascii=False))

	return True


