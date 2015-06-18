<!DOCTYPE html>
<html lang="fr">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1.0, user-scalable=no"/>
	<title>MyESIEE - Recherche de salle</title>

	<link rel="icon" type="image/png" href="img/ico_logo.png">

	<!-- CSS  -->
	<link href="css/materialize.css" type="text/css" rel="stylesheet" media="screen,projection"/>
	<link href="css/style.css" type="text/css" rel="stylesheet" media="screen,projection"/>
	<link href="css/palette.css" type="text/css" rel="stylesheet" media="screen,projection"/>

	<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.3.5/angular.min.js"></script>

</head>


<body ng-app="myApp">

	<main>
	
		<?php include('includes/header.php'); ?>

		<div class="row center default-primary-color">

			<!-- Bouton activé : bouton_active-->
			<span class="btn-flat waves-effect waves-light margin_top_bouton filtre-type " id="info" title="Salle informatique" ng-click="roomFilter = {type: 'it'}">
				<i class="medium mdi-hardware-desktop-windows primary-text-color">
				</i>
			</span>
			<span class="btn-flat  waves-effect waves-light margin_top_bouton filtre-type primary-text-color" id="TP" title="Salle de cours" ng-click="roomFilter = {type: 'banal'}">
				<i class="medium mdi-toggle-check-box-outline-blank primary-text-color">
				</i>
			</span>
			<span class="btn-flat waves-effect waves-light margin_top_bouton filtre-type primary-text-color" id="Laboratoire" title="Laboratoire d'électronique" ng-click="roomFilter = {type: 'elec'}">
				<i class="medium mdi-image-flash-on primary-text-color">
				</i>
			</span>
			<a href="recherche_avancee.php" class="btn-flat waves-effect waves-light  margin_top_bouton filtre-type" id="avancee" title="Recherche avancée">
				<i class="medium mdi-hardware-keyboard-control primary-text-color">
				</i>
			</a>


			<p class="center categorie">
				<span class="white-text" id="nom_categorie">
					Occupation des salles
				</span>
			</p>

			<div id="searchRow" class="row">
				<!-- <form id="form_salle" method="GET" action='fiche.php'> -->
					<div id="recherche_simple" class="input-field col s6 offset-s3">
						<!-- <input list="salle_list" name="salle" id="numero_salle" type="text" class="validate white-text" autocomplete="off" required onkeyup="room_suggest(this);"> -->
						<input type="text" ng-model="room.nom">
						<label class="white-text" for="numero_salle">
							Numéro de salle
						</label>
						
						<button id="searchBtn" class="btn-floating waves-effect waves-light accent-color btn-large " type="submit" form="form_salle">
							<i class="large mdi-action-search"></i>
						</button>
					</div>
				<!-- </form> -->


			</div>
		</div>

		<div id="searchResults" class="row center" ng-controller="MainController" ng-show="room">
			<div class="col s12 offset-s3 stop_offset" >
				<!-- <ul class="collection valign-wrapper reduc_liste_salle" ng-repeat="salle in libres | filter: room">
					<li class="tab col s2" ng-repeat="(key,val) in salle">{{ key }}</li>
					<li class="tab col s7">{{ salles[$index].imprimante | imprimante }}</li>
					<li class="tab col s2" ng-repeat="(key,val) in salle">{{ val }}</li>
			    </ul> -->
			    <ul class="collection valign-wrapper reduc_liste_salle" ng-repeat="salle in values = (salles | filter: room | filter:roomFilter)">
					<li class="tab col s2">
						<a href="fiche.php?salle={{ salle.nom | encodeURIComponent }}">
							{{ salle.nom }}
						</a>
					</li>
					<li class="tab col s7">
						<i class="{{ salle.type | type }}"></i>
						<i class="{{ salle.projecteur | projecteur }}"></i>
						<i class="{{ salle.tableau | tableau }}"></i>
						<i class="{{ salle.imprimante | imprimante }}"></i>
						<i class="{{ salle.taille | taille }}"></i>
					</li>
					<li class="tab col s2" ng-repeat="(key,val) in libres[$index]">{{ val | occupation }}</li>
			    </ul>
			    <p>{{ values.length }} salle{{ values.length > 1 ? "s" : ""}} trouvée{{ values.length > 1 ? "s" : ""}}</p>
			</div>
		</div>

	</main>



	<!--  Scripts-->

	<?php include('includes/scripts_js.php'); ?>

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

	<!-- Modules -->
    <script src="js/app.js"></script>

    <!-- Controllers -->
    <script src="js/controllers/MainController.js"></script>

    <script src="js/filters.js"></script>

</body>
</html>
