<?php
        $connection = mysqli_connect('localhost','cs20111412','20111412','Backpacker')
                                                or die ("connection failed");

if(isset($_REQUEST["userid"]) && isset($_REQUEST["photoid"])){

        $userid = $_REQUEST["user_num"];
        $photoid = $_REQUEST["photo_num"];

        $query = "select latitude, longitude from photo_info
                        where photo_name like '%".$photoid.".jpg'";

        $result = mysqli_query($connection, $query);

        if($result){
                $data_row = mysqli_fetch_array($result);

                if($data_row['latitude'] && $data_row['longitude'])
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
