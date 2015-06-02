<?php

	class ADE
	{
		/////////////////////////////////////////////////////////////////////////////////////////
		/// ATTRIBUTS
		/////////////////////////////////////////////////////////////////////////////////////////

		private $_projetADE = 4;
		private $_sessionID;
		private $_prefixe_api = "https://planif.esiee.fr/jsp/webapi"; // ancien port : 8443
		private $_proxy_url = "https://dupontl:mvx2dupontl@mvproxy.esiee.fr:3128";
		private $_use_proxy = true;

		/////////////////////////////////////////////////////////////////////////////////////////
		/// CONSTRUCTEUR - DESTRUCTEUR
		/////////////////////////////////////////////////////////////////////////////////////////

		public function __construct($projetADE)
		{
			$this->_projetADE = $projetADE;
			$this->_ade_connect();
		}

		public function __destruct()
		{
			$this->_ade_disconnect();
		}

		/////////////////////////////////////////////////////////////////////////////////////////
		/// METHODES PRIVEES
		/////////////////////////////////////////////////////////////////////////////////////////

		/**
		 * Lance une requête HTTP de type GET.
		 * @param  string $url L'URL à contacter pour effectuer la requête HTTP.
		 * @return SimpleXMLElement      Le résultat de la requête sous forme XML.
		 */
		private function _httpGet($url)
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

			return new SimpleXMLElement($out);
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

		private function _ade_disconnect()
		{
			$this->_httpGet($this->_prefixe_api
				."?sessionId=".$this->_sessionID
				."&function=disconnect");
		}

		/////////////////////////////////////////////////////////////////////////////////////////
		/// METHODES PUBLIQUES (fonctions de l'API)
		/////////////////////////////////////////////////////////////////////////////////////////

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
				"nom" => "LIKE",
				"type" => "LIKE",
				"taille" => "=",
				"projecteur" => "=",
				"tableau" => "=",
				"imprimante" => "="
			);
			foreach ($criteres_possibles as $critere => $operateur)
			{
				if (isset($_GET[$critere])) {
					$sql_conditions[] = $critere." ".$operateur." '".$_GET[$critere]."'";
				}
			}

			// Construction de la partie "WHERE" de la requête SQL :
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

			// Connexion à la BDD :
			$db = mysql_connect('localhost', 'root', 'MyMVX2');
			mysql_select_db('eroom', $db);

			// Requête SQL :
			$sql = "SELECT * FROM salle".$sql_where;

			// Exécution :
			$req = mysql_query($sql) or die('Erreur SQL !<br>'.$sql.'<br>'.mysql_errno());

			// Stockage :
			$salles = array();
			while ($data = mysql_fetch_assoc($req))
			{
				$salles[$data['nom']] = array(
					$data['resourceID'], 	// 0
					-1 					// 1 (disponibilité)
				);
			}

			mysql_close();


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

			return json_encode($resultats);
		}
	}

 ?>