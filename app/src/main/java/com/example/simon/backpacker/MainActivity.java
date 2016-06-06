package com.example.simon.backpacker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {

    private int userId = -1;        //-1 meaning that user not exist in DB
    private int photoNum = 0;
    private PopupWindow popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button login = (Button)findViewById(R.id.login);
        Button join = (Button)findViewById(R.id.login_join);

        final EditText emailView = (EditText)findViewById(R.id.login_email);
        final EditText pwView = (EditText)findViewById(R.id.login_pw);

        LinearLayout background = (LinearLayout)findViewById(R.id.login_background);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager keyboard;
                keyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.hideSoftInputFromWindow(emailView.getWindowToken(),0);
                keyboard.hideSoftInputFromWindow(pwView.getWindowToken(),0);
            }
        });

        login.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isLoginSuccess = false;

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
        intent.putExtra("PHOTONUM",photoNum);
        startActivity(intent);
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private  boolean isUser(String email, String pw){
        getUserId(email,pw);
        if(userId == -1)
            return false;
        else
            return true;
    }

    private void getUserId(String email, String pw){
        //with DB connection
        //if exist, save it at userId(global var)
        GetIdNet task = new GetIdNet();

        String[] result = null;
        try {
            result = task.execute(email,pw).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(result[0].equals("-1")) {
            userId = -1;
            return;
        }

        userId = Integer.parseInt(result[1]);
        photoNum = Integer.parseInt(result[2]);
    }

    private class GetIdNet extends AsyncTask<String,Void,String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("email",params[0]));
            nameValuePairs.add(new BasicNameValuePair("pw",params[1]));

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://uni07.unist.ac.kr/~cs20111412/login_se.php");
            String[] temp = new String[4];
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entityResponse = response.getEntity();
                InputStream stream = entityResponse.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, HTTP.UTF_8));


                for(int i=0;i<4;i++)
                    temp[i] = reader.readLine();

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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View popupView = getLayoutInflater().inflate(R.layout.popup_login,null);
            popup = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);

            popup.showAtLocation(popupView, Gravity.NO_GRAVITY,0,0);
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            popup.dismiss();
        }
    }


}
