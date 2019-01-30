package com.sheygam.masa_g2_2018_23_01_19_cw_part2;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {
    private int contactPos;
    private Contact curr;
    private EditText inputName,
            inputLastName,
            inputEmail,
            inputPhone,
            inputAddress,
            inputDesc;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        progressDialog = new AlertDialog.Builder(this)
                .setTitle("Progress ...")
                .setView(R.layout.frame_progress_dialog)
                .setCancelable(false)
                .create();
        inputName = findViewById(R.id.input_name);
        inputLastName = findViewById(R.id.input_last_name);
        inputEmail = findViewById(R.id.input_email);
        inputPhone = findViewById(R.id.input_phone);
        inputAddress = findViewById(R.id.input_address);
        inputDesc = findViewById(R.id.input_desc);

        Intent intent = getIntent();
        contactPos = intent.getIntExtra("POS", -1);
        if (contactPos < 0) {
            curr = new Contact("", "", "", "", "", "");
        } else {
            new LoadContactTask().execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.done_item) {

            String name = inputName.getText().toString().trim();
            String lastName = inputLastName.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String phone = inputPhone.getText().toString().trim();
            String address = inputAddress.getText().toString().trim();
            String desc = inputDesc.getText().toString().trim();

            if (isValid(name, lastName, email, phone, address, desc)) {
                curr = new Contact(name, lastName, email, phone, address, desc);
                new SaveTask().execute();
            } else {
                Toast.makeText(this, "All fields need by fill!", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void isProgressEnabled(boolean res){
        if(res){
            progressDialog.show();
        }else{
            progressDialog.dismiss();
        }
    }

    private boolean isValid(String name,
                            String lastName,
                            String email,
                            String phone,
                            String address,
                            String desc) {
        return !name.isEmpty()
                && !lastName.isEmpty()
                && !email.isEmpty()
                && !phone.isEmpty()
                && !address.isEmpty()
                && !desc.isEmpty();
    }

    class LoadContactTask extends AsyncTask<Void,Void,Contact> {

        @Override
        protected void onPreExecute() {
            isProgressEnabled(true);
        }

        @Override
        protected Contact doInBackground(Void... voids) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return StoreProvider.getInstance().getByPosition(contactPos);
        }

        @Override
        protected void onPostExecute(Contact c) {
            isProgressEnabled(false);
            curr = c;
            inputName.setText(curr.getName());
            inputEmail.setText(curr.getEmail());
            inputLastName.setText(curr.getLastName());
            inputPhone.setText(curr.getPhone());
            inputAddress.setText(curr.getAddress());
            inputDesc.setText(curr.getDesc());
        }
    }

    class SaveTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            isProgressEnabled(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (contactPos < 0) {
                StoreProvider.getInstance().add(curr);
            } else {
                StoreProvider.getInstance().update(curr, contactPos);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            isProgressEnabled(false);
            finish();
        }
    }
}
