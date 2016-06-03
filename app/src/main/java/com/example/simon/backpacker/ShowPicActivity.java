package com.example.simon.backpacker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;

public class ShowPicActivity extends AppCompatActivity {
    private ArrayList<MapsActivity.PicInfo> picInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pic);

        int photoNum = getIntent().getExtras().getInt("id");
        picInfo = (ArrayList< MapsActivity.PicInfo>)getIntent().getExtras().get("piclist");

        Bitmap photo;
        photo = picInfo.get(photoNum).getBitmap();

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
