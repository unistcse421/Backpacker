<?php
	$connect = mysqli_connect( "localhost", "cs20111412","20111412","Backpacker") or die ("fail db");

	mysqli_query($connect, "SET NAMES UTF8");
 
	session_start();

	$sql = "select count(user_num) from user_info where '$_REQUEST[email]' = e_mail";
	$result = mysqli_query($connect, $sql);
	$row = mysqli_fetch_array($result);
	echo $row[0];
	if($row[0] == 0 && $_REQUEST[pw]){
		$sql = "insert into user_info(e_mail,password) values('$_REQUEST[email]',password('$_REQUEST[pw]'))";
		mysqli_query($connect, $sql);
	}
	mysqli_close($connect);
?>
