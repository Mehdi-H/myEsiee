<?php
		if (isset($_GET['resource'])
			&& isset($_GET['date'])
			&& isset($_GET['heure']))
		{
			$resource = $_GET['resource'];
			$date = $_GET['date'];
			$heure = $_GET['heure'];
		}
		else { ?>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="fr" xml:lang="fr">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

	<title>Script ADE</title>
	<link href="http://static.alwaysdata.com/v3/images/favicon.ico" rel="shortcut icon" />

	<!-- CSS -->
	<link href="css/style.css" rel="stylesheet" type="text/css">
</head>

<body>
	<p>Spécifiez la ressource (genre 659 pour la salle 5201V...), la date (format jj/mm/aaaa) et l'heure à vérifier (format hh:mm)</p>
	<p>Exemple : <pre>https://mvx2.esiee.fr/api/ade.php?resource=659&date=05/05/2015&heure=09:00</pre> (la salle est occupée)</p>
	<p>Exemple : <pre>https://mvx2.esiee.fr/api/ade.php?resource=659&date=05/05/2015&heure=13:30</pre> (la salle est libre)</p>
</body>
</html>

		<?
			exit;
		}

		/////////////////////////////////////////////////////////////////////////////////////////
		/// FONCTIONS
		/////////////////////////////////////////////////////////////////////////////////////////

		function httpGet($url)
		{
			echo("Requête : " . $url);

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
			return new SimpleXMLElement($out);
		}

		/////////////////////////////////////////////////////////////////////////////////////////
		/// Connexion à ADE
		/////////////////////////////////////////////////////////////////////////////////////////

		// === Se connecter à une session ===

		$req = httpGet("https://planif.esiee.fr:8443/jsp/webapi"
			."?function=connect"
			."&login=lecteur1&password=");

		//$req_xml = new SimpleXMLElement($req);
		$sessionId = $req['id'];

		// === Se placer sur le projet 4 (EDT 2014-2015) ===

		httpGet("https://planif.esiee.fr:8443/jsp/webapi"
			."?sessionId=".$sessionId
			."&function=setProject"
			."&projectId=4");

		/////////////////////////////////////////////////////////////////////////////////////////
		/// Afficher les horaires où la salle 5201V est occupée
		/// - ID de ressource de la salle 5201V : 659
		/////////////////////////////////////////////////////////////////////////////////////////

		// === Récupérer toutes les activités utilisant la salle ===

		$req = httpGet("https://planif.esiee.fr:8443/jsp/webapi"
			."?sessionId=".$sessionId
			."&function=getEvents"
			."&detail=0"
			."&resources=659" // salle 5201V
			."&date=".$date
		);

		// === Afficher tous les horaires où elle est utilisée ===

		$horaires = array();

		foreach ($req->xpath("//event") as $event)
		{
			$start = "".$event['startHour'];
			$end = "".$event['endHour'];

			// Ajouter les horaires de l'event à cette date :
			$horaires[] = array($start, $end);
		}

		echo("<pre>");
		print_r($horaires);
		echo("</pre>");

		// === Vérifier si la salle est libre ===

		$libre = true;
		$until = "";

		foreach ($horaires as $event)
		{
			$start = $event[0];
			$end = $event[1];
			$h_demande = $heure;

			// Date to integer :
			str_replace(":", "", $start);
			str_replace(":", "", $end);
			str_replace(":", "", $h_demande);

			// Comparer heure demandée :
			if ($start <= $h_demande && $h_demande < $end) {
				// Salle occupée :
				$libre = false;
				$until = $event[1];
			}
		}

		// Verdict :

		if ($libre) {
			echo("<p>La salle est libre !</p>");
		}
		else {
			echo("<p>La salle est occupée jusqu'à ".$until."</p>");
		}

		/////////////////////////////////////////////////////////////////////////////////////////
		/// Déconnexion d'ADE
		/////////////////////////////////////////////////////////////////////////////////////////

		httpGet("https://planif.esiee.fr:8443/jsp/webapi"
			."?sessionId=".$sessionId
			."&function=disconnect");
 ?>
