<!DOCTYPE html>
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1.0, user-scalable=no"/>
	<title>MyESIEE - Recherche de salle</title>

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
						<li class="active"><a href="#" >Salles</a></li>
						<li><a href="Recherche_Professeur.html">Profs</a></li>
						<li><a href="notes.html">Notes</a></li>
						<li><a href="absence.html">Absences</a></li>
						<li><a href="appreciation.html">Appréciations</a></li>
						<li><a href="#">Paramètres</a></li>
						<li><a href="#">Déconnexion</a></li>
					</ul>

					<ul id="nav-mobile" class="side-nav">
						<li class="active"><a href="#" >Salles</a></li>
						<li><a href="Recherche_Professeur.html">Profs</a></li>
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

		<div class="row center default-primary-color">
			<!-- Bouton activé : bouton_active-->
			<a class="btn-flat waves-effect waves-light  margin_top_bouton filtre-type " id="info" title="Salle informatique">
				<i class="medium mdi-hardware-desktop-windows primary-text-color">
				</i>
			</a>
			<a class="btn-flat  waves-effect waves-light  margin_top_bouton filtre-type primary-text-color" id="TP" title="Salle de travaux pratiques">
				<i class="medium mdi-social-school primary-text-color">
				</i>
			</a>
			<a class="btn-flat waves-effect waves-light  margin_top_bouton filtre-type primary-text-color" id="Laboratoire" title="Laboratoire">
				<i class="medium mdi-notification-sd-card primary-text-color">
				</i>
			</a>
			<a class="btn-flat waves-effect waves-light  margin_top_bouton filtre-type" id="avancee" title="Recherche avancée">
				<i class="medium mdi-hardware-keyboard-control primary-text-color">
				</i>
			</a>


			<p class="center categorie"><span class="white-text" id="nom_categorie">
				Salle informatique</span>
			</p>

			<div id="searchRow" class="row">
				<div class="input-field col s6 offset-s3">
					<input id="numero_salle" type="text" class="validate white-text">
					<label class="white-text" for="numero_salle">
						Numéro de salle
					</label>
					<button id="searchBtn" class="btn-floating waves-effect waves-light accent-color btn-large " type="submit" name="action">
						<i class="large mdi-action-search"></i>
					</button>
				</div>
			</div>
		</div>

		<div id="searchResults" class="row center">
			<div class="col s12 offset-s3 stop_offset">
			<?php 
				$salles = json_decode(file_get_contents('https://mvx2.esiee.fr/api/ade.php?func=rechSalle'),TRUE);

				foreach ($salles as $indexInArray => $room) {
					foreach ($room as $roomNumber => $roomState) {

						if ($roomState >= 0) {
							echo('<a class="validate collection-item" href="#">
										<ul class="collection valign-wrapper reduc_liste_salle">
											<li class="tab col s2 offset-s2>
												<span class="valign right">
													'. strval($roomNumber) .'
												</span>
											</li>
											<li class="tab col s7">
												<i class="small mdi-hardware-desktop-windows primary-text-color">
												</i>
												<i class="small mdi-social-school primary-text-color">
												</i>
												<i class="small mdi-notification-sd-card primary-text-color">
												</i>
												<i class="small mdi-notification-sd-card primary-text-color">
												</i>
												<i class="small mdi-notification-sd-card primary-text-color">
												</i>
											</li>');
												if($roomState > 0){
													echo('<li class="time tab col s2"><span class="valign right">'. $roomState ." min");
												}else{
													echo('<li class="tab col s2"><span class="valign right">Libre');
												}
												echo('</span>
											</li>
										</ul>
									</a>');
						}
					}
				}
			 ?>
			</div>
		</div>




	</main>



	<!--  Scripts-->

	<script src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
	<script src="js/materialize.js"></script>
	<script src="js/init.js"></script>

	<script>  
	  // === Dropdown ===  
	  $(document).ready(function() {
	  	 $(".button-collapse").sideNav({
	        closeOnClick: true
	    });
	  	$('select').material_select();
	  });
	  </script>
	  <script>$('select').material_select('destroy');</script>

	  <script>
	  $(document).ready(function(){
	  	$('.collapsible').collapsible({
	  accordion : false // A setting that changes the collapsible behavior to expandable instead of the default accordion style
	});
	  });
	  </script>

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
