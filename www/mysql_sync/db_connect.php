<?php

	/**
	 * Méthodes pour ouvrir ou fermer une connexion à la BDD MySQL
	 */

	class DB_Connect
	{
	    // constructor
	    function __construct() {

	    }

	    // destructor
	    function __destruct() {
	        // $this->close();
	    }

	    /**
	     * Initie une connexion à la BDD MySQL.
	     * @return Handler de la BDD
	     */
	    public function connect()
	    {
	        require_once 'config.php';

	        // connecting to mysql
	        $con = mysql_connect(DB_HOST, DB_USER, DB_PASSWORD);
	        mysql_select_db(DB_DATABASE);

	        // return database handler
	        return $con;
	    }

	    /**
	     * Ferme une connexion à la BDD.
	     */
	    public function close() {
	        mysql_close();
	    }

	}

 ?>