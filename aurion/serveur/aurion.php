<html>
<head>
	<meta charset="utf-8">
</head>

<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-05-28 19:33:13
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-05-29 17:17:54
 */

if (isset($_GET['login'], $_GET['pwd'])) {
		$login = $_GET['login'];
		$pwd = $_GET['pwd'];
		$command = "python3 script_aurion.py " . $login . " " . $pwd . " 2>&1";
		// sleep(10);
		// shell_exec("export http_proxy=http://login:mdp@mvproxy.esiee.fr:3128");
		// shell_exec("export https_proxy=http://login:mdp@mvproxy.esiee.fr:3128");
		// $r = shell_exec($command);
		$grades = file_get_contents('grades.json');
		// $grades = array_map('utf8_encode', $grades);
		// $grades = json_decode($grades);
		echo($grades);
		// print_r($data);
		// echo('<pre>'); print_r($data); echo('</pre>');
		// var_dump(get_defined_vars());
	}
	else {
		echo("login & pwd requis");
	}

?>
</html>
