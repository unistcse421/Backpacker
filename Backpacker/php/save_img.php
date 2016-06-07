<?php

  header('Content-type : bitmap; charset=utf-8');

  if(isset($_REQUEST[encoded_string])){
    $encoded_string = $_REQUEST[encoded_string];
    $image_name = $_REQUEST[image_name];
    $latitude = $_REQUEST[latitude];
    $longitude = $_REQUEST[longitude];
    $user_num = $_REQUEST[user_num];

//      echo $image_name."<br />\n".$encoded_string;

    $decoded_string = base64_decode($encoded_string);

    $file = fopen("./imgs/".$image_name, "wb");

    if($file)
        echo "suc<br />\n";
    else
        echo "fail<br />\n";

    $is_written = fwrite($file, $decoded_string);

    fclose($file);

    if($is_written > 0){

        echo "is_written<br />\n";

      $connection = mysqli_connect('localhost','cs20111412','20111412','Backpacker');
      $query = "INSERT INTO photo_info(photo_name,latitude,longitude,user_num)
                         values('$image_name','$latitude','$longitude','$user_num');";

      $result = mysqli_query($connection, $query);

      if($result){
        echo "success";
      }
      else{
        echo "failed";
      }

      mysqli_close($connection);
  }
}

?>

