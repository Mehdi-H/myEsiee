-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Client: localhost
-- Généré le: Mar 26 Mai 2015 à 13:33
-- Version du serveur: 5.5.43-0ubuntu0.14.04.1
-- Version de PHP: 5.5.9-1ubuntu4.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de données: `eroom`
--

-- --------------------------------------------------------

--
-- Structure de la table `salle`
--

CREATE TABLE IF NOT EXISTS `salle` (
  `nom` varchar(8) NOT NULL,
  `resourceID` bigint(20) unsigned NOT NULL,
  `type` varchar(30) DEFAULT NULL,
  `taille` int(11) unsigned NOT NULL DEFAULT '0',
  `projecteur` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:non, 1:oui',
  `tableau` tinyint(2) unsigned NOT NULL DEFAULT '0' COMMENT '0:aucun, 1:blanc, 2:noir, 3:deux',
  `imprimante` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:non, 1:oui',
  PRIMARY KEY (`nom`),
  UNIQUE KEY `resourceID` (`resourceID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Contenu de la table `salle`
--

INSERT INTO `salle` (`nom`, `resourceID`, `type`, `taille`, `projecteur`, `tableau`, `imprimante`) VALUES
('0110', 139665119367776, 'Amphithéatres', 156, 0, 1, 2),
('0112', 139665119368392, 'Salles', 57, 0, 1, 2),
('0113', 139665119367608, 'Salles', 58, 0, 1, 2),
('0114', 139665119586376, 'Salles', 58, 0, 1, 2),
('0115', 139665119586264, 'Salles', 58, 0, 1, 2),
('0160', 139665119587048, 'Amphithéatres', 156, 0, 1, 2),
('0162V', 139665119587216, 'Salles', 58, 0, 1, 2),
('0163V', 139665119587440, 'Salles', 57, 0, 1, 2),
('0164V', 139665119587608, 'Salles', 57, 0, 1, 2),
('0165V', 139665119587720, 'Salles', 57, 0, 1, 2),
('0210', 139665119588112, 'Amphithéatres', 178, 0, 1, 2),
('0260', 139665114767408, 'Amphithéatres', 178, 0, 1, 2),
('1001', 139665119588280, 'Labo', 0, 0, 0, 0),
('1005', 139665114767800, 'Salles', 407, 0, 1, 2),
('1007+', 139665114767912, 'Salles', 0, 0, 1, 2),
('1051', 139665114767856, 'Salles', 0, 0, 0, 2),
('1055', 139665114768640, 'Labo', 0, 0, 0, 0),
('1103', 139665114768976, 'Salles', 50, 0, 0, 1),
('1105', 139665114768864, 'Salles', 50, 0, 0, 1),
('1107V', 139665114769424, 'Salles', 50, 0, 0, 1),
('1109V', 139665114769480, 'Salles', 50, 0, 0, 1),
('1201+', 139665114769816, 'Salles', 100, 0, 0, 1),
('1205V', 139665114769928, 'Salles', 50, 0, 0, 1),
('1207V', 139665114770320, 'Salles', 50, 0, 0, 1),
('1209V !!', 139665114770376, 'Salles', 50, 0, 0, 1),
('1301+', 139665114770712, 'Salles', 50, 0, 1, 2),
('1305', 139665114771160, 'Salles', 50, 0, 0, 2),
('1307', 139665114771272, 'Salles', 50, 0, 0, 1),
('1309', 139665114775600, 'Salles', 50, 0, 0, 2),
('1401', 139665114775992, 'Salles', 50, 0, 0, 2),
('1403', 139665114776104, 'Salles', 50, 0, 0, 2),
('1405', 139665114776496, 'Salles', 50, 0, 0, 2),
('1407', 139665114776328, 'Salles', 49, 0, 0, 2),
('1409', 139665114776720, 'Salles', 50, 0, 0, 1),
('2101', 139665114776944, 'Salles', 25, 0, 0, 1),
('2102', 139665114777168, 'Salles', 25, 0, 0, 1),
('2103', 139665114777616, 'Salles', 25, 0, 0, 1),
('2104', 139665114777672, 'Labo', 25, 0, 0, 1),
('2105', 139665114777840, 'Labo', 48, 0, 0, 1),
('2107', 139665114778064, 'Labo', 25, 0, 0, 1),
('2108', 139665114778288, 'Salles', 24, 0, 1, 2),
('2201+', 139665114778736, 'Salles', 49, 0, 0, 1),
('2205', 139665114778960, 'Salles', 48, 0, 0, 2),
('2207', 139665114779184, 'Salles', 48, 0, 0, 2),
('2209', 139665114779464, 'Salles', 50, 0, 0, 2),
('2305', 139665114779920, 'Labo', 48, 0, 0, 1),
('2309-', 139665114780480, 'Labo', 50, 0, 0, 0),
('2401V+', 139665114780704, 'Labo', 99, 0, 0, 1),
('2409V', 139665114781208, 'Labo', 50, 0, 0, 0),
('3001', 139665114780928, 'Salles', 407, 0, 0, 2),
('3005', 139665114781376, 'Salles', 0, 0, 0, 2),
('3007', 139665114781544, 'Salles', 0, 0, 0, 2),
('3051', 139665114781712, 'Salles', 0, 0, 0, 2),
('3053', 139665114781936, 'Salles', 0, 0, 0, 2),
('3055', 139665114782440, 'Salles', 0, 0, 1, 2),
('3103', 139665114782496, 'Salles', 48, 0, 0, 2),
('3105', 139665114782608, 'Salles', 46, 0, 0, 1),
('3107', 139665114783000, 'Salles', 49, 0, 0, 1),
('3109', 139665114782832, 'Salles', 50, 0, 0, 1),
('3201', 139665114783224, 'Salles', 50, 0, 0, 2),
('3203', 139665114783448, 'Salles', 50, 0, 0, 2),
('3207+', 139665114787944, 'Salles', 99, 0, 0, 2),
('3301', 139665114788112, 'Salles', 92, 0, 0, 2),
('3305', 139665114788280, 'Salles', 48, 0, 0, 2),
('3307+', 139665114788784, 'Salles', 99, 0, 0, 2),
('3401V+', 139665114788896, 'Labo', 99, 0, 0, 1),
('3407', 139665114789344, 'Labo', 96, 0, 0, 2),
('4003', 139665114789064, 'Labo', 44, 0, 1, 2),
('4005', 139665114789568, 'Labo', 44, 0, 1, 2),
('4007', 139665114789624, 'Labo', 172, 0, 1, 2),
('4105V', 139665114789960, 'Labo', 164, 0, 0, 0),
('4109', 139665114790128, 'Labo', 50, 0, 1, 2),
('4201+', 139665114790296, 'Salles', 99, 0, 0, 2),
('4307+', 139665114791304, 'Salles', 99, 0, 0, 2),
('4351', 139665114791472, 'Labo', 36, 0, 1, 2),
('4401', 139665114791696, 'Salles', 470, 0, 0, 2),
('4403', 139665114791752, 'Salles', 30, 0, 0, 2),
('4405-5', 139665114796472, 'Salles', 200, 0, 0, 0),
('4451-3', 139665114796304, 'Salles', 66, 0, 0, 2),
('5004', 139665114796976, 'Salles', 46, 0, 0, 1),
('5006', 139665114797368, 'Salles', 46, 0, 0, 1),
('5008V+', 139665114797200, 'Salles', 69, 0, 0, 1),
('5101', 139665114797760, 'Labo', 49, 0, 0, 1),
('5103', 139665114797872, 'Labo', 49, 0, 0, 1),
('5105', 139665114798376, 'Labo', 48, 0, 0, 1),
('5107', 139665114798096, 'Labo', 99, 0, 0, 1),
('5155', 139665114798936, 'Labo', 71, 0, 0, 1),
('5201V', 139665114799328, 'Labo', 99, 0, 0, 1),
('5207', 139665114799048, 'Labo', 45, 0, 0, 4),
('5209', 139665114799608, 'Labo', 50, 0, 0, 4),
('5301V++', 139665114799720, 'Labo', 99, 0, 0, 1),
('5309V++', 139665114800400, 'Labo', 50, 0, 0, 1),
('5401V', 139665114800176, 'Labo', 97, 0, 1, 2),
('5407', 139665114800848, 'Labo', 95, 0, 0, 0),
('6301', 139665114801632, 'Labo', 26, 0, 1, 2),
('6401', 139665114801968, 'Labo', 99, 0, 0, 0),
('6409V', 139665114802136, 'Labo', 50, 0, 1, 2);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
