<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-09 14:02:23
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-20 22:44:35
 */
$c = json_decode($_COOKIE['eroom']);
$login = $c->{'login'};
?>

<div class="navbar-fixed">
	<nav class="dark-primary-color" role="navigation">
		<div class="nav-wrapper">
			<a id="logo-container" href="index.php" class="brand-logo">
				<img id="logo" src="img/logo.png">
			</a>
			<ul id="dropdown1" class="dropdown-content">
			  <li id="deconnexion" data-position="bottom"><a href="#">Déconnexion</a></li>
			</ul>
			<ul class="right hide-on-med-and-down">
				<li><a href="index.php" >Salles</a></li>
				<li><a href="recherche_professeur.php">Professeurs</a></li>
				<li><a href="aurion.php">Vie étudiante</a></li>
				<li><a class="modal-trigger" href="#modalcontrib">Contribuer</a></li>
				<?php 
					if(!empty($_COOKIE['eroom'])){
				?>
				<li>
					<a href="#!" class="dropdown-button" data-activates="dropdown1">
						<?php echo json_decode($_COOKIE['eroom'])->{'login'}; ?>
						<i class="mdi-navigation-arrow-drop-down right"></i>
					</a>
				</li>
				<?php
					}else{
				?>
				<li><a href="connexion.php">Connexion</a></li>
				<?php
					}
				?>
			</ul>

			<ul id="nav-mobile" class="side-nav">
				<li><a href="index.php">Salles</a></li>
				<li><a href="recherche_professeur.php">Professeurs</a></li>
				<li><a href="aurion.php">Vie étudiante</a></li>
				<li><a class="modal-trigger" href="#modalcontrib">Contribuer</a></li>
				<li><a href="#">Déconnexion</a></li>
			</ul>
			<a href="#" data-activates="nav-mobile" class="button-collapse"><i class="mdi-navigation-menu"></i></a>
		</div>
	</nav>
</div>

<div id="modalcontrib" class="modal modal-fixed-footer">
    <div class="modal-content">
		<h4>Contribuer</h4>
		<p>Des erreurs ou bugs à signaler ? Des améliorations à suggérer ? Décrivez-les nous !</p>
		<div class="row">
			<form id="form_contrib" method="post" action="" class="col s12">
				<label>Choisissez un type de contribution :</label>
				<select required name="type_contrib">
				  <option value="" disabled selected></option>
				  <option value="Amélioration">Amélioration</option>
				  <option value="Erreur">Erreur</option>
				  <option value="Bug">Bug</option>
				  <option value="Autre">Autre</option>
				</select>
				<div class="input-field col s12">
						<input name="email" id="email" type="email" class="validate">
						<label for="email">Votre email</label>
				</div>
				<div class="row">
					<div class="input-field col s12">
						<i class="mdi-editor-mode-edit prefix"></i>
						<textarea required id="icon_prefix2" name="contenu" class="contribution_formulaire materialize-textarea"></textarea>
						<label for="icon_prefix2">Ecrivez !</label>
					</div>
				</div>
				<input name="useragent_contrib" id="useragent" type="hidden" value="">
				<input name="location" id="location" type="hidden">
				<input name="a_ete_soumis" type="hidden" value="oui">
				<input name="login" type="hidden" value="<?php echo $login; ?>">
			</form>
		</div>
    </div>
	<div class="modal-footer">
		<button form="form_contrib" class="bouton_formulaire btn waves-effect waves-light" type="submit" name="action">Soumettre
			<i class="mdi-content-send right"></i>
		</button>
    </div>
</div>

<script>
	var browser = '';
	var browserVersion = 0;

	if (/Opera[\/\s](\d+\.\d+)/.test(navigator.userAgent)) {
		browser = 'Opera';
	} else if (/MSIE (\d+\.\d+);/.test(navigator.userAgent)) {
		browser = 'MSIE';
	} else if (/Navigator[\/\s](\d+\.\d+)/.test(navigator.userAgent)) {
		browser = 'Netscape';
	} else if (/Chrome[\/\s](\d+\.\d+)/.test(navigator.userAgent)) {
		browser = 'Chrome';
	} else if (/Safari[\/\s](\d+\.\d+)/.test(navigator.userAgent)) {
		browser = 'Safari';
		/Version[\/\s](\d+\.\d+)/.test(navigator.userAgent);
		browserVersion = new Number(RegExp.$1);
	} else if (/Firefox[\/\s](\d+\.\d+)/.test(navigator.userAgent)) {
		browser = 'Firefox';
	}
	if(browserVersion === 0){
		browserVersion = parseFloat(new Number(RegExp.$1));
	}
	document.getElementById("useragent").value = browser + " (v. " + browserVersion + ")";
	document.getElementById("location").value = document.location.pathname;
</script>

<?php
$contrib_path = $_SERVER['DOCUMENT_ROOT'] . "/api/contribution.php";
if (isset($_POST['a_ete_soumis']))
{
	include_once $contrib_path;
}
?>

<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-64335794-1', 'auto');
  ga('send', 'pageview');
</script>