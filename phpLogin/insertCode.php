<?php


if($_SERVER['REQUEST_METHOD'] == 'POST'){

	$code = "1234";

	$hashedCode = password_hash($code, PASSWORD_DEFAULT);

	require_once 'connection.php';

	$query = "INSERT INTO codes(code) VALUES('".$hashedCode."');";

	if (mysqli_query($link, $query)) {
		echo "uspelo";
	}
	else{
		echo "ni uspelo";
	}
}

?>