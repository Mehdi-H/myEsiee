<?php

	error_reporting(E_ALL);
	ini_set('display_errors', 1);
	header('Access-Control-Allow-Origin: *');


	// === Vérifications ===

	$function =
		isset($_GET['func']) ? $_GET['func'] :
			(isset($_POST['func']) ? $_POST['func'] : false);

	if (! $function) {
		echo("Erreur : pas de fonction spécifiée.<br/>'".$function."'");
		exit;
	}


	// === Imports ===

	require 'class_ADE.php';


	// === Fonction à appeler ===

	$ade = new ADE(4);

	if (strcmp($function, "rechSalle") == 0)
	{
		$ade->rechSalle();
	}
	elseif (strcmp($function, "dispoSalle") == 0)
	{
		$ade->dispo("salle");
	}
	elseif (strcmp($function, "dispoProf") == 0) {
		$ade->dispo("prof");
	}

 ?>
