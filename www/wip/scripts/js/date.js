/* 
* @Author: Mehdi-H
* @Date:   2015-06-12 19:57:49
* @Last Modified by:   Mehdi-H
* @Last Modified time: 2015-06-16 17:04:55
*/

$(document).ready(function() {

	$('.datepicker').pickadate({
		monthsFull: ['Janvier', 'Février', 'Mars', 'Avril', 'Mai', 'Juin', 'Juillet', 'Août', 'Septembre', 'Octobre', 'Novembre', 'Décembre'],
		monthsShort: ['Jan', 'Fev', 'Mar', 'Avr', 'Mai', 'Juin', 'Jul', 'Aou', 'Sep', 'Oct', 'Nov', 'Dec'],
		weekdaysFull: ['Dimanche', 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'],
		weekdaysShort: ['Dim', 'Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam'],

		// Buttons
		today: "Aujourd'hui",
		clear: 'X',
		close: 'Fermer',

		// Formats
		format: 'dd/mm/yyyy',

		// Accessibility labels
		labelMonthNext: 'Mois prochain',
		labelMonthPrev: 'Mois précédent',
		labelMonthSelect: 'Choisir un mois',
		labelYearSelect: 'Choisir une année',

	    selectMonths: true, // Creates a dropdown to control month
	    selectYears: 15 // Creates a dropdown of 15 years to control year
	});

	$('#date_salle').on('change',function(){
		var d = $('#date_salle').val();
		var d_array = d.split('/');
		var date_api = d_array[1]+'/'+d_array[0]+'/'+d_array[2];
		$('#edt_salle').attr(
			'src', $('#edt_salle').attr('src').slice(0,-10)+date_api
		);

		$('.datepicker').Close();
	});
});