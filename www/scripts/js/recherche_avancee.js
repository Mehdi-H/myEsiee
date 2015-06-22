/* 
* @Author: Mehdi-H
* @Date:   2015-06-16 14:15:32
* @Last Modified by:   Mehdi-H
* @Last Modified time: 2015-06-16 15:00:31
*/

$(document).ready(function() {
	$('#z_index_bouton').on('click', function(e){

		e.preventDefault();

		console.log('hello');

		$.ajax({
			method: "POST",
			url: 'recherche_avancee.php',
			data: $("#form_recherche_avancee").serialize(),
			// dataType: 'json',
			beforeSend : function(){
				console.log($("#form_recherche_avancee").serialize());
			},
			success : function( data ) {
				
				var a = JSON.parse(data);
				console.log(a);
				$('#salle_list').empty();
				if(a.length != 0){
					for (var i = 0; i < data.length; i++) {
						for(room in a[i]){
							$('#salle_list').append('<li class="collection-item room_suggestion"><a href="fiche.php?salle='+room.replace("+","%2B")+'">'+room+'</a><li>');
						}
					}
				}else{
					$('#salle_list').append('<p>Aucun salle libre en ce moment ne correspond à ces critères.</p>');
				}
				$('#salle_list').css('display','initial');
				
			},
			error : function(xhr, textStatus, error){
				console.log(xhr.responseText);
				console.log(xhr.statusText);
				console.log(textStatus);
				console.log(error);
			}
		})
		.done(function(){
			// console.log('done');
		});

		return false;
	});
});