<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-12 16:23:04
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-16 16:42:00
 */
?>

<!DOCTYPE html>
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1.0, user-scalable=no"/>
	<title>MyESIEE - Recherche de professeur</title>

	<!-- CSS  -->
	<link href="css/materialize.css" type="text/css" rel="stylesheet" media="screen,projection"/>
	<link href="css/style.css" type="text/css" rel="stylesheet" media="screen,projection"/>  
	<link href="css/palette.css" type="text/css" rel="stylesheet" media="screen,projection"/>
</head>


<body>
	<main>		
		
		<?php include('includes/header.php'); ?>

		<!-- Recherche d'un professeur--> 
		<div class="row">
			<div class="input-field col s6 offset-s3 searchbar">
				<input id="#prof_list" autocomplete="off" type="text" onkeyup="lv_sort(this);" autofocus>
				<label for="prof_list">Nom du professeur</label>
				<ul class="collection unvisible" id="prof_results"></ul>
			</div>
		</div>
		<?php 
			if(isset($_GET['q'])){

				include('includes/bdd_connect.php');

				$stmt = $conn->prepare('SELECT * FROM prof WHERE nom = "'.$_GET['q'].'"');
				$stmt->execute();
				$data_prof = $stmt->fetch();

				if(!empty($data_prof)){
		?>
		<div class="row z-depth-4 nomprof" id="ficheprof">
			<h4 class="center">
				<?php echo($data_prof['nom']); ?>
			</h4>
			<span class="valign-wrapper">
				<i class="valign icone_prof mdi-action-home"></i>Bureau : <?php echo($data_prof['bureau']); ?>
			</span>
			<span class="valign-wrapper">
				<i class="valign icone_prof mdi-content-mail"></i>Mail : <a href="#"><?php echo($data_prof['email']); ?></a>
			</span>
			<div class="center">
				Disponibilité :
			</div>
		<input id="date_salle" type="date" class="datepicker primary-text-color center" placeholder="<?php echo(date('d/m/y')); ?>">
			<div class="center">
				<img id="edt_salle" class="responsive-img" src="https://mvx2.esiee.fr/api/ade.php?nom=<?php echo($data_prof['nom']);?>&func=dispoProf&date=<?php echo(date("m/d/Y")); ?>">
			</div>
		</div>

		<?php }else{
				header('Location:recherche_professeur.php');
			  }
		}else{
		?>
		<?php
		}
		?>


	</main>
	<!--  Scripts-->

	<?php include_once('includes/scripts_js.php'); ?>
	<script src="scripts/js/date.js"></script>

</body>
</html>
