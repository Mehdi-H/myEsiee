<?php

	/**
	 * Retourne toute la BDD MySQL au format JSON.
	 */

	include_once './db_functions.php';

    $db = new DB_Functions();

    // Tables à synchroniser depuis la BDD MySQL du serveur vers les BDD SQLite Android :
    $tables_to_sync = array(
    	"salle",
    	"prof"
    );

    // === Récupération des tables ===

    $data_array = array();

    foreach ($tables_to_sync as $table_name)
    {
    	$data = $db->getTable($table_name);

    	// Vérification :
    	if ($data == false) {
    		exit();
    	}

    	// Pour chaque entrée dans la table :
    	while ($row = mysql_fetch_array($data))
    	{
    		// Récupérer les infos de la ligne :
    		$tmp = array();
    		foreach ($row as $nom_champ => $value) {
    			$tmp[$nom_champ] = $value;
    		}

    		// Stocker la ligne dans le tableau correspondant à la table lue :
    		array_push($data_array[$table_name], $tmp);
    	}
    }

    // === Retourner les données de la BDD au format JSON ===
    echo(json_encode($data_array));

 ?>