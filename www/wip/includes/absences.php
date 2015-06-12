<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-09 14:54:39
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-09 15:50:29
 */
?>

<ul class="collection">
	<?php
		if($data === NULL){
			$show_modal = true;
			// header("refresh:3;url=connexion.php");
		}else{
	 ?>
	<?php 
		foreach ($data as $indexInArray => $absenceArray) {
	?>
	<li class="collection-item avatar taille_ligne">
		<?php 
			if ($absenceArray->{'motif'} == 'Retard excusé') {
				echo('<i class="circle mdi-notification-event-available green"></i>');
			}else{
				echo('<i class="circle mdi-notification-event-busy red darken-3"></i>');
			}
		?>				
		<span class="title">
			<?php echo($absenceArray->{'activite'}
						.' - '.
						$absenceArray->{'unite'}
						.' - '.
						$absenceArray->{'code'}
					); 
			?>
		</span>
		<p>
			<?php echo($absenceArray->{'date'}
						. ' de '.
						$absenceArray->{'creneau'}
						.' - '.
						$absenceArray->{'intervenant'}
						.'<br>'.
						$absenceArray->{'motif'}
					); 
			?>
		</p>
	</li>
	<?php
		}
	}
	?>
</ul>