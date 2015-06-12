<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-04 16:09:25
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-11 10:36:15
 */

if(empty($_COOKIE['eroom'])){
	header('Location: https://mvx2.esiee.fr/wip/connexion.php');
}else{
	$data = json_decode($_COOKIE['eroom']);
	$login = $data->{'login'};
	$pwd = $data->{'pwd'};
}
?>