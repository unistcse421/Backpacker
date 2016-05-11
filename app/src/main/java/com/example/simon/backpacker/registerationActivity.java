package com.example.simon.backpacker;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.regex.Pattern;

public class registerationActivity extends AppCompatActivity {
    private PopupWindow popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        Button register = (Button)findViewById(R.id.register);
        register.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValidateForm = true;

                EditText emailView = (EditText)findViewById(R.id.email);
                EditText pwView = (EditText)findViewById(R.id.pw);
                EditText pwCheckView = (EditText)findViewById(R.id.pw_check);

                String email = emailView.getText().toString();
                String pw = pwView.getText().toString();
                String pwCheck = pwCheckView.getText().toString();

                //validate email
                if(!isValidEmail(email)) {
                    TextView emailValidation = (TextView) findViewById(R.id.email_validation);
                    emailValidation.setText("Email is Invalidate");
                    isValidateForm = false;
                }else if(isExistEmail(email)){                              //check email duplication
                    TextView emailValidation = (TextView) findViewById(R.id.email_validation);
                    emailValidation.setText("Email already Exist");
                    isValidateForm = false;
                }else{
                    TextView emailValidation = (TextView) findViewById(R.id.email_validation);
                    emailValidation.setText("");
                }

                //validate pw
                if(pw.length()<8 || pw.length()>20){
                    TextView pwValidation = (TextView) findViewById(R.id.pw_validation);
                    pwValidation.setText("PW should be 8~20 characters");
                    isValidateForm = false;
                }else{
                    TextView pwValidation = (TextView) findViewById(R.id.pw_validation);
                    pwValidation.setText("");
                }

                //validate pwcheck
                if(pw.compareTo(pwCheck) != 0 || pw.length() == 0){
                    TextView pwCheckValidation = (TextView) findViewById(R.id.pwcheck_validation);
                    pwCheckValidation.setText("PW check is not correct");
                    isValidateForm = false;
                }else{
                    TextView pwCheckValidation = (TextView) findViewById(R.id.pwcheck_validation);
                    pwCheckValidation.setText("");
                }

                if(isValidateForm){
                    //send data to DB

                    View popupView = getLayoutInflater().inflate(R.layout.activity_registeration,null);
                    popup = new PopupWindow(popupView);

                    popup.showAtLocation(popupView, Gravity.CENTER,0,0);

                    moveHome();
                }
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

    private void moveHome(){
        popup.dismiss();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
