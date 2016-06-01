<?php

// session_start();
// echo "session started<br />\n";

if(isset($_POST['email']) && isset($_POST['pw']) && isset($_POST['pw_'])){

	$email = $_POST['email'];
	$pw = $_POST['pw'];
	$pw_= $_POST['pw_'];

	if($pw == $pw_){
	
		$connection = mysqli_connect('localhost','root','111111','test');
		$query = "INSERT INTO user_info('email','pw') values('$email','$pw');";

		echo $query."<br />\n";

                $result = mysqli_query($connection, $query);

                if($result){
                        echo "success";
                }else{
                        echo "failed";
                }

                mysqli_close($connection);
	}
	else{
		echo "pw and pw_ are different<br />\n";
//		echo -1;
	}
}

?>