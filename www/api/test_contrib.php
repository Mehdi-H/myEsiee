<?php

	$data = print_r($_POST, true);

	require_once '../mysql_sync/config.php';

	// Connexion à la BDD :
	$db = mysql_connect(DB_HOST, DB_USER, DB_PASSWORD);
	mysql_select_db(DB_DATABASE, $db);

	// Exécution :
	mysql_set_charset("utf8", $db);
	$sql = "INSERT INTO test_contrib VALUES ('". addslashes($data) ."')";
	$req = mysql_query($sql)
		or die('Erreur SQL !<br>'.$sql.'<br>'.mysql_errno());

	mysql_close($db);

 ?>