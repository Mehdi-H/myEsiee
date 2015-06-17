/* 
* @Author: Mehdi-H
* @Date:   2015-06-17 16:52:41
* @Last Modified by:   Mehdi-H
* @Last Modified time: 2015-06-17 20:14:44
*/

myApp.filter('type', function() {

  return function(type) {

	if(type == 'it'){
		return "mdi-hardware-desktop-windows";
	}else if(type == 'elec'){
		return "mdi-image-flash-on";
	}else{
		return "mdi-toggle-check-box-outline-blank";
	}

  }
});

myApp.filter('projecteur', function() {

  return function(proj) {

	proj = parseInt(proj);
	if(proj == 1){
		return "mdi-av-videocam";
	}else{
		return "";
	}

  }
});

myApp.filter('tableau', function() {

  return function(tableau) {

	tableau = parseInt(tableau);
	if(tableau == 1){
		return "mdi-image-panorama-fisheye";
	}else if(tableau == 2){
		return "mdi-image-lens";
	}else if(tableau == 3){
		return "mdi-toggle-radio-button-on";
	}else{
		return "";
	}
  }
});

myApp.filter('imprimante', function() {

  return function(number) {

	number = parseInt(number);
	if(number == 1){
		return "mdi-maps-local-print-shop";
	}else{
		return "";
	}

  }
});

myApp.filter('taille', function() {

  return function(taille) {

	if(taille == 'petite'){
		return "mdi-social-person";
	}else if(taille == 'grande'){
		return "mdi-social-group-add";
	}else{
		return "mdi-social-group";
	}

  }
});

myApp.filter('occupation', function() {

  return function(o) {

	o = parseInt(o);
	if(o == 0){
		return "Libre";
	}else if(o == -1){
		return "Occupée";
	}else{
		return "Libre "+o+"min";
	}
  }
});

myApp.filter('encodeURIComponent', function() {

  return function(s) {
  	return encodeURIComponent(s);
  }
});