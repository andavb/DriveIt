<?php

if($_SERVER['REQUEST_METHOD'] == 'POST'){

	$name = mysqli_real_escape_string($link, $_POST['usrname']);
	$pass = mysqli_real_escape_string($link, $_POST['password']);
	$code = mysqli_real_escape_string($link, $_POST['code']);

	$pass = password_hash($pass, PASSWORD_DEFAULT);

	require_once 'connection.php';

	$queryCode = "SELECT code FROM codes";

	$rezCode = mysqli_query($link, $queryCode);

	if (mysqli_num_rows($rezCode) > 0){

		while ($row = $rezCode->fetch_assoc()) {

			if(password_verify($code, $row['code'])){

				$query = "INSERT INTO user(username, password, code) VALUES('".$name."','".$pass."', '".$row['code']."');";

				if (mysqli_query($link, $query)) {
					@$myObj->success = "1";
					@$myObj->message = "success";

					echo json_encode($myObj);
					mysqli_close($link);
					break;


				}
				else{
					@$myObj->success = "0";
					@$myObj->message = "error";

					echo json_encode($myObj);
					mysqli_close($link);
					break;

				}
			}
   		}

	}
	else{
		@$myObj->success = "0";
		@$myObj->message = "error";

		echo json_encode($myObj);
		mysqli_close($link);
	}
}

?>