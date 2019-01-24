package com.sheygam.masa_g2_2018_23_01_19_cw_part2;

import android.content.Intent;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        inputName = findViewById(R.id.input_name);
        inputLastName = findViewById(R.id.input_last_name);
        inputEmail = findViewById(R.id.input_email);
        inputPhone = findViewById(R.id.input_phone);
        inputAddress = findViewById(R.id.input_address);
        inputDesc = findViewById(R.id.input_desc);

        Intent intent = getIntent();
        contactPos = intent.getIntExtra("POS",-1);
        if(contactPos < 0){
            curr = new Contact("","","","","","");
        }else{
            loadContact();
        }
    }

    private void loadContact() {
        String id = getSharedPreferences("AUTH",MODE_PRIVATE)
                .getString("CURR",null);
        if(id != null){
            String contactsStr = getSharedPreferences("DATA",MODE_PRIVATE)
                    .getString(id,null);
            if(contactsStr!=null){
                String[] arr = contactsStr.split(";");
                if(arr.length  > contactPos){
                    curr = Contact.of(arr[contactPos]);
                    inputName.setText(curr.getName());
                    inputEmail.setText(curr.getEmail());
                    inputLastName.setText(curr.getLastName());
                    inputPhone.setText(curr.getPhone());
                    inputAddress.setText(curr.getAddress());
                    inputDesc.setText(curr.getDesc());
                }else{
                    finishOnError();
                }
            }else{
                finishOnError();
            }
        }else{
            finishOnError();
        }
    }

    private void finishOnError() {
        Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.done_item){

            String name = inputName.getText().toString().trim();
            String lastName = inputLastName.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String phone = inputPhone.getText().toString().trim();
            String address = inputAddress.getText().toString().trim();
            String desc = inputDesc.getText().toString().trim();

            if(isValid(name,lastName,email,phone,address,desc)) {
                curr = new Contact(name, lastName, email, phone, address, desc);
                saveChanges();
                finish();
            }else{
                Toast.makeText(this, "All fields need by fill!", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveChanges() {
        String id = getSharedPreferences("AUTH",MODE_PRIVATE)
                .getString("CURR",null);
        if(id != null){
            String contactsStr = getSharedPreferences("DATA",MODE_PRIVATE)
                    .getString(id,null);
            List<Contact> list = new ArrayList<>();
            if(contactsStr!= null){
                String[] arr = contactsStr.split(";");
                for(String str : arr){
                    list.add(Contact.of(str));
                }
            }
            if(contactPos < 0){
                list.add(curr);
            }else{
                list.set(contactPos,curr);
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                builder.append(list.get(i).toString());
                if(i != list.size()-1){
                    builder.append(";");
                }
            }
            getSharedPreferences("DATA",MODE_PRIVATE)
                    .edit()
                    .putString(id,builder.toString())
                    .commit();
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
}
