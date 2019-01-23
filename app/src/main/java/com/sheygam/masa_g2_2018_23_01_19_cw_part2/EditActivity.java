package com.sheygam.masa_g2_2018_23_01_19_cw_part2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class EditActivity extends AppCompatActivity {
    private int contactPos;
    private Contact curr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        contactPos = intent.getIntExtra("POS",-1);
        if(contactPos < 0){
            curr = new Contact("","","","","","");
        }else{
            loadContact();
        }
    }

    private void loadContact() {
        //Todo load current from SP;
        //curr = getSp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.done_item){
            //Todo Check fields for empty
            //Todo Save changes
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
