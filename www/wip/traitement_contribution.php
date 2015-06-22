<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
	<script src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
	<script src="js/bootstrap.min.js" type="text/javascript"></script>
	<script src="js/jquery.watable.js" type="text/javascript" charset="utf-8"></script>
	<link rel='stylesheet' href="css/bootstrap.min.css" />
	<link rel='stylesheet' href='css/watable.css'/>
	
	<?php
	try
	{
		$bdd = new PDO('mysql:host=localhost;dbname=eroom;charset=utf8', 'root', 'MyMVX2', array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));
	}
	catch (Exception $e)
	{
			die('Erreur : ' . $e->getMessage());
	}
	
	$cols = array(
		"idcontribution" => array (
			"index"=> 1,
			"type"=> "number"
		),
		"login" => array (
			"index"=> 2,
			"type"=> "string"
		),
		"type_contribution" => array (
			"index"=> 3,
			"type"=> "string"
		),
		"date_contribution" => array (
			"index"=> 4,
			"type"=> "string"
		),
		"navigateur" => array (
			"index"=> 5,
			"type"=> "string"
		),
		"version_android" => array (
			"index"=> 6,
			"type"=> "string"
		),
		"email" => array (
			"index"=> 7,
			"type"=> "string"
		),
		"location" => array (
			"index"=> 8,
			"type"=> "string"
		),
		"statut" => array (
			"index"=> 9,
			"type"=> "string"
		),
		"contenu" => array (
			"index"=> 10,
			"type"=> "string"
		)
	);
	
	$rows = array();
	
	$reponse = $bdd->query('SELECT * FROM contribution');
	while ($donnees = $reponse->fetch())
	{
		$rows[] = array(
			"idcontribution" => $donnees['idcontribution'],
			"login" => $donnees['login'],
			"type_contribution" => $donnees['type_contribution'],
			"date_contribution" => $donnees['date_contribution'],
			"navigateur" => $donnees['navigateur'],
			"version_android" => $donnees['version_android'],
			"email" => $donnees['email'],
			"location" => $donnees['location'],
			"statut" => $donnees['statut'],
			"contenu" => $donnees['contenu']
		);
	}
	
	$reponse->closeCursor();

	$full_data = array(
		"cols" => $cols,
		"rows" => $rows
	);
	$full_data = json_encode($full_data);
	?>
	</head>

	<body>

		<h1>tablo dé bg</h1>
		<div id="example1" style="width:auto"></div>

		<script type="text/javascript">
			$(document).ready( function() {
					var donnees = <? echo($full_data); ?>;
					$('#example1').WATable({
					  data: donnees
					});
			});			

		</script>
	</body>
</html>
