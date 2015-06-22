<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-14 16:19:21
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-19 15:41:00
 */
if(isset($_POST['epi'])){
	// var_dump($_POST);
	$url_api = "https://mvx2.esiee.fr/api/ade.php?func=rechSalle";
	$params = array(
		'epi'        => '&epi='.$_POST['epi'], 
		'niveau'     => '&etage='.$_POST['niveau'],
		'taille'	 => '&taille='.$_POST['taille'],
		'projecteur' => '&projecteur=1', 
		'imprimante' => '&imprimante=1'
	);

	foreach ($params as $key => $value) {
		if($_POST[$key] || $_POST[$key] != ''){
			$url_api .= $value;
		}
	}

	if(isset($_POST['craie']) and isset($_POST['feutre'])){
		$url_api .= "&tableau=3";
	}elseif(isset($_POST['craie']) and !isset($_POST['feutre'])){
		$url_api .= "&tableau=2";
	}elseif(!isset($_POST['craie']) and isset($_POST['feutre'])){
		$url_api .= "&tableau=1";
	}

	$salles = json_decode(file_get_contents($url_api));

	exit(json_encode($salles));die();
}
?>

<!DOCTYPE html>
<html lang="fr">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1.0, user-scalable=no"/>
	<title>MyESIEE - Recherche avancée de salle</title>

	<!-- CSS  -->
	<link href="css/materialize.css" type="text/css" rel="stylesheet" media="screen,projection"/>
	<link href="css/style.css" type="text/css" rel="stylesheet" media="screen,projection"/>
	<link href="css/palette.css" type="text/css" rel="stylesheet" media="screen,projection"/>
</head>


<body>
	<main>
		<?php include('includes/header.php'); ?>

		<div class="row center">

			<h2 id="nom_categorie">
				Recherche avancée
			</h2>

		</div>

		<form id="form_recherche_avancee" class="col s12" method="POST" action="#">
			<div class="row">
				<div class="input-field col s4 offset-s2">
					<select name="epi">
						<option value=""> </option>
						<option value="0">Hors épis</option>
						<option value="1">Épi 1</option>
						<option value="2">Épi 2</option>
						<option value="3">Épi 3</option>
						<option value="4">Épi 4</option>
						<option value="5">Épi 5</option>
						<option value="6">Épi 6</option>
					</select>
					<label>Numéro de l'épi</label>
				</div>
				<div class="input-field col s4">
					<select name="niveau">
						<option value=""> </option>
						<option value="0">Niveau 0</option>
						<option value="1">Niveau 1</option>
						<option value="2">Niveau 2</option>
						<option value="3">Niveau 3</option>
						<option value="4">Niveau 4</option>
					</select>
					<label>Niveau</label>
				</div>
			</div>
			<div class="row center">
				<div class="input-field col s4 offset-s4">
					<select name="taille">
						<option value=""> </option>
						<option value="s">Petite</option>
						<option value="m">Moyenne</option>
						<option value="l">Grande</option>
					</select>
					<label>Taille</label>
				</div>
			</div>
			<div class="row">
					<table>
						<tr>
							<td class="right">
								<input name="craie" type="checkbox" id="test1" />
								<label class="taille_checkbox" for="test1">Tableau à craie</label>
							</td>

							<td>
								<input name="feutre" type="checkbox" id="test2" />
								<label class="taille_checkbox" for="test2">Tableau véléda</label>
							</td>
						</tr>
						<tr>
							<td class="right">
								<input name="projecteur" type="checkbox" id="test3" />
								<label class="taille_checkbox" for="test3">Retroprojecteur</label>
							</td>

							<td>
								<input name="imprimante" type="checkbox" id="test4" />
								<label class="taille_checkbox" for="test4">Imprimante</label>
							</td>						
						</tr>
					</table>
			</div>
		</div>
		<div class="row center">
			<button class="btn waves-effect waves-light accent-color " id ="z_index_bouton" type="submit" name="recherche_avancee">Rechercher
				<i class="mdi-content-send right"></i>
			</button>
		</div>
			</form>
		<div class="row center">
			<div class="col s8 offset-s2">
				<ul class="collection" id="salle_list"></ul>
			</div>
		</div>

		</main>

		<!--  Scripts-->

		<script src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
		<script src="js/materialize.js"></script>
		<script src="js/init.js"></script>
		<script>$(".button-collapse").sideNav();</script>
		<script>    
		$(document).ready(function() {
			$('select').material_select();
		});
		</script>
		<script>$('select').material_select('destroy');</script>

		<script src="scripts/js/recherche_avancee.js"></script>


		<script type="text/javascript">

// === Afficher le nom de la catégorie sélectionnée ===

$(".filtre-type").each(function() {
	$(this).click(function() {
		$("#nom_categorie")[0].innerHTML = $(this)[0].title;
	});
});


</script>

</body>
</html>