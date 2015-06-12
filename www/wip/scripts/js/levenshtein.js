/* 
* @Author: Mehdi-H
* @Date:   2015-06-12 16:21:39
* @Last Modified by:   Mehdi-H
* @Last Modified time: 2015-06-12 19:18:57
*/

function lv_sort(e){

    var nom = $.trim(e.value);

    $.ajax({
        method: "GET",
        url: 'scripts/php/levenshtein.php',
        data: {'nom' : nom},
        dataType: 'json',
        beforeSend : function(){
            // console.log({query_room : room});
        },
        success : function( data ) {
            if(data.length === 0 || nom.length === 0){
                if($('#prof_results').hasClass('visible')){
                    $('#prof_results').addClass('unvisible').removeClass('visible');
                }
            }else{
                // console.log(data);
                if($('#prof_results').hasClass('unvisible')){
                    $('#prof_results').addClass('visible').removeClass('unvisible');
                }
                $('#prof_results').empty();
                for (i = 0; i < data.length; i++) {
                    $('#prof_results').append('<li class="collection-item">'+ data[i] +'</li>');
                }
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

$('#prof_results').on('click','li',function(){
        
        var nom = $(this).text();
        console.log(nom);
        $('#prof_results').empty();
        console.log("https://mvx2.esiee.fr/wip/recherche_professeur.php?q=" + nom + " #ficheprof");
        window.location.href="https://mvx2.esiee.fr/wip/recherche_professeur.php?q=" + encodeURIComponent(nom);
});