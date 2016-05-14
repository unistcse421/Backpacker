package com.example.simon.backpacker;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class registerationActivity extends AppCompatActivity {
    private PopupWindow popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);

        final EditText emailView = (EditText)findViewById(R.id.email);
        final EditText pwView = (EditText)findViewById(R.id.pw);
        final EditText pwCheckView = (EditText)findViewById(R.id.pw_check);

        LinearLayout background = (LinearLayout)findViewById(R.id.register_background);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager keyboard;
                keyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.hideSoftInputFromWindow(emailView.getWindowToken(),0);
                keyboard.hideSoftInputFromWindow(pwView.getWindowToken(),0);
                keyboard.hideSoftInputFromWindow(pwCheckView.getWindowToken(),0);
            }
        });

        Button register = (Button)findViewById(R.id.register_join);
        register.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValidateForm = true;

                String email = emailView.getText().toString();
                String pw = pwView.getText().toString();
                String pwCheck = pwCheckView.getText().toString();

                //validate email
                if(!isValidEmail(email)) {
                    TextView emailValidation = (TextView) findViewById(R.id.email_validation);
                    emailValidation.setText("Email is Invalidate");
                    isValidateForm = false;
                    return;
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
                    return;
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

                    View popupView = getLayoutInflater().inflate(R.layout.popup_register_success,null);
                    popup = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);

                    popup.showAtLocation(popupView,Gravity.NO_GRAVITY,0,0);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            popup.dismiss();
                            moveHome();
                        }

                    }, 2000);
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
        return false;
    }

    private void moveHome(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
