<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-05-28 19:33:13
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-05-31 17:31:44
 */

if (isset($_GET['func'], $_GET['login'], $_GET['pwd'])) {
		// JSON header
		header("Content-type: application/json; charset=utf-8");

		if($_GET['func'] == 'grades' || $_GET['func'] == 'absences'){

			$login = $_GET['login'];
			$pwd = $_GET['pwd'];
			$func = $_GET['func'];
			$filename = $func.'.json';

			$command = "python3 script_aurion.py " . $login . " " . $pwd . " " . $func . " 2>&1";
			$r = shell_exec($command);
			
			$js = file_get_contents($filename);
			echo($js);
			unlink($filename);
		}
		else{
			echo("Erreur : " . $_GET['func'] . " non reconnu.");
		}
}else {
	echo("login & pwd requis");
}

?>
