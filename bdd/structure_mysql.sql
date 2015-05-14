DROP TABLE IF EXISTS salle;
CREATE TABLE salle (
	nom_salle			varchar(8) PRIMARY KEY,
	resourceID_salle	int(11) unsigned NOT NULL UNIQUE,
	projecteur 			tinyint(1) unsigned NOT NULL DEFAULT 0,
	tableau 			tinyint(2) unsigned NOT NULL DEFAULT 0,
	taille 				int(11) unsigned NOT NULL DEFAULT 0,
	imprimante			tinyint(1) unsigned NOT NULL DEFAULT 0
);

DROP TABLE IF EXISTS prof;
CREATE TABLE prof (
	nom_prof			varchar(8) PRIMARY KEY,
	resourceID_prof		int(11) unsigned NOT NULL UNIQUE,
	bureau 				varchar(8) DEFAULT NULL,
	email 				varchar(30) DEFAULT NULL
);

