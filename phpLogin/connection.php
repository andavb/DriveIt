<?php


$user = 'root';
$password = 'root';
$db = 'driveit';
$host = 'localhost';
$port = 8889;


$link = mysqli_init();
$conn = mysqli_real_connect(
   $link,
   $host,
   $user,
   $password,
   $db,
   $port
);

mysqli_query($link, "SET NAMES UTF8");



if(!$link)
	{
	    echo "Ne moremo se povezati z MySQL: " . mysqli_connect_error();
	    mysqli_close($link);
	    exit;
	}
?>