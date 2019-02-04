package com.sheygam.masa_g2_2018_23_01_19_cw_part2;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.HttpProvider;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.StoreProvider;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText inputEmail, inputPassword;
    private Button loginBtn, regBtn;
    private FrameLayout progressFrame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StoreProvider.getInstance().setContext(this);
        super.onCreate(savedInstanceState);

        if(StoreProvider.getInstance().getToken() != null){
            showContactList();
        }
        setContentView(R.layout.activity_main);
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        loginBtn = findViewById(R.id.login_btn);
        regBtn = findViewById(R.id.reg_btn);
        progressFrame = findViewById(R.id.progress_frame);
        progressFrame.setOnClickListener(null);
        loginBtn.setOnClickListener(this);
        regBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.login_btn){
            new LoginTask().execute();
        }else if(v.getId() == R.id.reg_btn){
            new RegTask().execute();
        }
    }

    private void showContactList() {
        Intent intent = new Intent(this, ContactListActivity.class);
        startActivityForResult(intent,1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode != RESULT_OK && requestCode == 1){
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class LoginTask extends AsyncTask<Void,Void,String>{
        private String email, password;
        private boolean isSuccess = true;

        @Override
        protected void onPreExecute() {
            email = inputEmail.getText().toString();
            password = inputPassword.getText().toString();
            progressFrame.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String res = "Login ok";
            try {
                String token = HttpProvider.getInstance().login(email,password);
                StoreProvider.getInstance().save(token);
            }catch (IOException ex){
                isSuccess = false;
                res = "Connection Error!";
            }catch (Exception e) {
                isSuccess = false;
                res = e.getMessage();
            }

            return res;
        }

        @Override
        protected void onPostExecute(String str) {
            progressFrame.setVisibility(View.GONE);
            if(isSuccess) {
                showContactList();
            }else{
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(str)
                        .setCancelable(false)
                        .setPositiveButton("Ok",null)
                        .create()
                        .show();
            }
        }
    }

    class RegTask extends AsyncTask<Void,Void,String>{
        private String email, password;
        private boolean isSuccess = true;

        @Override
        protected void onPreExecute() {
            email = inputEmail.getText().toString();
            password = inputPassword.getText().toString();
            progressFrame.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String res = "Registration ok";
            try {
                String token = HttpProvider.getInstance().registration(email,password);
                StoreProvider.getInstance().save(token);
            }catch (IOException ex){
                isSuccess = false;
                res = "Connection Error!";
            }catch (Exception e) {
                isSuccess = false;
                res = e.getMessage();
            }

            return res;
        }

        @Override
        protected void onPostExecute(String str) {
            progressFrame.setVisibility(View.GONE);
            if(isSuccess) {
                showContactList();
            }else{
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(str)
                        .setCancelable(false)
                        .setPositiveButton("Ok",null)
                        .create()
                        .show();
            }
        }
    }
}

