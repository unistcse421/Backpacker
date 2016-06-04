<?php

$connection = mysqli_connect('localhost','cs20111412','20111412','Backpacker') or die ("connect failed");

if(isset($_REQUEST["photo_name"])){

	$photo_name = $_REQUEST["photo_name"];

	$query = "SELECT * FROM photo_info WHERE photo_name = '".$photo_name."'";

	$result = mysqli_query($connection,$query);

	if($result)
		echo "query accepted<br />\n";
	else
		echo "query failed<br />\n";

	if(!empty($result)){

		while($data_row = mysqli_fetch_array($result)){
		
		print_r($data_row);

		echo "<br />\n";

		}
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
