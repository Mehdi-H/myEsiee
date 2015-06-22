<?php

if(isset($_POST['login'], $_POST['pwd'])){

	$l = $_POST['login'];
	$p = $_POST['pwd'];

	$cookie_aurion = array(
		'login' => $l,
		'pwd' => $p
	);

	$cookie_aurion = json_encode($cookie_aurion);

	setcookie("eroom", "", time() - 3600);

	setcookie("eroom", $cookie_aurion, time() + (86400 * 30), '/');

	// header("refresh:0;url=".basename($_SERVER['PHP_SELF']));
}

?>