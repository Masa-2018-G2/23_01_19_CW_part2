package com.sheygam.masa_g2_2018_23_01_19_cw_part2;

import android.content.DialogInterface;
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

import com.google.gson.Gson;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.HttpProvider;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.OkHttpProvider;
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
        }else if(item.getItemId() == R.id.delete_all_item){
            new AlertDialog.Builder(this)
                    .setTitle("Delete all?")
                    .setMessage("Are you sure that you want delete all your contacts?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new ClearTask().execute();
                        }
                    })
                    .setNegativeButton("Cancel",null)
                    .setCancelable(false)
                    .create()
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        StoreProvider.getInstance().logout();
        setResult(RESULT_OK);
        finish();
    }

    private void showContact(ContactDto contact){
        Gson gson = new Gson();
        Intent intent = new Intent(this,ViewActivity.class);
        intent.putExtra("CONTACT",gson.toJson(contact));
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ContactDto contact = (ContactDto) parent.getAdapter().getItem(position);
        showContact(contact);
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
                list = OkHttpProvider.getInstance().getAllContacts(token);
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

    class ClearTask extends AsyncTask<Void,Void,String>{
        private boolean isSuccess = true;
        private AlertDialog dialog;

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
            try {
                OkHttpProvider.getInstance().clear(token);
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
            dialog.dismiss();
            if (isSuccess){
                contactList.setAdapter(new ContactListAdapter(
                        new ArrayList<ContactDto>(),ContactListActivity.this));
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
