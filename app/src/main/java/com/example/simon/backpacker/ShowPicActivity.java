package com.example.simon.backpacker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ShowPicActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pic);

        int photoNum = getIntent().getExtras().getInt("photoid");
        int userid = getIntent().getIntExtra("userid",-1);

        //make url
        String url = "http://uni07.unist.ac.kr/~cs20111412/imgs/" + String.valueOf(userid) + "_" + String.valueOf(photoNum) + ".jpg";


        Bitmap photo = null;
        try{
            photo = new getPhoto().execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
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

    private class getPhoto extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try{
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();

                bitmap = BitmapFactory.decodeStream(is);

                float width = bitmap.getWidth();
                float height = bitmap.getHeight();

                if(width<height) {
                    int viewHeight = 1400;

                    if (height > viewHeight) {
                        float percente = (float) (height / 100);
                        float scale = (float) (viewHeight / percente);
                        width *= (scale / 100);
                        height *= (scale / 100);
                    }

                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
                }
                else{
                    int viewWidth = 1100;

                    if (width > viewWidth) {
                        float percente = (float) (width / 100);
                        float scale = (float) (viewWidth / percente);
                        width *= (scale / 100);
                        height *= (scale / 100);
                    }

                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
                }


            }catch(IOException e){
                e.printStackTrace();
            }
            return bitmap;
        }
    }
}
