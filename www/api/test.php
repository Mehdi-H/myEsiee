<?php

	// Afficher les erreurs :
	error_reporting(E_ALL);
	ini_set('display_errors', 1);

	$ch = curl_init();

	curl_setopt_array($ch, array(
		CURLOPT_RETURNTRANSFER => true,
		CURLOPT_SSL_VERIFYPEER => false,
		CURLOPT_SSL_VERIFYHOST => false,
		CURLOPT_FOLLOWLOCATION => true,
		CURLOPT_PROXY => 'https://dupontl:mvx2dupontl@mvproxy.esiee.fr:3128',
		//CURLOPT_URL => "https://aurionprd.esiee.fr/faces/Login.xhtml",
		//CURLOPT_URL => "https://icampus.esiee.fr/",
		CURLOPT_URL => "https://planif.esiee.fr:8443/jsp/webapi?function=connect&login=lecteur1&password=",
		//CURLOPT_URL => "http://9gag.com/",
		CURLOPT_USERAGENT => 'E-Room pour ESIEE Paris (projet E3E 2015)'
	));

	$out = curl_exec($ch);
	curl_close($ch);

	echo('"'.$out.'"');

	//$google = new SimpleXMLElement($out);
 ?>
