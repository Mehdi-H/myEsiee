<?php

	/**
	 * Classe servant à se connecter à la BDD et à effectuer des requêtes.
	 */

	class MySQL_Sync
	{
		/////////////////////////////////////////////////////////////////////////////////////////
		/// ATTRIBUTS
		/////////////////////////////////////////////////////////////////////////////////////////

	    $db;

	    /////////////////////////////////////////////////////////////////////////////////////////
		/// CONSTRUCTEUR - DESTRUCTEUR
		/////////////////////////////////////////////////////////////////////////////////////////

	    function __construct() {
	    	$this->connect();
	    }

	    function __destruct() {
	        $this->close();
	    }

	    /////////////////////////////////////////////////////////////////////////////////////////
		/// METHODES PRIVEES
		/////////////////////////////////////////////////////////////////////////////////////////

	    /**
	     * Initie une connexion à la BDD MySQL.
	     */
	    private function connect()
	    {
	        require_once 'config.php';

	        // connecting to mysql
	        $this->db = mysql_connect(DB_HOST, DB_USER, DB_PASSWORD);
	        mysql_select_db(DB_DATABASE, $this->db);
	    }

	    /**
	     * Ferme une connexion à la BDD.
	     */
	    private function close() {
	        mysql_close($this->db);
	    }

	    /////////////////////////////////////////////////////////////////////////////////////////
		/// METHODES PUBLIQUES
		/////////////////////////////////////////////////////////////////////////////////////////

	    /**
         * Récupérer tout le contenu d'une table.
         * @return Le résultat de la requête SQL.
         */
        public function getTable($table_name)
        {
        	$sql = "SELECT * FROM ".$table_name;
            $result = mysql_query($sql) or die('Erreur SQL !<br>'.$sql.'<br>'.mysql_errno());

            return $result;
        }

	}

 ?>