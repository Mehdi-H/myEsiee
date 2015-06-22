<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-03 16:11:32
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-18 22:12:13
 */

require_once('scripts/php/post-modal-form.php');

require_once('cookies.php');

require_once('scripts/php/curl-post-request.php');

$show_modal = false;
// var_dump($data);die();
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
	<script type="text/javascript">
		var show_modal = false;
	</script>
	<main>

		<?php include('includes/header.php'); ?>
		
		
		<div class="row center bouton_notes">
			<span id="options">
				<a href="#"class="waves-effect waves-light btn accent-color taille_bouton" onclick="load(this);">notes</a>
				<a href="#"class="waves-effect waves-light btn accent-color taille_bouton" onclick="load(this);">absences</a>
					<a id="appreciation_btn" href="#"class="waves-effect waves-light btn accent-color taille_bouton" onclick="load(this);">appreciations</a>

					<span class="switch" id="archives-box">
						<label for="archivesBtn">
						  <!-- Off -->
						  <input class="with-gap" type="checkbox" id="archivesBtn">
						  <span class="lever"></span>
						  Archives
						</label>
					</span>

			</span>

			<span id="loading">
				
			</span>
		</div>
		
		<div class="row">
			<div class="col s8 offset-s2" id="data-output">
				<?php 
					include('includes/'.$p.'.php');
				?>
			</div>
		</div>

		<?php include('includes/modal-form.php'); ?>

	</main>

	<!--  Scripts-->
	
	<?php include('includes/scripts_js.php'); ?>

	<script type="text/javascript">
		function load(e){

			var currentlyAnimating = false;

		    if (currentlyAnimating) {
			        return false;
			}

			currentlyAnimating = true;

			if ($.cookie('eroom') != null ){
			
				var param = $(e).text();

				if($('#archivesBtn').prop("checked")){
					param += "_archivees";
				}

				// var param = "<?php Print($p); ?>";

				$('#options').hide();
				$('#loading').append('<div class="progress blue"><div class="indeterminate"></div></div>');
				$('#options a').click(false);
				$('#loading').append('<p>Veuillez patienter...</p>');
				$('#data-output').load("aurion.php?q=" + param + " #data-output", function(response, status, xhr){
					$('#loading').empty();
					$('#options').fadeIn('slow');
					$('#options a').click(true);
					// console.log(response);
					if ($('.collection').length == 0){
					console.log('000');
				  	$('#modal1').openModal();
				}
					
				});
			}else{

				$('#modal1').openModal();
			}

			currentlyAnimating = false;
			return false;
		}
	</script>

</body>
</html>