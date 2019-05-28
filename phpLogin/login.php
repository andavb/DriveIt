<?php


if($_SERVER['REQUEST_METHOD'] == 'POST'){

	$name = $_POST['usrname'];
	$pass = $_POST['password'];

	require_once 'connection.php';


	$queryCode = "SELECT username, password FROM user where username='".$name."';";

	$rezCode = mysqli_query($link, $queryCode);

	if (mysqli_num_rows($rezCode) > 0){

		while ($row = $rezCode->fetch_assoc()) {

			if(password_verify($pass, $row['password'])){

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
	else{
		@$myObj->success = "2";
		@$myObj->message = "error";

		echo json_encode($myObj);
		mysqli_close($link);
	}
}

?>