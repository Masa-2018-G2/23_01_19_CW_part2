package com.sheygam.masa_g2_2018_23_01_19_cw_part2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ContactListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
    }

    @Override
    protected void onStart() {
        loadCurrentList();
        super.onStart();
    }

    private void loadCurrentList() {
        //Todo get current from sp and update adapter
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
        getSharedPreferences("AUTH",MODE_PRIVATE)
                .edit()
                .remove("CURR")
                .commit();
        setResult(RESULT_OK);
        finish();
    }

    private void showContact(int pos){
        Intent intent = new Intent(this,ViewActivity.class);
        intent.putExtra("POS",pos);
        startActivity(intent);
    }
}
