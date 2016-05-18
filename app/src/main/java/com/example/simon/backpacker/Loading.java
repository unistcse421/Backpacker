package com.example.simon.backpacker;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Loading extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                moveHome();
            }

        }, 2500);
    }

    private void moveHome(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
