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
import android.widget.Toast;

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

    class LoadTask extends AsyncTask<Void,Void,List<Contact>>{
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
        protected List<Contact> doInBackground(Void... voids) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return StoreProvider.getInstance().contacts();
        }

        @Override
        protected void onPostExecute(List<Contact> contacts) {
            ContactListAdapter adapter = new ContactListAdapter(contacts,ContactListActivity.this);
            contactList.setAdapter(adapter);
            dialog.dismiss();
        }
    }
}
