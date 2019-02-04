package com.sheygam.masa_g2_2018_23_01_19_cw_part2;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.StoreProvider;

public class ViewActivity extends AppCompatActivity {
    private int contactPos;
    private TextView nameTxt,
            emailTxt,
            lastNameTxt,
            phoneTxt,
            addressTxt,
            descTxt;
    private FrameLayout progressFrame;
    private boolean isProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Intent intent = getIntent();
        contactPos = intent.getIntExtra("POS", 0);
        progressFrame = findViewById(R.id.progress_frame);
        progressFrame.setOnClickListener(null);

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
        new LoadContactTask().execute();
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
        intent.putExtra("POS", contactPos);
        startActivity(intent);
    }

    private void showProgress(){
        progressFrame.setVisibility(View.VISIBLE);
        isProgress = true;
    }

    private void hideProgress(){
        progressFrame.setVisibility(View.GONE);
        isProgress = false;
    }

    class LoadContactTask extends AsyncTask<Void,Void,Contact>{

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected Contact doInBackground(Void... voids) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return StoreProvider.getInstance().getByPosition(contactPos);
        }

        @Override
        protected void onPostExecute(Contact c) {
            hideProgress();
            nameTxt.setText(c.getName());
            emailTxt.setText(c.getEmail());
            lastNameTxt.setText(c.getLastName());
            phoneTxt.setText(c.getPhone());
            addressTxt.setText(c.getAddress());
            descTxt.setText(c.getDesc());
        }
    }

    class DeleteContactTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            StoreProvider.getInstance().remove(contactPos);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            hideProgress();
            finish();
        }
    }
}
