<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-11 13:51:59
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-11 15:21:52
 */

if(isset($_GET['salle'])){

?>
<!doctype html>
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1.0, user-scalable=no"/>
	<title>MyESIEE - Salle</title>

	<!-- CSS  -->
	<link href="css/materialize.css" type="text/css" rel="stylesheet" media="screen,projection"/>
	<link href="css/style.css" type="text/css" rel="stylesheet" media="screen,projection"/>
	<link href="css/palette.css" type="text/css" rel="stylesheet" media="screen,projection"/>
</head>

<body>
	
	<?php 
		$_GET['salle'] = htmlspecialchars($_GET['salle']);
		include('includes/bdd_connect.php'); 

		$stmt = $conn->prepare('SELECT * FROM salle WHERE nom = "'.$_GET['salle'].'"');
		$stmt->execute();
		$data = $stmt->fetch();

		$show_modal = false;
		if($data == NULL){
			$ressource = $_GET['salle'];
			$show_modal = true;
			header("refresh:1;url=index.php");
		}

		// import du tableau des classes pour les icones dans $icons
		// et du tableau $title pour les indications on hover icons
		include('includes/icon_class.php');
		// var_dump($icons); 
		$salle = json_decode(
				file_get_contents(
				'https://mvx2.esiee.fr/api/ade.php?func=rechSalle&nom='.str_replace("+","%2B",$_GET['salle']),TRUE)
			)[0];
	?>	

	<div class="navbar-fixed">
		<nav class="dark-primary-color" role="navigation">
			<div class="nav-wrapper container"><a id="logo-container" href="#" class="brand-logo">Logo</a>
				<ul class="right hide-on-med-and-down">
					<li><a href="index.html" >Salles</a></li>
					<li><a href="Recherche_Professeur.html">Profs</a></li>
					<li><a href="notes.html">Notes</a></li>
					<li><a href="#">Absences</a></li>
					<li><a href="appreciation.html">Appréciations</a></li>
					<li><a href="#">Paramètres</a></li>
					<li><a href="#">Déconnexion</a></li>
				</ul>

				<ul id="nav-mobile" class="side-nav">
					<li><a href="index.html">Salles</a></li>
					<li><a href="Recherche_Professeur.html">Profs</a></li>
					<li><a href="notes.html">Notes</a></li>
					<li class="active"><a href="#">Absences</a></li>
					<li><a href="appreciation.html">Appréciations</a></li>
					<li><a href="#">Paramètres</a></li>
					<li><a href="#">Déconnexion</a></li>
				</ul>
				<a href="#" data-activates="nav-mobile" class="button-collapse"><i class="mdi-navigation-menu"></i></a>
			</div>
		</nav>
	</div>

	<br>
	<div class="row z-depth-4 fichesalle">
		<h4 class="center">
			<?php echo($data['nom']);?>
		</h4>
		<div class="row">
			<?php  // Type de la salle ?>                  
			<div class="col s6">
				<i class="small <?php echo($icons['type'][$data['type']]); ?> primary-text-color"></i>
				<span class="carac primary-text-color"><?php echo($title['type'][$data['type']]); ?></span>
			</div>

			<?php // Taille de la salle ?>
			<?php 
				$t_val = intval($data['taille']);
				$taille = 'moyenne';
				if((0 <= $t_val) && ($t_val < 30)){
					$taille = 'petite';
				}elseif((30 <= $t_val) && ($t_val < 70)){
					$taille = 'moyenne';
				}else{
					$taille = 'grande';
				}
			 ?>
			<div class="col s6">
				<i class="small <?php echo($icons['taille'][$taille]); ?> primary-text-color"></i>
				<span class="carac primary-text-color"><?php echo($title['taille'][$taille]); ?></span>
			</div>

			<?php // Présence d'un projecteur ?>
			<?php
				if($data['projecteur'] == 1){ 
			?>
			<div class="col s6">
				<i class="small <?php echo($icons['projecteur'][$data['projecteur']]); ?> primary-text-color"></i>
				<span class="carac primary-text-color"><?php echo($title['projecteur']); ?></span>
			</div>
			<?php 
				} 
			?>

			<?php // Type de tableau ?>
			<?php 
				if($data['tableau'] != 0 && $data['tableau'] != 4){
			?>
			<div class="col s6">
				<i class="small <?php echo($icons['tableau'][$data['tableau']]); ?> primary-text-color"></i>
				<span class="carac primary-text-color"><?php echo($title['tableau'][$data['tableau']]); ?></span>
			</div>
			<?php
				}
			 ?>

			<?php // Présence d'une imprimante ?>
			<?php
				if($data['imprimante'] == 1){ 
			?>
			<div class="col s6">
				<i class="small <?php echo($icons['imprimante'][$data['imprimante']]); ?> primary-text-color"></i>
				<span class="carac primary-text-color"><?php echo($title['imprimante']); ?></span>
			</div>
			<?php 
				} 
			?>
		</div>
		<div class="row">
			<div class="center">
				<?php
					if($salle->{$data['nom']} === 0){
						echo("Salle libre pour la journée.");
					}elseif($salle->{$data['nom']} === -1){
						echo("Salle actuellement occupée.");
					}else{
						echo("Salle libre pendant ".$salle->{$data['nom']}." minutes.");
					}
				?>
			</div>
		</div>
		<div>	
				<input id="date_salle" type="date" class="datepicker primary-text-color center">
			<div class="center">
				<img class="responsive-img" src="https://mvx2.esiee.fr/api/ade.php?func=dispoSalle&nom=1201%2B">
			</div>
		</div>
	</div>

	<?php include('includes/modal-fiche.php'); ?>

	<script src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
	<script src="js/materialize.js"></script>
	<script src="js/init.js"></script>
	<script> $('.datepicker').pickadate({
    selectMonths: true, // Creates a dropdown to control month
    selectYears: 15 // Creates a dropdown of 15 years to control year
});</script> 

	<?php 
		if($show_modal){
	 ?>
	<script type="text/javascript">
		$('#modal1').openModal();
	</script>
	<?php 
	}
	 ?>

</body>
</html>

<?php 

}else{
	header('Location:index.php');
}
?>
