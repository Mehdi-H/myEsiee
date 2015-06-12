<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-12 16:23:04
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-12 19:56:24
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
		<div class="navbar-fixed">
			<nav class="dark-primary-color" role="navigation">
				<div class="nav-wrapper container"><a id="logo-container" href="#" class="brand-logo">Logo</a>
					<ul class="right hide-on-med-and-down">
						<li><a href="index.html" >Salles</a></li>
						<li class="active"><a href="#">Profs</a></li>
						<li><a href="notes.html">Notes</a></li>
						<li><a href="absence.html">Absences</a></li>
						<li><a href="appreciation.html">Appréciations</a></li>
						<li><a href="#">Paramètres</a></li>
						<li><a href="#">Déconnexion</a></li>
					</ul>

					<ul id="nav-mobile" class="side-nav">
						<li><a href="index.html" >Salles</a></li>
						<li class="active"><a href="#">Profs</a></li>
						<li><a href="notes.html">Notes</a></li>
						<li><a href="absence.html">Absences</a></li>
						<li><a href="appreciation.html">Appréciations</a></li>
						<li><a href="#">Paramètres</a></li>
						<li><a href="#">Déconnexion</a></li>
					</ul>
					<a href="#" data-activates="nav-mobile" class="button-collapse"><i class="mdi-navigation-menu"></i></a>
				</div>
			</nav>
		</div>

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
		<input type="date" class="datepicker primary-text-color center" placeholder="<?php echo(date('d/m/y')); ?>">
			<div class="center">
				<img class="responsive-img" src="https://mvx2.esiee.fr/api/ade.php?nom=<?php echo($data_prof['nom']);?>&func=dispoProf">
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
	<script>$('.datepicker').pickadate({
    selectMonths: true,
    selectYears: 15});</script>

</body>
</html>
