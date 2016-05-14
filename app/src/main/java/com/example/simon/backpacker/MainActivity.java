package com.example.simon.backpacker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private int userId = -1;        //-1 meaning that user not exist in DB

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button login = (Button)findViewById(R.id.login);
        Button join = (Button)findViewById(R.id.login_join);

        login.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isLoginSuccess = false;

                EditText emailView = (EditText)findViewById(R.id.email);
                EditText pwView = (EditText)findViewById(R.id.pw);

                String email = emailView.getText().toString();
                String pw = pwView.getText().toString();

                //validate email
                if(!isValidEmail(email)) {
                    TextView emailValidation = (TextView) findViewById(R.id.login_email_validation);
                    emailValidation.setText("Email is Invalidate form");
                    return;
                }else{
                    TextView emailValidation = (TextView) findViewById(R.id.login_email_validation);
                    emailValidation.setText("");
                }

                //user check
                if(!isUser(email,pw)){
                    TextView pwValidation = (TextView) findViewById(R.id.login_pw_validation);
                    pwValidation.setText("Check your Email or PW, again");
                    return;
                }else{
                    TextView pwValidation = (TextView) findViewById(R.id.login_pw_validation);
                    pwValidation.setText("");
                    isLoginSuccess = true;
                }

                if(isLoginSuccess){
                    moveMap();
                }
            }
        });

        join.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveRegister();
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                new AlertDialog.Builder(this)
                        .setTitle("종료")
                        .setMessage("종료 하시겠습니까?")
                        .setPositiveButton("아니요",null)
                        .setNegativeButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                moveTaskToBack(true);
                                finish();
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        })
                        .show();
                return false;
            default:
                return false;
        }
    }

    private void moveRegister(){
        Intent intent = new Intent(this,registerationActivity.class);
        startActivity(intent);
    }

    private void moveMap(){
        Intent intent = new Intent(this,MapsActivity.class);
        intent.putExtra("USERID",userId);
        startActivity(intent);
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private  boolean isUser(String email, String pw){
        if(userId == -1)
            return false;
        else
            return true;
    }

    private void getUserId(String email, String pw){
        //with DB connection
        //if exist, save it at userId(global var)
    }
}
