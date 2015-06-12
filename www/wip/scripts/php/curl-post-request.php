<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-06 09:59:51
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-09 19:25:56
 */
if(isset($_GET['q'])){

	$p = $_GET['q'];

	$arr = array(
		'notes'                   => 'grades',
		'notes_archivees'         => 'old_grades',
		'absences'                => 'absences',
		'absences_archivees'      => 'old_absences',
		'appreciations'		      => 'appreciations',
		'appreciations_archivees' => 'old_appreciations'
	);

	if (array_key_exists($p, $arr)) {
	    $func = $arr[$p];
	    $p = explode("_", $p)[0];
	}else{
		echo("Erreur func dans la conception de la requete cURL.");
		die();
	}

}else{
	$p = "notes";
	$func = "grades";
}

	$ch = curl_init();

	curl_setopt_array($ch, array(
		CURLOPT_RETURNTRANSFER => 1,
		CURLOPT_URL => 'https://mvx2.esiee.fr/api/aurion.php',
		CURLOPT_USERAGENT => 'E-Room pour ESIEE Paris (projet E3E 2015)',
		CURLOPT_FOLLOWLOCATION => 1,
		CURLOPT_SSL_VERIFYPEER => false,
		CURLOPT_POSTFIELDS => array(
		    'func' => $func,
		    'login' => $login,
		    'pwd' => $pwd
	    	)
		)
	);

	$out = curl_exec($ch); // Exécution
	curl_close($ch); // Fermeture

	$data = json_decode($out);

?>