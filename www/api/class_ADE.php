<?php

	class ADE
	{
		/////////////////////////////////////////////////////////////////////////////////
		/// ATTRIBUTS
		/////////////////////////////////////////////////////////////////////////////////

		private $_projetADE = 4;
		private $_sessionID;
		private $_prefixe_api = "https://planif.esiee.fr/jsp/webapi"; // ancien port : 8443
		private $_proxy_url = "https://dupontl:mvx2dupontl@mvproxy.esiee.fr:3128";
		private $_use_proxy = true;

		/////////////////////////////////////////////////////////////////////////////////
		/// CONSTRUCTEUR - DESTRUCTEUR
		/////////////////////////////////////////////////////////////////////////////////

		public function __construct($projetADE)
		{
			require_once '../mysql_sync/config.php';

			$this->_projetADE = $projetADE;
			$this->_ade_connect();
		}

		public function __destruct()
		{
			$this->_ade_disconnect();
		}

		/////////////////////////////////////////////////////////////////////////////////
		/// METHODES PRIVEES
		/////////////////////////////////////////////////////////////////////////////////

		/**
		 * Lance une requête HTTP de type GET.
		 * @param  string $url L'URL à contacter pour effectuer la requête HTTP.
		 * @return SimpleXMLElement      Le résultat de la requête sous forme XML.
		 */
		private function _httpGet($url, $xml = true)
		{
			// Création de la requête :
			$ch = curl_init();

			curl_setopt_array($ch, array(
				CURLOPT_RETURNTRANSFER => 1,
				CURLOPT_URL => $url,
				CURLOPT_USERAGENT => 'E-Room pour ESIEE Paris (projet E3E 2015)',
				CURLOPT_PROXY => $this->_use_proxy ? $this->_proxy_url : NULL,
				CURLOPT_FOLLOWLOCATION => 1,
				CURLOPT_SSL_VERIFYPEER => false,
			));

			$out = curl_exec($ch); // Exécution
			curl_close($ch); // Fermeture

			return $xml ? new SimpleXMLElement($out) : $out;
		}

		/**
		 * Lance une requête pour afficher une image de l'emploi du temps pour une ressource et une date donnée.
		 */
		private function _imageET($resourceID, $date, $largeur = 500, $hauteur = 700)
		{
			// === Mise en forme des paramètres weeks et days à partir de la date ===

			$weeks = date("W", $date) + 18; // offset d'origine inconnue à utiliser dans l'API fournie par ADE...
			$days = date("N", $date) - 1; // pour avoir lundi = 0, ...


			// === Format de l'URL ===

			$url = $this->_prefixe_api
				."?sessionId=".$this->_sessionID
				."&function=imageET"
				."&resources=".$resourceID
				."&width=".$largeur
				."&height=".$hauteur
				."&weeks=".$weeks
				."&days=".$days;


			// === Récupération de l'image sur ADE et sauvegarde du fichier ===

			$image_name = "/var/www/tmp/imageET_".time().".gif";
			$fp = fopen($image_name, 'wb');

			$ch = curl_init($url);
			curl_setopt_array($ch, array(
				CURLOPT_USERAGENT => 'E-Room pour ESIEE Paris (projet E3E 2015)',
				CURLOPT_PROXY => $this->_use_proxy ? $this->_proxy_url : NULL,
				CURLOPT_SSL_VERIFYPEER => false,
				CURLOPT_FILE => $fp,
				CURLOPT_HEADER => false

			));
			$out = curl_exec($ch);
			curl_close($ch);
			fclose($fp);

			return $image_name;
		}

		/**
		 * Connexion à ADE pour récupérer un SessionID et se placer sur le bon projet ADE.
		 */
		private function _ade_connect()
		{
			// Se connecter à une session :
			$req = $this->_httpGet($this->_prefixe_api
				."?function=connect"
				."&login=lecteur1&password=");

			$this->_sessionID = $req['id'];

			// Se placer sur le projet 4 (EDT 2014-2015) :
			$this->_httpGet($this->_prefixe_api
				."?sessionId=".$this->_sessionID
				."&function=setProject"
				."&projectId=".$this->_projetADE);
		}

		/**
		 * Déconnexion d'ADE, ferme la session utilisée.
		 */
		private function _ade_disconnect()
		{
			$this->_httpGet($this->_prefixe_api
				."?sessionId=".$this->_sessionID
				."&function=disconnect");
		}

		private function _mysql_request($request)
		{
			// Connexion à la BDD :
			$db = mysql_connect(DB_HOST, DB_USER, DB_PASSWORD);
			mysql_select_db(DB_DATABASE, $db);

			// Exécution :
			$req = mysql_query($request)
				or die('Erreur SQL !<br>'.$request.'<br>'.mysql_errno());

			mysql_close($db);
			return $req;
		}

		private function _get_param($param, $default)
		{
			return isset($_GET[$param]) ? $_GET[$param] :
					(isset($_POST[$param]) ? $_POST[$param] : $default);
		}

		/////////////////////////////////////////////////////////////////////////////////
		/// METHODES PUBLIQUES (fonctions de l'API)
		/////////////////////////////////////////////////////////////////////////////////

		/**
		 * FONCTION : Recherche d'une salle. Appelée si "func=rechSalle" dans l'URL.
		 *
		 * Paramètres possibles de la requête :
		 *  - "nom" : le nom de la salle
		 *  - "type" : le type de salle
		 *  - "taille" : la taille de la salle (en nombre de places)
		 *  - "projecteur" : [0 = non, 1 = oui]
		 *  - "tableau" : [0 = non, 1 = noir, 2 = blanc, 3 = les deux]
		 *  - "imprimante" : [0 = non, 1 = oui]
		 *
		 *  Résultat en JSON : format :
		 *  - [{numeroSalle:dispo}, ...]
		 *	-- dispo : [-1=occupée, 0=libre jusqu'à ce soir, autre=durée pendant laquelle la salle est encore libre]
		 *
		 * @return string Le résultat de la recherche au format JSON.
		 */
		public function rechSalle()
		{
			// === 1. Paramètres de la recherche ===

			// Tableau des conditions à ajouter à la requête SQL :
			$sql_conditions = array();

			// Critères possibles :
			$criteres_possibles = array(
				"nom", // nom : index 0, ne pas changer
				"type",
				"taille",
				"projecteur",
				"tableau",
				"imprimante"
			);

			// --- Récupérer la valeur des critères "epi" et "etage" ---

			$val_epi = $this->_get_param("epi", false);
			$val_etage = $this->_get_param("etage", false);

			// Construire le modèle du "nom LIKE modele" :
			if (!! $val_epi || !!$val_etage)
			{
				// Exemples :
				// - si "epi=1" et "etage=2" : "nom LIKE '12%'"
				// - si "epi=1" : "nom LIKE '1%'"
				// - si "etage=2" : "nom LIKE '_2%'"
				$sql_conditions[] = "nom LIKE '"
					.(!! $val_epi ? $val_epi : "_")
					.(!! $val_etage ? $val_etage : "")
					."%'";

				// Annuler le critère possible "nom" :
				unset($criteres_possibles[0]);
			}

			// --- Parcours de tous les critères possibles ---

			foreach ($criteres_possibles as $critere)
			{
				// Récupérer la valeur du critère en GET ou en POST si elle existe :
				$val_critere = $this->_get_param($critere, false);

				// Critère suivant si celui-ci n'est pas spécifié :
				if (! $val_critere) {
					continue;
				}

				// Prise en compte du critère :
				$sql_conditions[] = $critere."='".$val_critere."'";
			}

			// --- Construction de la partie "WHERE" de la requête SQL ---

			$sql_where = "";
			foreach ($sql_conditions as $index => $condition)
			{
				if ($index == 0) {
					// Première condition précédée du mot-clé "WHERE" :
					$sql_where .= " WHERE ".$condition;
				}
				else {
					// Les conditions suivantes précédées du mot-clé "AND" :
					$sql_where .= " AND ".$condition;
				}
			}


			// === 2. Lister les salles correspondant aux critères dans la BDD ===

			$req = $this->_mysql_request("SELECT * FROM salle".$sql_where);

			// Stockage :
			$salles = array();
			while ($data = mysql_fetch_assoc($req))
			{
				$salles[$data['nom']] = array(
					$data['resourceID'], 	// 0
					-1 					// 1 (disponibilité)
				);
			}


			// === 3. Déterminer la disponibilité de chaque salle ===

			$now_date_ADE = date("m/d/Y"); // format "05/20/2015" pour les requêtes ADE

			foreach ($salles as $nom_salle => $salle)
			{
				// --- Requête ADE ---

				$req = $this->_httpGet($this->_prefixe_api
					."?sessionId=".$this->_sessionID
					."&function=getEvents"
					."&detail=0"
					."&resources=".$salle[0] // resourceID
					."&date=".$now_date_ADE
				);

				// --- Lecture des horaires ---

				$horaires = array();
				foreach ($req->xpath("//event") as $event)
				{
					$start = "".$event['startHour'];
					$end = "".$event['endHour'];

					// Ajouter les horaires de l'event à cette date :
					$horaires[] = array($start, $end);
				}

				// --- Calcul du temps libre restant ---
				// Pas libre (-1), libre jusqu'à telle heure (entier), libre toute la journée (0)

				$libre = true;
				$pendant = -1;

				foreach ($horaires as $cours)
				{
					$debut = strtotime($cours[0]);
					$fin = strtotime($cours[1]);

					if ($fin < time())
					{
						// Si l'heure de fin est dépassée : on ne s'occupe pas de cette horaire.
						continue;
					}
					elseif (time() < $debut && $libre)
					{
						// Si on est avant l'heure de début : combien de temps on est libre ?
						$pendant = round( ($debut-time())/60 );
					}
					elseif ($debut <= time())
					{
						// Si on est après le début d'un cours (et avant la fin grâce au premier IF) : pas libre
						$libre = false;
					}
				}

				if ($libre && $pendant > 0) {
					$salles[$nom_salle][1] = $pendant;
				} elseif ($libre) {
					$salles[$nom_salle][1] = 0;
				} else {
					$salles[$nom_salle][1] = -1;
				}
			}

			$this->_ade_disconnect();


			// === 4. Résultats en JSON ===

			// Sélection de la dispo uniquement (sans resourceID) :
			$resultats = array();
			foreach ($salles as $nom => $vals)
			{
				$resultats[] = array(
					$nom => $vals[1]
				);
			}

			header("Content-type: application/json");
			echo(json_encode($resultats));
		}

		/**
		 * FONCTION : Disponibilité d'une salle. Affiche une image de l'emploi du temps d'une salle pour une journée donnée. Appelée si "func=dispoSalle" dans l'URL.
		 *
		 * Paramètres possibles de la requête :
		 *  - "nom" : le nom de la salle.
		 *  - "date" : le jour à afficher (au format américain "mm/jj/aaaa"). Optionnel : par défaut, l'emploi du temps du jour-même sera affiché.
		 *
		 * @return image L'image de l'emploi du temps pour la salle et le jour sélectionnés
		 */
		public function dispo($type = "salle")
		{
			// === 1. Récupération des paramètres (nom et date) ===

			// Nom de la salle :
			$nom = $this->_get_param("nom", false);

			if (! $nom) {
				echo("Erreur : nom ".$type." non spécifié.");
				exit;
			}

			// Date :
			// (Format : "mm/jj/aaa". Si non spécifiée : la date du jour)
			$date = $this->_get_param("date", false);
			$date = (! $date ? time() : strtotime($date));

			// Dimensions de l'image :
			$largeur = $this->_get_param("largeur", "500");
			$hauteur = $this->_get_param("hauteur", "700");


			// 2. === Récupération du Resource ID de la salle dans la BDD ===

			$req = $this->_mysql_request("SELECT resourceID FROM ".addslashes($type)." WHERE nom='".addslashes($nom)."'");
			$resourceID = mysql_fetch_assoc($req)['resourceID'];

			if($resourceID == null) {
				echo("Erreur: ressource non trouvée dans la BDD.");
				exit();
			}


			// === 3. Génération de l'image de l'emploi du temps ===

			$image_name = $this->_imageET($resourceID, $date, $largeur, $hauteur);


			// === 4. Affichage de l'image ===

			header("Content-type: image/gif");
			readfile($image_name);

			//ignore_user_abort(true);
			unlink($image_name); // supprime l'image du serveur
		}
	}

 ?>