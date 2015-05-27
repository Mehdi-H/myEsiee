#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Author: mehdi
# @Date:   2015-05-25 12:53:52
# @Last Modified by:   mehdi
# @Last Modified time: 2015-05-25 14:52:23

import pprint as pp

# /**
#  * Une salle de l'esiee
#  */
class Salle:
    
    def __init__(self, nom, fonction, taille, projecteur, tableau, imprimante):
        self.nom = nom  # string
        self.resourceID = id(self)
        self.fonction = fonction  # string
        self.taille = taille  # int
        self.projecteur = projecteur  # int '0:non, 1:oui'
        self.tableau = tableau  # int '0:aucun, 1:blanc, 2:noir, 3:deux'
        self.imprimante = imprimante  # int '0:non, 1:oui'
    
    def display_salle(self):
        pp.pprint(self.__dict__)
    
    def set_board(self,b):
        self.tableau = b
    
    def set_projector(self,p):
        self.projecteur = p
    
    def set_printer(self,i):
        self.imprimante = i
        
    def set_bpp(self,b,p,i):
        self.set_board(b)
        self.set_projector(p)
        self.set_printer(i)