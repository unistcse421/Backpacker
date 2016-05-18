<?php

  session_strat();

//  echo "session started<br />\n";

  if($_SESSION['auth_user'] == 1){
	echo "already login";
  }
  else{

	  if(isset($_POST['email']) && isset($_POST['pw'])){

	      $email = $_POST['email'];
	      $pw = $_POST['pw'];

	      $connection = mysqli_connect('localhost','root','111111','test');

	      $query = "SELECT * FROM user_info WHERE e_mail = '".$email."' and pw = '".$pw."'";

	      $result = mysqli_query($connection, $query);

	      $data_row = mysqli_fetch_array($result);

	      if($email == $data_row['e_mail'] && $pw == $data_row['pw']){
		$_SESSION['email'] = $data_row['email'];
		$_SESSION['pw'] = $data_row['pw'];
	        echo $data_row['User_num'];
	      }
 	     else{
	       echo -1;
	      }

      mysqli_close($connection);
  }
  else{
	echo "cannot find email and pw<br />\n";
  }
}

?>
