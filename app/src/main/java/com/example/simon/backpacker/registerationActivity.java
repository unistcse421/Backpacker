package com.example.simon.backpacker;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Pattern;

public class registerationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        Button register = (Button)findViewById(R.id.register);
        register.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailView = (EditText)findViewById(R.id.email);
                EditText pwView = (EditText)findViewById(R.id.pw);
                EditText pwCheckView = (EditText)findViewById(R.id.pw_check);

                String email = emailView.getText().toString();
                String pw = pwView.getText().toString();
                String pwCheck = pwCheckView.getText().toString();

                //validate email
                if(!isValidEmail(email)) {
                    //alarm at screen
                }

                //check email duplication
                if(isExistEmail(email)){
                    //alarm at screen
                }

                //validate pw
                if(pw.compareTo(pwCheck) != 0){
                    //alarm at screen
                }

                //send data to DB
            }
        });
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private boolean isExistEmail(String email){
        //with DB connection
        return true;
    }

}
