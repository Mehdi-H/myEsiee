<?php
/**
 * @Author: Mehdi-H
 * @Date:   2015-06-09 20:02:10
 * @Last Modified by:   Mehdi-H
 * @Last Modified time: 2015-06-12 20:00:17
 */
?>

<script src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
<script src="js/materialize.js"></script>
<script src="js/init.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-cookie/1.4.1/jquery.cookie.js"></script>
<script src="scripts/js/cookies.js"></script>
<script src="scripts/js/rooms.js"></script>
<script src="scripts/js/levenshtein.js"></script>
<script src="scripts/js/date.js"></script>
<script>
	$(document).ready(function(){
		// the "href" attribute of .modal-trigger must specify the modal ID that wants to be triggered
		$('.modal-trigger').leanModal();
	});
</script>