<?php

require_once '/db_connect.php';

$connection = mysqli_connect('localhost','root','111111','test');

if($connection)
	echo "connected<br />\n";
else
	echo "fail connect<br />\n";

if(isset($_GET["name"])){

	$name = $_GET["name"];

	echo "input : ".$name."<br />\n";

	$query = "SELECT * FROM photos WHERE name = '".$name."'";

	echo $query."<br />\n";

	$result = mysqli_query($connection,$query);

	if($result)
		echo "query accepted<br />\n";
	else
		echo "query failed<br />\n";

	if(!empty($result)){

		echo "result is not empty<br />\n";

		while($data_row = mysqli_fetch_array($result)){
		
		print_r($data_row);

		echo "<br />\n";

		}

/*		if(mysql_num_row($result) > 0){
			$result = mysqli_fetch_array($result);

			$photo = array();
			$photo["name"] = $result["name"];
			$photo["id"] = $result["id"];
			$photo["path"] = $result["path"];

			print_r($photo);
		}
		else{
			echo "No photo found 1<br />\n";
		}*/







	}
	else{
		echo "No photo found 2<br />\n";
	}
}
else{
	echo "Required field is missing";
}

	mysqli_close($connection);

?>