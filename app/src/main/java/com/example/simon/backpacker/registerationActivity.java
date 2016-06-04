package com.example.simon.backpacker;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
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
                    new conn().execute(email,pw);

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

        EmailConn task = new EmailConn();
        String result = null;
        try {
            result = task.execute(email).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



        if(result.equals("0"))
            return false;
        else
            return true;
    }

    private void moveHome(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private class conn extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("email",params[0]));
            nameValuePairs.add(new BasicNameValuePair("pw",params[1]));

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://uni07.unist.ac.kr/~cs20111412/join.php");
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpClient.execute(httpPost);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class EmailConn extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("email",params[0]));

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://uni07.unist.ac.kr/~cs20111412/join.php");
            String temp = null;
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entityResponse = response.getEntity();
                InputStream stream = entityResponse.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, HTTP.UTF_8));

                temp = reader.readLine();
                stream.close();


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return temp;
        }
    }


}
