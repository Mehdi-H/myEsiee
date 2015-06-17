<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-09 14:02:23
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-16 17:15:42
 */
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
		<form method="post" action="contribution.php" class="col s12">
			<label>Choisissez un type de contribution :</label>
			<select>
			  <option value="" disabled selected>Type de contribution :</option>
			  <option value="1">Amélioration</option>
			  <option value="2">Erreur</option>
			  <option value="3">Bug</option>
			  <option value="4">Autres</option>
			</select>
			<div class="row">
				<div class="input-field col s6">
					<i class="mdi-editor-mode-edit prefix"></i>
					<textarea id="icon_prefix2" name="contenu" class="contribution_formulaire materialize-textarea"></textarea>
					<label for="icon_prefix2">Ecrivez !</label>
				</div>
			</div>
		</form>
    </div>
    <div class="modal-footer">
			<button class="bouton_formulaire btn waves-effect waves-light" type="submit" name="action">Soumettre
				<i class="mdi-content-send right"></i>
		</div>
    </div>
</div>
