<?php

	error_reporting(E_ALL);
	ini_set('display_errors', 1);
	header('Access-Control-Allow-Origin: *');


	// === Vérifications ===

	function getParam($param_name)
	{
		return isset($_GET[$param_name]) ? $_GET[$param_name] :
			(isset($_POST[$param_name]) ? $_POST[$param_name] : false);
	}

	$lang = getParam("lang");
	$type = getParam("type");

	if (! $lang || ! $type) {
		echo("Erreur : type ou langue non spécifié.");
		exit;
	}


	// === Requête SQL ===

	require_once '../mysql_sync/config.php';

	$db = mysql_connect(DB_HOST, DB_USER, DB_PASSWORD);
	mysql_select_db(DB_DATABASE, $db);

	mysql_set_charset("utf8", $db);
	$req = mysql_query("SELECT * FROM infos WHERE cle='".$type."_".$lang."'")
		or die('Erreur SQL !<br>'.$request.'<br>'.mysql_errno());

	mysql_close($db);

	$broadcast = "";
	while ($data = mysql_fetch_assoc($req)) {
		$broadcast = $data['valeur'];
	}

	// === Affichage du broadcast ===

	echo($broadcast);

 ?>
