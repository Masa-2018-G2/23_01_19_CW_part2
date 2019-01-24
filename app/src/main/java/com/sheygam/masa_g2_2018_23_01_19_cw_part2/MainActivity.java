package com.sheygam.masa_g2_2018_23_01_19_cw_part2;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText inputEmail, inputPassword;
    private Button loginBtn;
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
        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.login_btn){
            StoreProvider.getInstance().login(inputEmail.getText().toString(),
                    inputPassword.getText().toString());
            showContactList();
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
}

