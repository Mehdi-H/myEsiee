#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Author: mehdi
# @Date:   2015-05-24 12:48:56
# @Last Modified by:   mehdi
# @Last Modified time: 2015-05-25 14:40:16

import csv
import pickle
from salle import *

def save_as_dic(dico,destination):

	with open(destination, 'wb') as handle:
		pickle.dump(dico, handle)

# /**
#  * Fonction pour récupérer toutes les salles et leurs infos depuis l'excel de la planif.
#  */
def salles_de_travail_txt(source='ressources/surfaces-2013-2014 (copie).csv', destination='ressources/salles.txt'):

	salles_etudiant = []  # liste où stocker les salles et leurs infos
	types = ['Salles','Amphithéatres','Labo']  # types de salles recherchées

	f = open('ressources/salles.txt', 'w')  # fichier où stocker les salles

	with open('ressources/surfaces-2013-2014 (copie).csv', 'rt',encoding='utf-8') as csvfile:
		spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
		heading = spamreader.__next__()  # les données d'en-têtes (lieu, pièce, surface, etc..)
		f.write(str(heading)+'\n\n')
		for row in spamreader:
			if row[3] in types:  # on compare la colonne 'fonction' de la salle à un des types recherchés
				f.write(str(row)+'\n')
				salles_etudiant.append(row)
	f.write(str(len(salles_etudiant))+' salles.')  # on finit avec le nombre de salles
	f.close()


# /**
#  * Fonction qui répertorie les salles selon nos besoins en BDD à partir du fichier de la planif.
#  * Le resultat sera a complétée avec les données recueillies sur le terrain.
#  */
def salles_de_travail_planif(source='ressources/surfaces-2013-2014 (copie).csv'):

	salles_etudiant = {}
	types = ['Salles','Amphithéatres','Labo']  # types de salles recherchées

	with open('ressources/surfaces-2013-2014 (copie).csv', 'rt') as csvfile:
		spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
		for row in spamreader:
			if row[3] in types:
				salles_etudiant[row[1]] = Salle(
					nom = row[1],
					resourceID = 0,
					fonction = row[3],
					taille = row[2],
					projecteur = 0,
					tableau = 1,
					imprimante = 2
				)
	return salles_etudiant

# /**
#  * Fonction qui met à jour les salles avec les données recueillies sur le terrain (tableau, projecteur, etc..).
#  */
def salles_avec_donnees(source='ressources/Salles.csv'):

	salles_etudiant = salles_de_travail_planif('ressources/Salles.csv')

	
	replacement = {
		'non' : 0,
		'aucun' : 0,
		'oui' : 1,
		'feutres' : 1,
		'craies' : 2,
		'?' : 4,
		'à vérifier' : 4
	}

	with open(source, 'rt', encoding='utf-8') as csvfile:
		spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
		for row in spamreader:
			for i in range(len(row)):
				if len(row[i]) <1:
					row[i] = '0'
			for k,x in enumerate(row):
				for i,j in replacement.items():
					if x == i:
						row[k] = j
			for k,v in salles_etudiant.items():
				if len(k) == 3:
					tmp = salles_etudiant[k]
					salles_etudiant['0'+k] = tmp
					del salles_etudiant[k]
					salles_etudiant['0'+k].nom = '0'+k
				elif row[0] == k:
					v.set_bpp(row[2],row[3],row[4])

	return salles_etudiant

# /**
#  * Fonction qui met à jour le nom des salles par rapport aux noms d'ADE (ex: '5401V' pour la '5401')
#  */
def changer_noms_salles(source_csv='ressources/Salles.csv', source_txt='ressources/noms_salles_ADE_chiffres.txt', destination='ressources/dic_final_rooms.pickle'):

	salles_etudiant = salles_avec_donnees('ressources/Salles.csv')
	salles_txt = []

	with open('ressources/noms_salles_ADE_chiffres.txt','r')as f:
		salles_txt = f.read().splitlines()

	new_dic = {}

	for k,v in salles_etudiant.items():
		for i in salles_txt:
			if i.startswith(k):
				new_dic[i] = salles_etudiant[k]
				new_dic[i].nom = i
	save_as_dic(new_dic, destination)

	return new_dic

# /**
#  * Fonction de test pour afficher les salles du dictionnaire après les mises à jour sur le terrain et ADE.
#  */
def test_salle(source='ressources/dic_final_rooms.pickle'):
	salles = []
	with open(source, 'rb') as f:
		salles = pickle.load(f)
	f = open('ressources/salles_extraites.txt','w')
	for k,v in salles.items():
		f.write("%s\n" % k)
	f.close()

#changer_noms_salles('ressources/Salles.csv','ressources/noms_salles_ADE_chiffres.txt','ressources/dic_final_rooms.pickle')

if __name__ == '__main__':
	#salles_de_travail_txt('ressources/surfaces-2013-2014 (copie).csv','ressources/salles.txt')
	#salles_de_travail_planif('ressources/surfaces-2013-2014 (copie).csv')
	#salles_avec_donnees('ressources/Salles.csv')
	changer_noms_salles('ressources/Salles.csv','ressources/noms_salles_ADE_chiffres.txt','ressources/dic_final_rooms.pickle')
	test_salle()