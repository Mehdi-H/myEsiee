<?php

	/**
	 * Requêtes SQL  effectuer sur la BDD MySQL.
	 */

    class DB_Functions
    {
        private $db;

        //put your code here
        // constructor
        function __construct()
        {
            include_once './db_connect.php';

            // connecting to database
            $this->db = new DB_Connect();
            $this->db->connect();
        }

        // destructor
        function __destruct() {

        }


        /**
         * Récupérer tout le contenu d'une table.
         */
        public function getTable($table_name)
        {
            $result = mysql_query("SELECT * FROM ".$table_name);
            return $result;
        }
    }

 ?>