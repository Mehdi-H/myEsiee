<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-12 16:23:04
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-19 16:13:17
 */
?>

<!DOCTYPE html>
<html lang="fr">
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
				<img class="icon icons8-Front-Desk" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAYAAAAeP4ixAAAB0ElEQVRoQ+2Y8TEEMRTGf1cBJegAFaACVIAOqAAV0IG7ClCBUwEl0AEVMN/N7s26md2XnbxkZs3Lv9nNy+/7kpeXzCjXzoBLYK8J8Q7cA4sSIWcFBt0GXjoAmyEEdAR8ecYuASKIQ2OSywbGjcUbRAACSWlyRUAuzRtkDmhvpDTtlfOUD1O+8QaRwgcpgYHXhCWYOBQESI9USrd3iTJeNek48fPhz7wdUer9ALaM2X0DO54p2BtE89cGfjBALgAlBrdWAkSTO2kmuunMZ3PaP7kRNAOVAmnnKaBuieIO0AYqDeItfO94niBS/njkzJ8B1V7ZzQNEmeox43DTIXqam8E8QFKKREvx7CIyF2RMkWjBZBWRuSA3wLU1w8T+W0DjWU2Xs11AlcF6f00RRElF+1IQ68vZ1EB0RdByVnmjfaWrgEqi7Oq31tLquz7LES2x+VQceRt4AxDM/hRAtCcEMtQWuSBWhqnW/69AfqrJVjCQHAmQggKPHlqOtI9kqc84o4MU/kHPSn8OxKkusVXC6matACm8dKzhwxFLodr94Uhtxa144YilUO3+cKS24la8cMRSqHZ/OFJbcSteOGIpVLs/HKmtuBUvHLEUqt2/cuQXgrxYsMK/zUIAAAAASUVORK5CYII=" width="30" height="30"> <span class="info-prof"><?php echo($data_prof['bureau']); ?></span>
			</span>
			<span class="valign-wrapper">
				<img class="icon icons8-Message-Outline" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAYAAAAeP4ixAAACMElEQVRoQ+2Z/TEEQRDF30VABmRABshABogAESACRIAMZIAMyIAMyED9qrZVG7u3u2Nm9mpv+p+rupuZ169fd8/HLTQTW8yEhyqRVVOyKlIVyRSBtUitbUlHkvYzBXHsss+SHiS9t03sUuRM0vVYpELjzyXdhFhtRFDgqZBTsTAHklDox9qI3DcpFQtSYh4pdtxHBKZ7blBnXpbwuMGwejXIl7B22xQJiZiMm5I+CzoPlGGG6f4vIpeSNiRdFSAEgQtJX5LATU6ExWl/dI7HTOocSrpr1CBo2YiY/xCBUGtfjyBJHdDyIWJWhAhg1AwRu41w3E85bdYhpbxlIQIITgMa2qukE0l8jrHdJo34DI3ggEewktaIAbEoO+tOCzjfD2kGVsycIEJ7k8T3fsPLQsSAiRaAdDJv1Azq/Np53QCcopipCW90KALBuqFlJQIYznAS8BuoOUEzgJDtPagAAV/MNpZ9gZ26q3FkJ2KO4ATRDNWxZsA4Ih0WMyowt6+VFyOCozgJGY7+Q4yjD6k55LRQlIhvBqTbVgebj0aFrhpqmzYJEd8MOAl4s31hiGJ+zKREcIT9wS5By4q5j9jkRPocHPp7JeL3hT/XyqFhTDiuKlIVSZhOK9V+U/GqNVJrJFUuBeskSS0OgKkeGGJ5cu/xL4uD3rVm82Q6m0ds5J/F3wqWx5aXHMXDK2psrsfO4xbJU1Nnva7FX2+x0ZtkXlVkkrAvAa2KVEUyRWA2qfUNdZC3M7kCYJAAAAAASUVORK5CYII=" width="30" height="30"> <span class="info-prof"><a href="mailto:<?php echo($data_prof['email']); ?>"><?php echo($data_prof['email']); ?></a></span>
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
