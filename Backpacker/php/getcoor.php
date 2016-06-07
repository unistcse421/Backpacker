<?php
        $connection = mysqli_connect('localhost','cs20111412','20111412','Backpacker')
                                                or die ("connection failed");

if(isset($_REQUEST["userid"]) ){
        $userid = $_REQUEST["userid"];

        $query = "select latitude, longitude from photo_info
                        where user_num = '".$userid."'";

        $result = mysqli_query($connection, $query);

        if($result){
		while($data_row = mysqli_fetch_array($result)){
			if($data_row['latitude'] && $data_row['longitude'])
       	                	echo $data_row['latitude']."\n".$data_row['longitude']."\n";
	    	        else{
       	                	echo -1;
       	                 	return;
       	         	}
        	}
	}
        else
                echo -1;

        mysqli_close($connection);
}
?>

