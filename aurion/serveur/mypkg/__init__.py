#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Author: Mehdi-H
# @Date:   2015-05-28 18:05:45
# @Last Modified by:   Mehdi-H
# @Last Modified time: 2015-05-31 18:05:41

from selenium import webdriver

driver = None

# /**
#  * Fonction qui permet de configurer le navigateur Ghostdriver de la bibliothèque PhantomJS. 
#  Cela paermet de travailler et découper la connexion en étapes élémentaires sur un même webdriver global.
#  */
def getOrCreateWebdriver():
    global driver

    # Configuration du webdriver en français pour accéder au Aurion fr.
    webdriver.DesiredCapabilities.PHANTOMJS['phantomjs.page.customHeaders.Accept-Language'] = 'fr-FR'

    # Instanciation du webdriver avec les paramètres nécessaires pour avoir la permission d'accéder à Aurion.
    driver = driver or webdriver.PhantomJS(service_args=['--ignore-ssl-errors=true', '--ssl-protocol=tlsv1'])
    return driver