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

DROP TABLE IF EXISTS contribution;
CREATE TABLE contribution (
	idcontribution		int(11) PRIMARY KEY AUTO_INCREMENT,
	login				varchar(10) DEFAULT NULL,
	type_contribution	varchar(30) NOT NULL,
	date_contribution	datetime NOT NULL,
	navigateur			varchar(30) DEFAULT NULL,
	version_android		varchar(30) DEFAULT NULL,
	email				varchar(50) DEFAULT NULL,
	location			varchar(80) DEFAULT NULL,
	contenu				text NOT NULL,
	statut				varchar(30) DEFAULT 'Nouveau'
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
