/* 
* @Author: Mehdi-H
* @Date:   2015-06-12 14:12:28
* @Last Modified by:   Mehdi-H
* @Last Modified time: 2015-06-16 18:01:11
*/

function room_suggest(e){
	
	var room = $.trim(e.value);

	$.ajax({
		method: "POST",
		url: 'index.php',
		data: {'query_room' : room},
		dataType: 'json',
		beforeSend : function(){
			console.log({query_room : room});
		},
		success : function( data ) {
			if(!room){
				$('#salle_list').css('display','none');
			}else{
				$('#salle_list').empty();
				$.each(data, function(i, item) {
					if (item['nom']){
						$('#salle_list').append('<li class="collection-item room_suggestion"><a href="fiche.php?salle='+encodeURIComponent(item['nom'])+'">'+item['nom']+'</a><li>');
						$('#salle_list').css('display','initial');
					}else{
						$('#salle_list').css('display','none');
					}
				})
			}
		},
		error : function(xhr, textStatus, error){
			console.log(xhr.statusText);
			console.log(textStatus);
			console.log(error);
		}
	})
	.done(function(){
		// console.log('done');
	});
}