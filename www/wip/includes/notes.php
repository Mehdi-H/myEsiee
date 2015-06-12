<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-09 14:42:44
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-09 15:59:41
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
		foreach ($data as $indexInArray => $gradeArray) {
			// echo($gradeArray->{'annee'});
	?>
	<li class="collection-item avatar taille_ligne">
		<?php 
			if(trim($gradeArray->{'note'}) == 'F' || trim($gradeArray->{'note'}) == 'FX'){
				echo('<i class=" white circle red-text text-darken-1 grade">');
			}else{
				echo('<i class=" white circle green-text grade ">');
			}
		?>
		<?php echo(str_replace('X','x',$gradeArray->{'note'})); ?></i>
		<span class="title "><?php echo($gradeArray->{'libelle'}); ?></span>
		<p><?php echo($gradeArray->{'unite'}); ?><br>
			Crédits : <?php echo($gradeArray->{'credit'}); ?>
		</p>
	</li>
	<?php
		}
	}
	 ?>
</ul>