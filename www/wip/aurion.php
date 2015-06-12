<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-03 16:11:32
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-09 21:52:38
 */

require_once('scripts/php/post-modal-form.php');

require_once('cookies.php');

require_once('scripts/php/curl-post-request.php');

$show_modal = false;
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

		<?php include('includes/header.php'); ?>
		
		
		<div class="row center bouton_notes">
			<span id="options">
				<a href="#"class="waves-effect waves-light btn accent-color taille_bouton" onclick="load(this);">notes</a>
				<a href="#"class="waves-effect waves-light btn accent-color taille_bouton" onclick="load(this);">absences</a>
				<a href="#"class="waves-effect waves-light btn accent-color taille_bouton" onclick="load(this);">appreciations</a>
				<span id="archives-box">
			      <input class="with-gap" type="checkbox" id="archivesBtn" />
			      <label for="archivesBtn">Archives</label>
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

			if ($.cookie('eroom') != null ){
			
				var param = $(e).text();

				if($('#archivesBtn').prop("checked")){
					param += "_archivees";
				}

				// var param = "<?php Print($p); ?>";

				$('#options').fadeOut('slow');
				$('#loading').append('<div class="progress blue"><div class="indeterminate"></div></div>');
				$('#loading').append('<p>Veuillez patienter...</p>');
				$('#data-output').load("aurion.php?q=" + param + " #data-output", function(response, status, xhr){
					$('#loading').empty();
					$('#options').fadeIn('slow');
					
				});
			}else{
				$('#modal1').openModal();
			}
			return false;
		}
	</script>
	
</body>
</html>