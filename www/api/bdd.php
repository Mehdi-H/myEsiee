<?php

	error_reporting(E_ALL);
	ini_set('display_errors', 1);


	// === Vérifications ===

	$function =
		isset($_GET['func']) ? $_GET['func'] :
			(isset($_POST['func']) ? $_POST['func'] : false);

	if (! $function) {
		echo("Erreur : pas de fonction spécifiée.<br/>'".$function."'");
		exit;
	}


	// === Fonctions à appeler ===

	if (strcmp($function, "getHashVersion") == 0)
	{
		require_once '../mysql_sync/config.php';

		$string = "";

		// === Connexion à la BDD ===

		$db = mysql_connect(DB_HOST, DB_USER, DB_PASSWORD);
		mysql_select_db(DB_DATABASE, $db);

		// === Toutes les salles ===

		$sql = "SELECT * FROM salle";
		$req = mysql_query($sql)
			or die('Erreur SQL !<br>'.$sql.'<br>'.mysql_errno());

		while ($data = mysql_fetch_assoc($req)) {
			$string .= join($data);
		}

		// === Tous les profs ===

		$sql = "SELECT * FROM prof";
		$req = mysql_query($sql)
			or die('Erreur SQL !<br>'.$sql.'<br>'.mysql_errno());

		while ($data = mysql_fetch_assoc($req))
		{
			$string .= join($data);
		}

		mysql_close($db);

		echo(hash("sha256", $string));
	}

 ?>
