package com.sheygam.masa_g2_2018_23_01_19_cw_part2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ViewActivity extends AppCompatActivity {
    private int contactPos;
    private TextView nameTxt,
            emailTxt,
            lastNameTxt,
            phoneTxt,
            addressTxt,
            descTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Intent intent = getIntent();
        contactPos = intent.getIntExtra("POS", 0);
        nameTxt = findViewById(R.id.name_txt);
        emailTxt = findViewById(R.id.email_txt);
        lastNameTxt = findViewById(R.id.last_name_txt);
        phoneTxt = findViewById(R.id.phone_txt);
        addressTxt = findViewById(R.id.address_txt);
        descTxt = findViewById(R.id.desc_txt);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String id = getSharedPreferences("AUTH",MODE_PRIVATE)
                .getString("CURR",null);
        if(id != null){
            String contactsStr = getSharedPreferences("DATA",MODE_PRIVATE)
                    .getString(id,null);
            if(contactsStr!=null){
                String[] arr = contactsStr.split(";");
                if(arr.length > contactPos){
                    Contact c = Contact.of(arr[contactPos]);
                    nameTxt.setText(c.getName());
                    emailTxt.setText(c.getEmail());
                    lastNameTxt.setText(c.getLastName());
                    phoneTxt.setText(c.getPhone());
                    addressTxt.setText(c.getAddress());
                    descTxt.setText(c.getDesc());
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
        getMenuInflater().inflate(R.menu.info_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_item) {
            removeItem();
        } else if (item.getItemId() == R.id.edit_item) {
            showEditView();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEditView() {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("POS", contactPos);
        startActivity(intent);
    }

    private void removeItem() {
        String id = getSharedPreferences("AUTH",MODE_PRIVATE)
                .getString("CURR",null);
        if(id!=null){
            String contactsStr = getSharedPreferences("DATA",MODE_PRIVATE)
                    .getString(id,null);
            if(contactsStr!= null){
                String[] arr = contactsStr.split(";");
                if(arr.length > contactPos){
                    List<Contact> list = new ArrayList<>();
                    for(String str : arr){
                        list.add(Contact.of(str));
                    }
                    list.remove(contactPos);
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < list.size(); i++) {
                        builder.append(list.get(i).toString());
                        if(i != list.size()-1){
                            builder.append(";");
                        }
                    }
                    String res = builder.toString();
                    if(res.isEmpty()){
                        getSharedPreferences("DATA",MODE_PRIVATE)
                                .edit()
                                .remove(id)
                                .commit();
                    }else {
                        getSharedPreferences("DATA", MODE_PRIVATE)
                                .edit()
                                .putString(id, builder.toString())
                                .commit();
                    }
                    finish();
                }else {
                    finishOnError();
                }
            }else{
                finishOnError();
            }
        }else{
            finishOnError();
        }
    }
}
