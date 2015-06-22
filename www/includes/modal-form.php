<div id="modal1" class="modal default-primary-color z-depth-3 modal-form">
	<div class="modal-content">
		<h4>Echec de connexion</h4>
		<p>Votre mot de passe ou votre login semble invalide.</p>
		<!-- <p> Vous allez être redirigé vers la page de connexion.</p> -->
		<div class="searchbar login in-form">
			<form id="connectionForm" class="col s12" method="POST">
				<div class="input-field col s6 offset-s3">
					<?php $l = (empty($_POST['login'])) ? $login : $_POST['login']; ?>
					<input value="<?php echo($l); ?>" id="first_name" type="text" class="validate white-text" name="login" required>
					<label class="white-text" for="first_name">Login ESIEE</label>
				</div>
				<div class="input-field col s6 offset-s3">
					<input id="password" type="password" class="validate white-text" name="pwd" required>
					<label class="white-text"for="Mot de passe">Mot de passe</label>
				</div>
				<div class="col s6 offset-s3">
					<button class="btn waves-effect waves-light accent-color" type="submit" name="action">Envoyer
						<i class="mdi-content-send right"></i>
					</button>
				</div>				
			</form>
		</div>
	</div>
	<div class="modal-footer default-primary-color z-depth-3">
		<a href="#" class=" modal-action modal-close waves-effect waves-green btn-flat accent-color">Fermer</a>
	</div>
</div>