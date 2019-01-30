package com.sheygam.masa_g2_2018_23_01_19_cw_part2;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText inputEmail, inputPassword;
    private Button loginBtn;
    private FrameLayout progressFrame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StoreProvider.getInstance().setContext(this);
        super.onCreate(savedInstanceState);

        if(StoreProvider.getInstance().getUserId() != null){
            showContactList();
        }
        setContentView(R.layout.activity_main);
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        loginBtn = findViewById(R.id.login_btn);
        progressFrame = findViewById(R.id.progress_frame);
        progressFrame.setOnClickListener(null);
        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.login_btn){
            new LoginTask().execute();
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

    class LoginTask extends AsyncTask<Void,Void,Void>{
        private String email, password;

        @Override
        protected void onPreExecute() {
            email = inputEmail.getText().toString();
            password = inputPassword.getText().toString();
            progressFrame.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            StoreProvider.getInstance().login(email,password);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressFrame.setVisibility(View.GONE);
            showContactList();
        }
    }
}

