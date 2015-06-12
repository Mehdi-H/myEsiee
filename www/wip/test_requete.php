<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-01 10:31:33
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-01 11:22:48
 */

$salles = json_decode(file_get_contents('https://mvx2.esiee.fr/api/ade.php?func=rechSalle'),TRUE);

// var_dump($salles);

$r = 0;

$item = '<a class="validate collection-item" href="#!">
					<ul class="collection valign-wrapper reduc_liste_salle">
						<li class="tab col s1 offset-s2">
							<span class="valign right">
								'. strval($r) .'
							</span>
						</li>
						<li class="tab col s4">
							<i class="small mdi-hardware-desktop-windows primary-text-color">
							</i>
							<i class="small mdi-social-school primary-text-color">
							</i>
							<i class="small mdi-notification-sd-card primary-text-color">
							</i>
							<i class="small mdi-notification-sd-card primary-text-color">
							</i>
						</li>
						<li class="tab col s1 offset-s1">
							<span class="valign right">30min
							</span>
						</li>
					</ul>
				</a>';

foreach ($salles as $indexInArray => $room) {
	foreach ($room as $roomNumber => $roomState) {
		echo('<a class="validate collection-item" href="#!">
					<ul class="collection valign-wrapper reduc_liste_salle">
						<li class="tab col s1 offset-s2">
							<span class="valign right">
								'. strval($roomNumber) .'
							</span>
						</li>
						<li class="tab col s4">
							<i class="small mdi-hardware-desktop-windows primary-text-color">
							</i>
							<i class="small mdi-social-school primary-text-color">
							</i>
							<i class="small mdi-notification-sd-card primary-text-color">
							</i>
							<i class="small mdi-notification-sd-card primary-text-color">
							</i>
						</li>
						<li class="tab col s1 offset-s1">
							<span class="valign right">30min
							</span>
						</li>
					</ul>
				</a>');
	}
}
?>