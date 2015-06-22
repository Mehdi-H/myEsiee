<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-20 11:40:58
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-21 17:36:20
 */


require_once('scripts/php/post-modal-form.php');

require_once('cookies.php');

require_once('scripts/php/curl-post-request.php');

$show_modal = false;
// var_dump($data);die();
?>

<!DOCTYPE html>
<html lang="fr">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1.0, user-scalable=no"/>
	<title>MyESIEE - Notes</title>

	<!-- CSS  -->
	<link href="css/materialize.css" type="text/css" rel="stylesheet" media="screen,projection"/>
	<link href="css/style.css" type="text/css" rel="stylesheet" media="screen,projection"/>  
	<link href="css/palette.css" type="text/css" rel="stylesheet" media="screen,projection"/>

	<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.3.5/angular.min.js"></script>
	<script src="js/angular-cookies.min.js" type="text/javascript"></script>
	<script src="js/angular-route.min.js" type="text/javascript"></script>

</head>


<body ng-app="aurionApp">
	<script type="text/javascript">
		var show_modal = false;
	</script>
	<main ng-controller="MainCtrl">

		<?php include('includes/header.php'); ?>
		
		
		<div class="row center bouton_notes">
			<span id="options">
				<a href="#/notes"class="waves-effect waves-light btn accent-color taille_bouton">
					notes
				</a>
				<a href="#/absences"class="waves-effect waves-light btn accent-color taille_bouton">
					absences
				</a>
				<a id="appreciation_btn" href="#/appreciations" class="waves-effect waves-light btn accent-color taille_bouton">
					appreciations
				</a>

					<span class="switch" id="archives-box">
						<label for="archivesBtn">
						  <!-- Off -->
						  <input ng-click="old()" class="with-gap" type="checkbox" id="archivesBtn">
						  <span class="lever"></span>
						  Archives
						</label>
					</span>

			</span>
			
		</div>
		
		<div class="row">
			<!-- <div class="row center" ng-if=' loading '>
				<div class="preloader-wrapper big active">
					<div class="spinner-layer spinner-blue-only">
						<div class="circle-clipper left">
							<div class="circle"></div>
						</div><div class="gap-patch">
						<div class="circle"></div>
							</div><div class="circle-clipper right">
						<div class="circle"></div>
						</div>
					</div>
				</div>
			</div> -->

			<div ng-view class="col s8 offset-s2" id="data-output">

			</div>

		</div>

		<?php include('includes/modal-form.php'); ?>

	</main>

	<!--  Scripts-->
	
	<?php include('includes/scripts_js.php'); ?>

	<script type="text/javascript">
		// function load(e){

		// 	var currentlyAnimating = false;

		//     if (currentlyAnimating) {
		// 	        return false;
		// 	}

		// 	currentlyAnimating = true;

		// 	if ($.cookie('eroom') != null ){
			
		// 		var param = $(e).text();

		// 		if($('#archivesBtn').prop("checked")){
		// 			param += "_archivees";
		// 		}

		// 		// var param = "<?php Print($p); ?>";

		// 		$('#options').hide();
		// 		$('#loading').append('<div class="progress blue"><div class="indeterminate"></div></div>');
		// 		$('#options a').click(false);
		// 		$('#loading').append('<p>Veuillez patienter...</p>');
		// 		$('#data-output').load("aurion.php?q=" + param + " #data-output", function(response, status, xhr){
		// 			$('#loading').empty();
		// 			$('#options').fadeIn('slow');
		// 			$('#options a').click(true);
		// 			// console.log(response);
		// 			if ($('.collection').length == 0){
		// 			console.log('000');
		// 		  	$('#modal1').openModal();
		// 		}
					
		// 		});
		// 	}else{

		// 		$('#modal1').openModal();
		// 	}

		// 	currentlyAnimating = false;
		// 	return false;
		// }
	</script>

	<!-- Modules -->
    <script src="js/app.js"></script>

    <!-- Controllers -->
    <script src="js/controllers/MainController.js"></script>

    <script src="js/filters.js"></script>

</body>
</html>