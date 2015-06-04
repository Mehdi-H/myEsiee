<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-04 18:58:24
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-04 21:21:08
 */
require_once('cookies.php');

$ch = curl_init();

curl_setopt_array($ch, array(
	CURLOPT_RETURNTRANSFER => 1,
	CURLOPT_URL => 'https://mvx2.esiee.fr/api/aurion.php',
	CURLOPT_USERAGENT => 'E-Room pour ESIEE Paris (projet E3E 2015)',
	CURLOPT_FOLLOWLOCATION => 1,
	CURLOPT_SSL_VERIFYPEER => false,
	CURLOPT_POSTFIELDS => array(
	    'func' => 'old_grades',
	    'login' => $login,
	    'pwd' => $pwd
    	)
	)
);

$out = curl_exec($ch); // Exécution
curl_close($ch); // Fermeture

$data = json_decode($out);

?>

<!DOCTYPE html>
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1.0, user-scalable=no"/>
	<title>MyESIEE - Notes</title>

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
						<li><a href="Recherche_Professeur.html">Profs</a></li>
						<li class="active"><a href="#">Notes</a></li>
						<li><a href="absence.html">Absences</a></li>
						<li><a href="appreciation.html">Appréciations</a></li>
						<li><a href="#">Paramètres</a></li>
						<li><a href="#">Déconnexion</a></li>
					</ul>

					<ul id="nav-mobile" class="side-nav">
						<li><a href="index.html">Salles</a></li>
						<li><a href="Recherche_Professeur.html">Profs</a></li>
						<li class="active"><a href="notes.html">Notes</a></li>
						<li><a href="absence.html">Absences</a></li>
						<li><a href="appreciation.html">Appréciations</a></li>
						<li><a href="#">Paramètres</a></li>
						<li><a href="#">Déconnexion</a></li>
					</ul>
					<a href="#" data-activates="nav-mobile" class="button-collapse"><i class="mdi-navigation-menu"></i></a>
				</div>
			</nav>
		</div>
		
		
		<div class="row center bouton_notes">
			<a href="https://mvx2.esiee.fr/wip/notes.php"class="waves-effect waves-light btn accent-color taille_bouton" id="header" onclick="load()">Cette année</a>
			<a href="https://mvx2.esiee.fr/wip/notes_archivees.php"class="waves-effect waves-light btn accent-color taille_bouton" onclick="load()">Archives</a>
		</div>

		<ul class="collection">
			<?php 
				foreach ($data as $indexInArray => $gradeArray) {
					// echo($gradeArray->{'annee'});
			?>
			<li class="collection-item avatar taille_ligne">
				<?php 
					if(trim($gradeArray->{'note'}) == 'F' || trim($gradeArray->{'note'}) == 'FX'){
						echo('<i class=" white circle red-text text-darken-1 grade">');
					}else{
						echo('<i class=" white circle green-text grade ">');
					}
				?>
				<?php echo(str_replace('X','x',$gradeArray->{'note'})); ?></i>
				<span class="title "><?php echo($gradeArray->{'libelle'}); ?></span>
				<p><?php echo($gradeArray->{'unite'}); ?><br>
					Crédits : <?php echo($gradeArray->{'credit'}); ?>
				</p>
			</li>
			<?php
				}
			 ?>
		</ul>
	</main>

	<!--  Scripts-->
	
	<script src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
	<script src="js/materialize.js"></script>
	<script src="js/init.js"></script>

	<script type="text/javascript">
	function load(){
		$('.bouton_notes').append('<div class="progress"><div class="indeterminate"></div></div>');
	}
	</script>
	
</body>
</html>