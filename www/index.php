<?php
	if (isset($_GET['config']))
	{
		if (strcmp($_GET['config'], 'mvx2themeute') == 0) {
			phpinfo();
			return;
		}
	}

	echo 'Hello';

?>
