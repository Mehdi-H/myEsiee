<?php
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

	$ade = new ADE;

	if (strcmp($function, "rechSalle") == 0)
	{
		echo($ade->rechSalle());
	}


 ?>
