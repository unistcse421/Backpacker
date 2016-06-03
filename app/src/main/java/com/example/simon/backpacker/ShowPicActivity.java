package com.example.simon.backpacker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ShowPicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pic);

        int photoNum = getIntent().getExtras().getInt("id");

        Bitmap photo;
        //get photo from url
        switch (photoNum){
            case 0:
                photo = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
                break;
            case 1:
                photo = BitmapFactory.decodeResource(getResources(),R.mipmap.test1);
                break;
            case 2:
                photo = BitmapFactory.decodeResource(getResources(),R.mipmap.test2);
                break;
            default:
                photo = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        }


        ImageView imageView = (ImageView)findViewById(R.id.showPic);
        imageView.setImageBitmap(photo);

        ImageButton back = (ImageButton)findViewById(R.id.back_arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
