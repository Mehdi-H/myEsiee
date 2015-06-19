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
	} else {
		// Préparation de la requête :
		require_once '../mysql_sync/config.php';

		$db = mysql_connect(DB_HOST, DB_USER, DB_PASSWORD);
		mysql_select_db(DB_DATABASE, $db);
	}


	// === Fonctions à appeler ===

	if (strcmp($function, "getHashVersion") == 0)
	{
		$string = "";

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

		echo(hash("sha256", $string));
	}
	elseif (strcmp($function, "getLastUpdate") == 0)
	{
		$sql = "SELECT * FROM infos WHERE cle='db_last_update'";
		$req = mysql_query($sql)
			or die('Erreur SQL !<br>'.$sql.'<br>'.mysql_errno());

		$last_update = "";
		while ($data = mysql_fetch_assoc($req))
		{
			$last_update = $data['valeur'];
		}

		echo($last_update);
	}


	mysql_close($db);
 ?>
