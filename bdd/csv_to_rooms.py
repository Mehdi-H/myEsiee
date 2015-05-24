#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Author: mehdi
# @Date:   2015-05-24 12:48:56
# @Last Modified by:   mehdi
# @Last Modified time: 2015-05-24 12:57:06

import csv

def salles_de_travail(source, destination):

	salles_etudiant = []  # liste où stocker les salles et leurs infos
	types = ['Salles','Amphithéatres','Labo']  # types de salles recherchées

	f = open(destination, 'w')  # fichier où stocker les salles

	with open(source, 'rt',encoding='utf-8') as csvfile:
		spamreader = csv.reader(csvfile, delimiter=',', quotechar='|')
		heading = spamreader.__next__()  # les données d'en-têtes (lieu, pièce, surface, etc..)
		f.write(str(heading)+'\n\n')
		for row in spamreader:
			if row[3] in types:  # on compare la colonne 'fonction' de la salle à un des types recherchés
				f.write(str(row)+'\n')
				salles_etudiant.append(row)
	f.write(str(len(salles_etudiant))+' salles.')  # on finit avec le nombre de salles
	f.close()



if __name__ == '__main__':
	salles_de_travail('surfaces-2013-2014 (copie).csv','salles.txt')