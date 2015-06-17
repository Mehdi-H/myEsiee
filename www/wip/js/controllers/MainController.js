/* 
* @Author: Mehdi-H
* @Date:   2015-06-17 13:44:41
* @Last Modified by:   Mehdi-H
* @Last Modified time: 2015-06-17 19:50:58
*/

myApp.controller('MainController', ['$scope','$http', function($scope, $http) { 

	$http.get('salle.json').success(function(data){
		$scope.salles = data;
		// console.log(data);
	});

	$http.get('https://mvx2.esiee.fr/api/ade.php?func=rechSalle').success(function(data){

		$scope.libres = data;

	});
 
}]);