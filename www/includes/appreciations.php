<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-09 19:16:56
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-09 19:19:23
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
		foreach ($data as $indexInArray => $appArray) {
	 ?>
	<li class="collection-item taille_ligne">
		<p><?php echo $appArray->{'commentaire'}; ?><br>
			<?php echo $appArray->{'periode'}; ?><br>
			<?php echo $appArray->{'annee'}; ?>
		</p>
	</li>
	<?php
		}
	}
	?>
</ul>