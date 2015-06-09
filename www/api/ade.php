<?php

	error_reporting(E_ALL);
	ini_set('display_errors', 1);

	// === Vérifications ===

	if (isset($_GET['func'])) {
		$function = $_GET['func'];
	}
	else {
		exit;
	}


	// === Imports ===

	require 'class_ADE.php';


	// === Fonction à appeler ===

	$ade = new ADE(4);

	if (strcmp($function, "rechSalle") == 0)
	{
		echo($ade->rechSalle());
	}
	elseif (strcmp($function, "dispoSalle") == 0)
	{
		$ade->dispo("salle");
	}
	elseif (strcmp($function, "dispoProf") == 0) {
		$ade->dispo("prof");
	}

 ?>
