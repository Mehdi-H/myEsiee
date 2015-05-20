<?php
		if (isset($_GET['func'])) {
			$function = $_GET['func'];
		}
		else {
			exit;
		}

		/////////////////////////////////////////////////////////////////////////////////////////
		/// FONCTIONS PERSOS
		/////////////////////////////////////////////////////////////////////////////////////////

		function httpGet($url)
		{
			// Création de la requête :
			$ch = curl_init();

			curl_setopt_array($ch, array(
				CURLOPT_RETURNTRANSFER => 1,
				CURLOPT_URL => $url,
				CURLOPT_USERAGENT => 'E-Room pour ESIEE Paris (projet E3E 2015)',
				CURLOPT_PROXY => 'https://dupontl:mvx2dupontl@mvproxy.esiee.fr:3128',
				CURLOPT_FOLLOWLOCATION => 1,
				CURLOPT_SSL_VERIFYPEER => false,
			));

			$out = curl_exec($ch); // Exécution
			curl_close($ch); // Fermeture

			//echo('url : '.$url.'<pre>'.htmlspecialchars($out).'</pre>');

			return new SimpleXMLElement($out);
		}

		/////////////////////////////////////////////////////////////////////////////////////////
		/// Préparation (connexion à ADE, choix du projet ADE)
		/////////////////////////////////////////////////////////////////////////////////////////

		// === VARIABLES ===

		$projetADE = 4;

		// === Se connecter à une session ===

		$req = httpGet("https://planif.esiee.fr:8443/jsp/webapi"
			."?function=connect"
			."&login=lecteur1&password=");

		$sessionId = $req['id'];

		// === Se placer sur le projet 4 (EDT 2014-2015) ===

		httpGet("https://planif.esiee.fr:8443/jsp/webapi"
			."?sessionId=".$sessionId
			."&function=setProject"
			."&projectId=".$projetADE);

		/////////////////////////////////////////////////////////////////////////////////////////
		/// FONCTION : Recherche de salles
		/////////////////////////////////////////////////////////////////////////////////////////

		if (strcmp($function, "rechSalle") == 0)
		{
			// === PARAMETRES DE LA RECHERCHE ===

			// Type de salle :
			if (isset($_GET['ty'])) {
				$type = $_GET['ty'];
			}

			// "misc" = "PTIS" (Projecteur [0,1], Tableau [0,1,2,3], Imprimante [0,1], Size [entier])
			// Tableau : [0:aucun, 1:blanc, 2:noir, 3:deux]
			if (isset($_GET['misc'])) {
				$projecteur = substr($_GET['misc'], 0, 1);
				$tableau = substr($_GET['misc'], 1, 1);
				$imprimante = substr($_GET['misc'], 2, 1);
				$taille = substr($_GET['misc'], 3);

				$sql_misc = " AND taille=".$taille
					." AND projecteur=".$projecteur
					." AND tableau=".$tableau
					." AND imprimante=".$imprimante;
			} else {
				$sql_misc = "";
			}

			// === Lister les salles correspondant aux critères (ty et misc) dans la BDD ===

			// Connexion à la BDD :
			$db = mysql_connect('localhost', 'root', 'MyMVX2');
			mysql_select_db('eroom', $db);

			// Requête SQL :
			$sql = "SELECT * FROM salle "
				."WHERE type_salle LIKE '".$type."'".$sql_misc;

			echo($sql.'<br/>');

			// Exécution :
			$req = mysql_query($sql) or die('Erreur SQL !<br>'.$sql.'<br>'.mysql_errno());

			// Stockage :
			$salles = array();
			while ($data = mysql_fetch_assoc($req))
			{
				$salles[$data[nom_salle]] = array(
					"id" => $data[resourceID_salle],
					"type" => $data[type_salle],
					"taille" => $data[taille],
					"projecteur" => $data[projecteur],
					"tableau" => $data[tableau],
					"imprimante" => $data[imprimante],
					"dispo" => -1
				);
			}

			mysql_close();

			// === Date et heure actuelles ===

			$now_date = date("d/m/Y"); // format "20/05/2015"
			$now_date_ADE = date("m/d/Y"); // format "05/20/2015" pour les requêtes ADE
			$now_time = date("H:i"); // format "15:07"

			echo($now_date.' - '.$now_time.'<br/>');

			// === Récupérer la disponibilité de chaque salle ===

			foreach ($salles as $nom_salle => $salle)
			{
				// --- Requête ADE ---

				$req = httpGet("https://planif.esiee.fr:8443/jsp/webapi"
					."?sessionId=".$sessionId
					."&function=getEvents"
					."&detail=0"
					."&resources=".$salle[id]
					."&date=".$now_date_ADE
				);

				// --- Lecture des horaires ---

				$horaires = array();
				foreach ($req->xpath("//event") as $event)
				{
					$start = "".$event['startHour'];
					$end = "".$event['endHour'];

					// Ajouter les horaires de l'event à cette date :
					$horaires[] = array($start, $end);
				}
				// echo('<pre>'); print_r($horaires); echo('</pre>');

				// --- Calcul du temps libre restant ---
				// Pas libre, libre jusqu'à telle heure, libre toute la journée

				$libre = true;
				$pendant = -1;

				foreach ($horaires as $cours)
				{
					$debut = strtotime($cours[0]);
					$fin = strtotime($cours[1]);

					if ($fin < time())
					{
						// Si l'heure de fin est dépassée : on ne s'occupe pas de cette horaire.
						continue;
					}
					elseif (time() < $debut && $libre)
					{
						// Si on est avant l'heure de début : combien de temps on est libre ?
						$pendant = $debut - time();
					}
					elseif ($debut <= time())
					{
						// Si on est après le début d'un cours (et avant la fin grâce au premier IF) : pas libre
						$libre = false;
					}
				}

				if ($libre && $pendant > 0) {
					echo("La salle ".$nom_salle." est libre jusqu'a ".$cours[1].", cad pendant ".$pendant." minutes.<br>");
					$salles[$nom_salle][dispo] = $pendant;
				} elseif ($libre) {
					echo("La salle ".$nom_salle." est libre jusqu'a ce soir.<br>");
					$salles[$nom_salle][dispo] = "full";
				} else {
					echo("La salle ".$nom_salle." n'est pas libre.<br>");
					$salles[$nom_salle][dispo] = 0;
				}

			}
			echo('<pre>'); print_r($salles); echo('</pre>');

			echo('<pre>'.json_encode($salles).'</pre>');

		}

		/////////////////////////////////////////////////////////////////////////////////////////
		/// Déconnexion d'ADE
		/////////////////////////////////////////////////////////////////////////////////////////

		httpGet("https://planif.esiee.fr:8443/jsp/webapi"
			."?sessionId=".$sessionId
			."&function=disconnect");
 ?>
