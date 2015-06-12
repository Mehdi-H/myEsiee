<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-12 17:51:24
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-12 18:23:39
 */
if(isset($_GET['nom'])){
	include('../../includes/bdd_connect.php');

	$names_lv = array();

	$conn->query("SET CHARACTER SET utf8");
	$stmt = $conn->prepare('SELECT nom FROM prof');
	$stmt->execute();
	$prof_list = $stmt->fetchAll();

	foreach ($prof_list as $i => $prof) {
		$names_lv[$prof['nom']] = levenshtein(explode(" ", $prof['nom'], 2)[0],strtoupper($_GET['nom']));
	}

	asort($names_lv);
	$result = array_keys(array_slice($names_lv, 0, 5));
	// var_dump(array_keys(array_slice($names_lv, 0, 5)));
	header('Content-Type: application/json; charset=utf-8');
	echo(json_encode($result, JSON_UNESCAPED_UNICODE));
	// var_dump($names_lv);
}else{
	echo('?nom=');
}

?>