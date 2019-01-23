package com.sheygam.masa_g2_2018_23_01_19_cw_part2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ViewActivity extends AppCompatActivity {
    private int contactPos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Intent intent = getIntent();
        contactPos = intent.getIntExtra("POS",0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Todo load contact by pos from SP
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.delete_item){
            removeItem();
        }else if(item.getItemId() == R.id.edit_item){
            showEditView();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEditView() {
        Intent intent = new Intent(this,EditActivity.class);
        intent.putExtra("POS",contactPos);
        startActivity(intent);
    }

    private void removeItem() {
        //Todo remove from sp
        finish();
    }
}
