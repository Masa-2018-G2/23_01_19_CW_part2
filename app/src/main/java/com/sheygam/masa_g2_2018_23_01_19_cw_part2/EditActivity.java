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

import com.google.gson.Gson;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.OkHttpProvider;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.StoreProvider;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.dto.ContactDto;

import java.io.IOException;

public class EditActivity extends AppCompatActivity {
    private ContactDto curr;
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
        String contactJson = intent.getStringExtra("CONTACT");
        if (contactJson == null) {
            curr = new ContactDto(-1,"", "", "", "", "", "");
        } else {
            Gson gson = new Gson();
            curr = gson.fromJson(contactJson,ContactDto.class);
        }

        inputName.setText(curr.getName());
        inputLastName.setText(curr.getLastName());
        inputEmail.setText(curr.getEmail());
        inputPhone.setText(curr.getPhone());
        inputAddress.setText(curr.getAddress());
        inputDesc.setText(curr.getDescription());
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
                curr.setName(name);
                curr.setLastName(lastName);
                curr.setEmail(email);
                curr.setPhone(phone);
                curr.setAddress(address);
                curr.setDescription(desc);
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

    class SaveTask extends AsyncTask<Void,Void,String>{
        private boolean isSuccess = true;
        private ContactDto res;
        @Override
        protected void onPreExecute() {
            isProgressEnabled(true);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String token = StoreProvider.getInstance().getToken();
            try {
                if (curr.getId() < 0) {
                    res = OkHttpProvider.getInstance().addContact(curr, token);
                } else {
                    res = OkHttpProvider.getInstance().updateContact(curr, token);
                }
            }catch (IOException e){
                e.printStackTrace();
                isSuccess = false;
                return "Connection error! Check your internet!";
            }catch (Exception e){
                isSuccess = false;
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String str) {
            isProgressEnabled(false);
            if(isSuccess) {
                Gson gson = new Gson();
                String contactJson = gson.toJson(res);
                Intent intent = new Intent();
                intent.putExtra("CONTACT",contactJson);
                setResult(RESULT_OK,intent);
                finish();
            }else{
                new AlertDialog.Builder(EditActivity.this)
                        .setTitle("Error")
                        .setMessage(str)
                        .setPositiveButton("Ok",null)
                        .setCancelable(false)
                        .create()
                        .show();
            }
        }
    }
}
