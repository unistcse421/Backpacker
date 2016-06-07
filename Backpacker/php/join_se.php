<?php
        function fix_string($conn, $string){
                if (get_magic_quotes_gpc()) $string = stripslashes($string);

                return $conn->real_escape_string($string);
        }

        $connect = mysqli_connect( "localhost", "cs20111412","20111412","Backpacker")
                                                                or die ("fail db");

        mysqli_query($connect, "SET NAMES UTF8");

//      session_start();

        $sql = "select count(user_num) from user_info where '$_REQUEST[email]' = e_mail";

        $result = mysqli_query($connect, $sql);

        $row = mysqli_fetch_array($result);

        echo $row[0];

        if($row[0] == 0 && $_REQUEST[pw]){

                $e_mail = fix_string($connect, $_REQUEST[email]);
                $password = fix_string($connect, $_REQUEST[pw]);

                $sql = "insert into user_info(e_mail,password)
                                values('".$e_mail."',password('".$password."'))";
                mysqli_query($connect, $sql);
        }

        mysqli_close($connect);
?>

