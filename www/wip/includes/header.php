<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-09 14:02:23
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-11 10:57:25
 */
?>
<div class="navbar-fixed">
	<nav class="dark-primary-color" role="navigation">
		<div class="nav-wrapper">
			<a id="logo-container" href="#" class="brand-logo">E-Room</a>
			<ul id="dropdown1" class="dropdown-content">
			  <li id="deconnexion" data-position="bottom"><a href="#">Déconnexion</a></li>
			</ul>
			<ul class="right hide-on-med-and-down">
				<li><a href="index.php" >Salles</a></li>
				<li><a href="Recherche_Professeur.html">Profs</a></li>
				<li><a href="aurion.php?q=notes">Notes</a></li>
				<li><a href="aurion.php?q=absences">Absences</a></li>
				<li><a href="aurion.php?q=appreciations">Appréciations</a></li>
				<li><a href="#">Paramètres</a></li>
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
				<li><a href="Recherche_Professeur.html">Profs</a></li>
				<li class="active"><a href="aurion.php?q=notes">Notes</a></li>
				<li><a href="aurion.php?q=absences">Absences</a></li>
				<li><a href="aurion.php?q=appreciations">Appréciations</a></li>
				<li><a href="#">Paramètres</a></li>
				<li><a href="#">Déconnexion</a></li>
			</ul>
			<a href="#" data-activates="nav-mobile" class="button-collapse"><i class="mdi-navigation-menu"></i></a>
		</div>
	</nav>
</div>