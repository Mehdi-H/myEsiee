Script de récupération des données Aurion
===================

@Author: mehdi
@Date:   2015-05-20 09:16:35
@Last Modified by:   Mehdi-H
@Last Modified time: 2015-05-23 11:47:29

> **Ce qui marche pour le moment:**

> - Ouvrir le navigateur Firefox ou PhantomJs
> - Accéder à Aurion en paramètrant PhantomJs
> - Se connecter à Aurion 
> - Aller sur la page des notes de l'année en cours
> - Extraire les notes de l'année en cours
> - Parser les notes de l'année en cours
> - Aller sur la page des notes archivées mais l'extraction ne marche pas toujours à cause du javascript

Fichier script_aurion.py
-------------

C'est notre fichier **main**. Il s'utilise en ligne de commande comme suit : **python3 script_aurion.py login_esiee password_esiee** .

Fichier config_selenium.py
-------------

C'est une **bibliothèque** avec les scripts élémentaires de **Selenium** pour Aurion (initialiser le navigateur, aller sur aurion, se connecter, récupérer les notes) .

Fichier parsing_bs4.py
-------------

C'est une **bibliothèque** avec les scripts de **BeautilfulSoup** pour parser le contenu html extrait d'Aurion (récupérer dans un format de données Python les notes par exemple). On peut le lancer avec **python3 parsing_bs4.py** pour effectuer le parsing et afficher les données d'en-têtes et les notes.

Les fichiers **générés lors de l'exécution**
-------------
> - Un log.txt
> - Des fichiers de notes au format .html à parser
> - Le fichier current_grades.pickle contenant des données en Python sur les notes de l'année en cours (un premier load permet de récupérer une liste de données d'en-tête (année, code unité, libellé), un second load permet de récupérer une liste de listes de notes)

Les fichiers **pour les tests**
-------------
> - Le fichier notes0.html contient mes notes extrait au format html sur Aurion en mai 2015 pour tester le script de parsing

**Coming soon**
-------------

> - Des scripts avec selenium pour récupérer les **absences**