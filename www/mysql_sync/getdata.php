<?php

	/**
	 * Retourne toute la BDD MySQL au format JSON.
	 */

	include_once './MySQL_Sync.php';

	/////////////////////////////////////////////////////////////////////////////////////////
	/// Initalisation
	/////////////////////////////////////////////////////////////////////////////////////////

	$db = new MySQL_Sync;

	// Table à synchroniser depuis la BDD MySQL du serveur vers les BDD SQLite Android :

	if (!isset($_GET['table'])) exit();
	$table_name = $_GET['table'];

	$data_array = array();

	/////////////////////////////////////////////////////////////////////////////////////////
	/// Récupérer les tables
	/////////////////////////////////////////////////////////////////////////////////////////

	// Requête SQL de la classe MySQL_Sync :
	$data = $db->getTable($table_name);

	// Vérification qu'il y ait des données :
	if ($data == false) {
		continue;
	}

	// Pour chaque entrée dans la table :
	$i = 0;
	while ($row = mysql_fetch_assoc($data))
	{
		// Préparer le sous-tableau :
		$data_array[$i] = array();

		// Récupérer les infos de la ligne :
		foreach ($row as $nom_champ => $value)
		{
			// Stocker la ligne dans le tableau correspondant à la table lue :
			$data_array[$i][$nom_champ] = $value;
		}

		$i++;
	}


	/////////////////////////////////////////////////////////////////////////////////////////
	/// Retourner les données de la BDD au format JSON
	/////////////////////////////////////////////////////////////////////////////////////////

	echo(json_encode($data_array));

 ?>