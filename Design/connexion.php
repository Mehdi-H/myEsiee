<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-03 16:20:43
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-04 21:04:23
 */

if(isset($_POST['login'], $_POST['pwd'])){

	$login = $_POST['login'];
	$pwd = $_POST['pwd'];

	$command = "python3 ". dirname(__FILE__) ."/scripts/python/connection.py " . $login . " " . $pwd . " 2>&1";
	$response = shell_exec($command);
	// $r = json_decode($r);

	$j = array();
	$j["titre"] = "Connexion en cours...";

	if(trim($response) != 'ok'){
		$j["retour"] = "Vous n'avez pas renseigné le bon login.";
	}else{
		$j["retour"] = "Vous êtes bien connecté. Vous allez être redirigé.";

		$cookie_aurion = array(
			'login' => $login,
			'pwd' => $pwd
		);

		$cookie_aurion = json_encode($cookie_aurion);

		setcookie("eroom", $cookie_aurion, time() + (86400 * 30), '');
	}

	// header("Content-Type: application/json; charset=utf-8", true);
	exit(json_encode($j));
	
}

?>

<!DOCTYPE html>
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1.0, user-scalable=no"/>
	<title>MyESIEE - Connexion</title>

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
						<li><a href="#">Notes</a></li>
						<li><a href="#">Absences</a></li>
						<li><a href="#">Appréciations</a></li>
						<li><a href="#">Paramètres</a></li>
						<li><a href="#">Déconnexion</a></li>
					</ul>

					<ul id="nav-mobile" class="side-nav">
						<li><a href="index.html">Salles</a></li>
						<li class="active"><a href="#">Profs</a></li>
						<li><a href="#">Notes</a></li>
						<li><a href="#">Absences</a></li>
						<li><a href="#">Appréciations</a></li>
						<li><a href="#">Paramètres</a></li>
						<li><a href="#">Déconnexion</a></li>
					</ul>
					<a href="#" data-activates="nav-mobile" class="button-collapse"><i class="mdi-navigation-menu"></i></a>
				</div>
			</nav>
		</div>
		
		
		<div class="row default-primary-color z-depth-3 searchbar login">
			<form id="connectionForm" class="col s12" method="POST" action="connexion.php">
				<div class="input-field col s6 offset-s3">
					<input id="first_name" type="text" class="validate white-text" name="login" required>
					<label class="white-text" for="first_name">Login ESIEE</label>
				</div>
				<div class="input-field col s6 offset-s3">
					<input id="password" type="password" class="validate white-text" name="pwd" required>
					<label class="white-text"for="Mot de passe">Mot de passe</label>
				</div>
				<div class="col s6 offset-s3">
					<button class="btn waves-effect waves-light accent-color" type="submit" name="action">Submit
						<i class="mdi-content-send right"></i>
					</button>
				</div>				
			</form>
		</div>

		<!-- Modal Trigger -->
			<!-- <a class="waves-effect waves-light btn modal-trigger" href="#modal1">Modal</a> -->

		<!-- Modal Structure -->
		<div id="modal1" class="modal default-primary-color z-depth-3">
			<div class="modal-content">
				<h4>Connexion en cours...</h4>
				<p></p>
			</div>
			<div class="modal-footer default-primary-color z-depth-3">
				<a href="#" class=" modal-action modal-close waves-effect waves-green btn-flat accent-color">Fermer</a>
			</div>
		</div>
		
		
	</main>
	<!--  Scripts-->  
	<!-- <script src="https://code.jquery.com/jquery-2.1.1.min.js"></script> -->
	<script src="https://code.jquery.com/jquery-latest.min.js" type="text/javascript"></script>
	<script src="js/materialize.js"></script>
	<script src="js/init.js"></script>

	<?php 
		// header("Content-Type: application/json; charset=utf-8", true);
	 ?>
	<script type="text/javascript">
		$(document).ready(function(){

			// $('.modal-trigger').leanModal();

			$("#connectionForm").submit(function(e){

				e.preventDefault();

				var login = $(this).find("input[name=login]").val();
				var pwd = $(this).find("input[name=pwd]").val();
				var url = $(this).attr("action");

				$.ajax({
					method: "POST",
					url: url,
					data: {login:login, pwd:pwd},
					dataType: 'json',
					beforeSend : function(){
						$('#modal1').openModal();
						$("#modal1").find("p").html('<div class="progress"><div class="indeterminate"></div></div>');
					},
					success : function( data ) {
						$("#modal1").find("h4").html(data.titre);
						$("#modal1").find("p").html(data.retour);
						if(data.retour == "Vous êtes bien connecté. Vous allez être redirigé."){
							window.location = "notes.php";
						}
					},
					error : function(xhr, textStatus, error){
						console.log(xhr.statusText);
						console.log(textStatus);
						console.log(error);
					}
				})
				.done(function(){
					// $("#modal1").find('.modal-footer').css('visibility':'visible');
				});

				return false;
			});			
		});
	</script>	
</body>
</html>