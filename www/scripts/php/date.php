<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-12 19:50:08
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-12 20:00:40
 */

function datepicker_to_fr_date(s){
	$time = strtotime(s);
	$newformat = date('d/m/y',$time);
	return $newformat;
}

?>