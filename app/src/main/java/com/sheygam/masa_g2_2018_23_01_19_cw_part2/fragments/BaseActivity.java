package com.sheygam.masa_g2_2018_23_01_19_cw_part2.fragments;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sheygam.masa_g2_2018_23_01_19_cw_part2.R;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.StoreProvider;

public class BaseActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StoreProvider.getInstance().setContext(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        if(StoreProvider.getInstance().getToken()!= null){
            showListFragment();
        }else{
            LoginFragment fragment = new LoginFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container,fragment)
                    .commit();
        }
    }


    private void showListFragment() {
        ContactListFragment fragment = new ContactListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container,fragment)
                .commit();
    }
}
