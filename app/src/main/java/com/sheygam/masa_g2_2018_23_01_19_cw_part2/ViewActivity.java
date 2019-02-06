package com.sheygam.masa_g2_2018_23_01_19_cw_part2;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.OkHttpProvider;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.StoreProvider;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.dto.ContactDto;

import java.io.IOException;

public class ViewActivity extends AppCompatActivity {
    private static final int EDIT_ACTIVITY = 1;
    private ContactDto curr;
    private Gson gson;
    private TextView nameTxt,
            emailTxt,
            lastNameTxt,
            phoneTxt,
            addressTxt,
            descTxt;
    private FrameLayout progressFrame;
    private boolean isProgress = false;
    private String contactJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        gson = new Gson();
        Intent intent = getIntent();


        contactJson = intent.getStringExtra("CONTACT");
        curr = gson.fromJson(contactJson,ContactDto.class);

        progressFrame = findViewById(R.id.progress_frame);
        progressFrame.setOnClickListener(null);

        nameTxt = findViewById(R.id.name_txt);
        emailTxt = findViewById(R.id.email_txt);
        lastNameTxt = findViewById(R.id.last_name_txt);
        phoneTxt = findViewById(R.id.phone_txt);
        addressTxt = findViewById(R.id.address_txt);
        descTxt = findViewById(R.id.desc_txt);

        nameTxt.setText(curr.getName());
        lastNameTxt.setText(curr.getLastName());
        emailTxt.setText(curr.getEmail());
        phoneTxt.setText(curr.getPhone());
        addressTxt.setText(curr.getAddress());
        descTxt.setText(curr.getDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(isProgress){
            Toast.makeText(this, "Wait for progress!", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (item.getItemId() == R.id.delete_item) {
            new DeleteContactTask().execute();
        } else if (item.getItemId() == R.id.edit_item) {
            showEditView();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEditView() {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("CONTACT", contactJson);
        startActivityForResult(intent,EDIT_ACTIVITY);
    }

    private void showProgress(){
        progressFrame.setVisibility(View.VISIBLE);
        isProgress = true;
    }

    private void hideProgress(){
        progressFrame.setVisibility(View.GONE);
        isProgress = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && requestCode == EDIT_ACTIVITY){
            String contactJson = data.getStringExtra("CONTACT");
            curr = gson.fromJson(contactJson,ContactDto.class);
            nameTxt.setText(curr.getName());
            lastNameTxt.setText(curr.getLastName());
            emailTxt.setText(curr.getEmail());
            phoneTxt.setText(curr.getPhone());
            addressTxt.setText(curr.getAddress());
            descTxt.setText(curr.getDescription());

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class DeleteContactTask extends AsyncTask<Void,Void,String>{
        private boolean isSuccess = true;
        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String token = StoreProvider.getInstance().getToken();
            try {
                OkHttpProvider.getInstance().delete(curr.getId(),token);
            } catch (IOException e){
                e.printStackTrace();
                isSuccess = false;
                return "Connection error! Check your internet!";
            }catch (Exception e) {
                isSuccess = false;
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String str) {
            hideProgress();
            if(isSuccess) {
                finish();
            }else{
                new AlertDialog.Builder(ViewActivity.this)
                        .setTitle("Error!")
                        .setMessage(str)
                        .setPositiveButton("Ok",null)
                        .setCancelable(false)
                        .create()
                        .show();
            }
        }
    }
}
