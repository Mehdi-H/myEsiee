Script de récupération des données Aurion
===================

@Author: mehdi
@Date:   2015-05-20 09:16:35
@Last Modified by:   Mehdi-H
@Last Modified time: 2015-05-22 16:50:56

> **Ce qui marche pour le moment:**

> - Ouvrir le navigateur Firefox ou PhantomJs
> - Accéder à Aurion en paramètrant PhantomJs
> - Se connecter à Aurion 
> - Aller sur la page des notes de l'année en cours
> - Extraire les notes de l'année en cours
> - Parser les notes de l'année en cours (non commité)
> - Aller sur la page des notes archivées mais l'extraction ne marche pas toujours à cause du javascript

Fichier script_aurion.py
-------------

C'est notre fichier **main**. Il s'utilise en ligne de commande comme suit : python3 script_aurion.py login_esiee password_esiee .

Fichier config_selenium.py
-------------

C'est une **bibliothèque** avec les scripts élémentaires de selenium pour Aurion (initialiser le navigateur, aller sur aurion, se connecter, récupérer les notes) .

Les fichiers **générés lors de l'exécution**
-------------
> - Un log.txt 
> - Des fichiers de notes au format .html à parser


**Coming soon**
-------------

> - Une bibliothèque de fonctions pour parser les notes avec **BeautifulSoup**
> - Des scripts avec selenium pour récupérer les **absences**