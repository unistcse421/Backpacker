<?php
        $connection = mysqli_connect('localhost','cs20111412','20111412','Backpacker')
                                                or die ("connection failed");

if(isset($_REQUEST["userid"]) && isset($_REQUEST["photoid"])){

        $userid = $_REQUEST["userid"];
        $photoid = $_REQUEST["photoid"];

        $query = "select user_num,latitude, longitude from photo_info
                        where photo_name like '%".$photoid.".jpg'";

        $result = mysqli_query($connection, $query);

        if($result){
                $data_row = mysqli_fetch_array($result);

                if($data_row['latitude'] && $data_row['longitude'] $data_row['user_num'] == $userid)
                        echo $data_row['latitude']."\n".$data_row['longitude'];
                else{
                        echo -1;
                        return;
                }
        }
        else
                echo -1;

        mysqli_close($connection);
?>
