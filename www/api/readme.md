# E-Room

Projet de fin de 3ème année à ESIEE Paris.
Mai-juin 2015.

* APRAHAMIAN Kaïl
* DUPONT Léo
* HOUACINE Mehdi
* KUHN Johann

# Utilisation de l'API custom ADE
## Utilisation de la fonction `rechSalle`

Cette fonction permet d'obtenir une liste de salles répondant à certains critères optionnels ainsi que leur disponibilité.

La réponse au format JSON est de cette forme :

`[{"5004":"45"}, ...]`

Où `"5004"` est le nom de la salle et `"43"` la disponibilité.

### Format de l'URL et critères de recherche

Une requête peut être de la forme :

`https://mvx2.esiee.fr/api/ade.php?func=rechSalle&nom=5004type=IT&taille=46&projecteur=0&tableau=1&imprimante=0`

Voici la liste des paramètres possibles :

- `func=rechSalle` : pour utiliser la fonction de recherche de salles (seul paramètre obligatoire).
- `nom` : le nom complet de la salle en BDD, si le nom contient des symboles spéciaux (comme "+", " " ou "!"), celui-ci doit être encodé pour URL (url_encode). Si au moins un des paramètres `epi` ou `etage` est spécifié, le paramètre `nom` ne sera pas pris en compte.
- `type` : le type de salle recherché.
- `taille` : Peut prendre les valeurs `S`, `M` ou `L` (majuscule ou minuscule). Correspond à la taille de la salle, respectivement petite, moyenne et grande.
- `projecteur` : la présence d'un projecteur (0 : non, 1 : oui).
- `tableau` : la présence de tableau(x) (0 : aucun, 1 : blanc, 2 : noir, 3 : les deux).
- `imprimante` : la présence d'une imprimante (0 : non, 1 : oui).
- `epi` : l'épi de la salle (correspond simplement au tout premier chiffre des noms des salles).
- `etage` : l'étage de la salle (correspond simplement au deuxième chiffre des noms des salles).

### Format de la disponibilité d'une salle

La disponibilité d'une salle peut prendre ces valeurs :

- `-1` si la salle n'est pas disponible actuellement
- `0` si la salle est disponible jusqu'à la fin de la journée
- un autre entier correspondant au nombre de minutes durant lesquelles la salle est libre

Par exemple, si à 14h15, une salle a une disponibilité de 45, cela signifie qu'elle est actuellement libre mais qu'elle sera occupée à 15h00.

## Utilisation de la fonction `dispoSalle`

Cette fonction permet d'obtenir une image au format GIF de l'emploi du temps d'une salle à un jour donné.

### Format de l'URL et paramètres

Une requête peut être de la forme :

`https://mvx2.esiee.fr/api/ade.php?func=dispoSalle&nom=5004&date=06/15/2015`

Les paramètres `func=dispoSalle` et `nom` sont obligatoires. Le format du nom est le même que pour la fonction `rechSalle`.

Le paramètre `date` correspond à la date du jour souhaité au format américain "mm/jj/aaaa" (exemple : 15/06/2015 pour le 15 juin 2015). Il est optionnel ; s'il est omis, la date d'aujourd'hui sera utilisée.

Les paramètres `largeur` et `hauteur` correspondent aux dimensions en pixels de l'image à générer.

## Utilisation de la fonction `dispoProf`

Cette fonction permet d'obtenir une image au format GIF de l'emploi du temps d'un professeur à un jour donné.

### Format de l'URL et paramètres

Une requête peut être de la forme :

`https://mvx2.esiee.fr/api/ade.php?func=dispoProf&nom=HABIB%20E.&date=06/08/2015`

Les paramètres `func=dispoProf` et `nom` sont obligatoires. Le nom correspond au nom du professeur enregistré dans la BDD (exemple : HABIB E.) et doit être encodé pour les URL (donc HABIB%20E.).

Le paramètre `date` est le même que pour la fonction `dispoSalle`.

Les paramètres `largeur` et `hauteur` correspondent aux dimensions en pixels de l'image à générer.
