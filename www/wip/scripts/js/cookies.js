/* 
* @Author: Mehdi-H
* @Date:   2015-06-09 19:48:04
* @Last Modified by:   Mehdi-H
* @Last Modified time: 2015-06-11 10:27:48
*/

$(document).ready(function(){
	$('#deconnexion').click(function(e){
		e.preventDefault();
		$.removeCookie('eroom', { path: '/wip' });
		Materialize.toast('Vous êtes déconnecté !', 4000);
		setTimeout(function(){window.location='connexion.php'} , 1000); 
	});
});