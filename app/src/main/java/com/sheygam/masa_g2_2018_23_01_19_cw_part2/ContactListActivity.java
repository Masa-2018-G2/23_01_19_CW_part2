package com.sheygam.masa_g2_2018_23_01_19_cw_part2;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.HttpProvider;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.StoreProvider;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.dto.ContactDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        contactList = findViewById(R.id.contact_list);
        contactList.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        new LoadTask().execute();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add_item){
            Intent intent = new Intent(this,EditActivity.class);
            startActivity(intent);
        }else if(item.getItemId() == R.id.logout_item){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        StoreProvider.getInstance().logout();
        setResult(RESULT_OK);
        finish();
    }

    private void showContact(int pos){
        Intent intent = new Intent(this,ViewActivity.class);
        intent.putExtra("POS",pos);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showContact(position);
    }

    class LoadTask extends AsyncTask<Void,Void,String>{
        private AlertDialog dialog;
        private boolean isSuccess = true;
        private List<ContactDto> list = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            dialog = new AlertDialog.Builder(ContactListActivity.this)
                    .setTitle("Loading...")
                    .setView(R.layout.frame_progress_dialog)
                    .setCancelable(false)
                    .create();
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String token = StoreProvider.getInstance().getToken();
            String res = "Response ok";
            try {
                list = HttpProvider.getInstance().getAllContacts(token);
            } catch (IOException e){
                isSuccess = false;
                res = "Connection error!";
            } catch (Exception e) {
                isSuccess = false;
                res = e.getMessage();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String str) {
            dialog.dismiss();
            if(isSuccess){
                ContactListAdapter adapter = new ContactListAdapter(list,ContactListActivity.this);
                contactList.setAdapter(adapter);
            }else{
                new AlertDialog.Builder(ContactListActivity.this)
                        .setMessage(str)
                        .setCancelable(false)
                        .setPositiveButton("Ok",null)
                        .create()
                        .show();
            }
        }
    }
}
