/**
 * Requetes pour créer les tables sur le serveur.
 */
DROP TABLE IF EXISTS salle;
CREATE TABLE salle (
	nom					varchar(15) PRIMARY KEY,
	resourceID 			int(11) unsigned UNIQUE DEFAULT NULL,
	type 				varchar(30),
	taille 		 		int(11) unsigned NOT NULL DEFAULT 0,
	projecteur 			tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '0:non, 1:oui',
	tableau 			tinyint(2) unsigned NOT NULL DEFAULT 0 COMMENT '0:aucun, 1:blanc, 2:noir, 3:deux',
	imprimante			tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '0:non, 1:oui'
);

DROP TABLE IF EXISTS prof;
CREATE TABLE prof (
	nom					varchar(30) PRIMARY KEY,
	resourceID			int(11) unsigned UNIQUE DEFAULT NULL,
	bureau 				varchar(15) DEFAULT NULL,
	email 				varchar(30) DEFAULT NULL
);


/**
 * Requete pour ajout dans ma bdd locale pour tests.
 */
CREATE TABLE salle (
      nom varchar(8) PRIMARY KEY,
      resourceID int(20) unsigned NOT NULL UNIQUE,
      type varchar(30),
      taille int(11) unsigned NOT NULL DEFAULT 0,
      projecteur tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '0:non, 1:oui',
      tableau tinyint(2) unsigned NOT NULL DEFAULT 0 COMMENT '0:aucun, 1:blanc, 2:noir, 3:deux',
      imprimante tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '0:non, 1:oui'
    ) ENGINE=InnoDB
