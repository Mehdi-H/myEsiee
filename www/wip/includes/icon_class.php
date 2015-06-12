<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-11 11:01:44
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-11 15:00:24
 */

$icons = array(
	'type' => array(
		'banal' => 'mdi-toggle-check-box-outline-blank',
		'elec'  => 'mdi-image-flash-on',
		'it'    => 'mdi-hardware-desktop-windows'
	),
	'projecteur' => array(
		'0' => '',//'mdi-av-videocam-off',
		'1' => 'mdi-av-videocam',
		'4' => '',//'mdi-av-videocam-off',
	),
	'tableau' => array(
		'0' => '',//'mdi-notification-do-not-disturb',  // aucun
		'1' => 'mdi-image-panorama-fisheye',  // blanc
		'2' => 'mdi-image-lens',  // noir
		'3' => 'mdi-toggle-radio-button-on',  // les 2
		'4' => '',//'mdi-notification-do-not-disturb',  // à checker
	),
	'imprimante' => array(
		'0' => '',
		'1' => 'mdi-maps-local-print-shop',
		'4' => '',
	),
	'taille' => array(
		'petite'  => 'mdi-social-person',
		'moyenne' => 'mdi-social-group',
		'grande'  => 'mdi-social-group-add'
	)

);

$title = array(
	'type' => array(
		'banal' => 'Salle de cours',
		'elec'  => 'Labo d\'électronique',
		'it'    => 'Salle info'
	),
	'projecteur' => 'Projecteur',
	'tableau' => array(
		'0' => '',  // aucun
		'1' => 'Tableau à feutre',  // blanc
		'2' => 'Tableau à craie',  // noir
		'3' => 'Tableau feutre & craie',  // les 2
		'4' => '',//'mdi-notification-do-not-disturb',  // à checker
	),
	'imprimante' => 'Imprimante',
	'taille' => array(
		'petite'  => 'Petite salle',
		'moyenne' => 'Salle de taille moyenne',
		'grande'  => 'Grande salle'
	)

);

?>