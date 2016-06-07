
<?php
        function fix_string($conn, $string){
                if (get_magic_quotes_gpc()) $string = stripslashes($string);

                return $conn->real_escape_string($string);
        }

//      $e_mail = $_REQUEST['email'];
//      $password = $_REQUEST['pw'];

        $connection = mysqli_connect('localhost','cs20111412','20111412','Backpacker');

        $e_mail = fix_string($connection, $_REQUEST['email']);
        $password = fix_string($connection, $_REQUEST['pw']);

        $query = "SELECT user_num FROM user_info WHERE e_mail = '".$e_mail."'
                        and password = password('".$password."')";

        $result = mysqli_query($connection, $query);

        $data_row = mysqli_fetch_array($result);

        if($data_row['user_num'])
                echo $data_row['user_num'];
        else{
                echo -1;
                echo "\n";
                echo 0;
                return;
        }

        $query = "select count(photo_num) from photo_info where user_num = "
                        .$data_row['user_num'];
        $result = mysqli_query($connection, $query);

        $data_row = mysqli_fetch_array($result);

        echo "\n";
        echo $data_row[0];

        mysqli_close($connection);
?>

