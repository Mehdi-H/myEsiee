<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-05-28 19:33:13
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-17 15:23:10
 */

if (isset($_POST['func'], $_POST['login'], $_POST['pwd'])) {

	$param = array(
		'grades', 'absences', 'appreciations', 'old_grades', 'old_absences', 'old_appreciations'
	);
	if(in_array($_POST['func'], $param)){

		$login = $_POST['login'];
		$pwd = $_POST['pwd'];
		$func = $_POST['func'];
		$filename = $func.'.json';

		$command = "python3 script_aurion.py " . $login . " " . $pwd . " " . $func . " 2>&1";
		$r = shell_exec($command);
		echo($r);

		$js = file_get_contents($filename);
		// JSON header
		header("Content-type: application/json; charset=utf-8");
		header('Access-Control-Allow-Origin: *');
		echo($js);
		unlink($filename);
		unlink('out.png');
	}
	else{
		echo("Erreur : " . $_POST['func'] . " non reconnu.");
	}
}else {
	var_dump($GLOBALS);
	echo("login & pwd requis");
}

?>
