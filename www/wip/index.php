<?php 
if (isset($_POST['query_room'])){

	include('includes/bdd_connect.php');

	$stmt = $conn->prepare('SELECT nom FROM salle WHERE nom LIKE "%'.$_POST['query_room'].'%"');
	$stmt->execute();
	$room_list = $stmt->fetchAll();

	exit(json_encode($room_list));
}
?>

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
	
		<?php include('includes/header.php'); ?>

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
				Liste des salles</span>
			</p>

			<div id="searchRow" class="row">
				<form id="form_salle" method="GET" action='fiche.php'>
					<div class="input-field col s6 offset-s3">
						<input list="salle_list" name="query_room" id="numero_salle" type="text" class="validate white-text" autocomplete="off" required onkeyup="room_suggest(this);">
						<label class="white-text" for="numero_salle">
							Numéro de salle
						</label>
						<ul class="collection" id="salle_list">
					    </ul>
						<button id="searchBtn" class="btn-floating waves-effect waves-light accent-color btn-large " type="submit" form="form_salle">
							<i class="large mdi-action-search"></i>
						</button>
					</div>
				</form>
			</div>
		</div>

		<div id="searchResults" class="row center">
			<div class="col s12 offset-s3 stop_offset">
			<?php 

				// Connexion à la bdd
				include('includes/bdd_connect.php');

				$salles = json_decode(file_get_contents('https://mvx2.esiee.fr/api/ade.php?func=rechSalle'),TRUE);

				$stmt = $conn->prepare('SELECT * FROM salle');
				$stmt->execute();
				$data = $stmt->fetchAll();

				// import du tableau des classes pour les icones dans $icons
				// et du tableau $title pour les indications on hover icons
				include('includes/icon_class.php');

				foreach ($salles as $indexInArray => $room) {
					foreach ($room as $roomNumber => $roomState) {

						if ($roomState >= 0) {
						?>
							<a class="validate collection-item" href="fiche.php?salle=<?php echo(str_replace("+","%2B",$roomNumber)); ?>">
								<ul class="collection valign-wrapper reduc_liste_salle">
									<li class="tab col s2">
										<span class="valign right">
											<?php echo(strval($roomNumber)); ?>
										</span>
									</li>
									<li class="tab col s7">
										<i class="small <?php echo($icons['type'][$data[$indexInArray]['type']]); ?> primary-text-color" title="<?php echo($title['type'][$data[$indexInArray]['type']]); ?>"></i>
										<i class="small <?php echo($icons['projecteur'][$data[$indexInArray]['projecteur']]); ?> primary-text-color" title="<?php echo($title['projecteur']); ?>"></i>
										<i class="small <?php echo($icons['tableau'][$data[$indexInArray]['tableau']]); ?> primary-text-color" title="<?php echo($title['tableau'][$data[$indexInArray]['tableau']]); ?>"></i>
										<i class="small <?php echo $icons['imprimante'][$data[$indexInArray]['imprimante']]; ?> primary-text-color" title="<?php echo($title['imprimante']); ?>"></i>
										<?php 
											$t_val = intval($data[$indexInArray]['taille']);
											$taille = 'moyenne';
											if((0 <= $t_val) && ($t_val < 30)){
												$taille = 'petite';
											}elseif((30 <= $t_val) && ($t_val < 70)){
												$taille = 'moyenne';
											}else{
												$taille = 'grande';
											}
										 ?>
										 <i class="small <?php echo($icons['taille'][$taille]); ?> primary-text-color" title="<?php echo($title['taille'][$taille]); ?>"></i>
									</li>
									<?php 
										if($roomState > 0){
											echo('<li class="time tab col s2"><span class="valign right">'. $roomState ." min");
										}else{
											echo('<li class="tab col s2"><span class="valign right">Libre');
										}
									?>
									</span>
									</li>
								</ul>
							</a>
						<?php
						}
					}
				}
			 ?>
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

</body>
</html>
