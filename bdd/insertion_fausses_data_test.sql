INSERT INTO salle
	(nom, resourceID, type, taille, projecteur, tableau, imprimante)
VALUES
	("5004", 789, "it", 24, 0, 2, 0),
	("5006", 790, "it", 24, 0, 2, 0),
	("5008V+", 786, "it", 26, 1, 2, 0),
	("5201V", 659, "it", 24, 1, 2, 0),
	("2101", 183, "vid", 12, 1, 2, 0);

INSERT INTO prof
	(nom, resourceID, bureau, email)
VALUES
	("HABIB E.", 286, "1234", "elia.habib@esiee.fr"),
	("EL HABIBI A.", 492, "5678", "a.elhabibi?@esiee.fr"),
	("REILLE L.", 129, "9101", "leila.reille@esiee.fr"),
	("BERCHER JF.", 75, "5254", "jf.bercher@esiee.fr"),
	("HILAIRE X.", 103, "5256", "xavier.hilaire@esiee.fr");

# Modifier les salles déjà présentes :

UPDATE salle
	SET resourceID="789"
	WHERE nom="5004";
UPDATE salle
	SET resourceID="790"
	WHERE nom="5006";
UPDATE salle
	SET resourceID="786"
	WHERE nom="5008V+";
UPDATE salle
	SET resourceID="659"
	WHERE nom="5201V";
UPDATE salle
	SET resourceID="183"
	WHERE nom="2101";
