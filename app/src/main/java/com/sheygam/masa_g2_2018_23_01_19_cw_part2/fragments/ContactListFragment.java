package com.sheygam.masa_g2_2018_23_01_19_cw_part2.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sheygam.masa_g2_2018_23_01_19_cw_part2.ContactListActivity;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.ContactListAdapter;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.R;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.OkHttpProvider;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.StoreProvider;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.dto.ContactDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactListFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ListView contactList;

    @Override
    public void onStart() {
        super.onStart();
        new LoadTask().execute();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_contact_list,container,false);
        contactList = view.findViewById(R.id.contact_list);
        contactList.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logout_item){
            logout();
        }else if(item.getItemId() == R.id.add_item){
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container,new EditFragment())
                    .addToBackStack(null)
                    .commit();
        }else if (item.getItemId() == R.id.delete_all_item){
            new AlertDialog.Builder(getContext())
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
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container,new LoginFragment())
                .commit();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ContactDto contact = (ContactDto) parent.getAdapter().getItem(position);
        ViewFragment fragment = ViewFragment.of(contact);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container,fragment,"VIEW_FRAG")
                .addToBackStack(null)
                .commit();

    }

    class LoadTask extends AsyncTask<Void,Void,String> {
        private AlertDialog dialog;
        private boolean isSuccess = true;
        private List<ContactDto> list = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            dialog = new AlertDialog.Builder(getContext())
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
                ContactListAdapter adapter = new ContactListAdapter(list,getContext());
                contactList.setAdapter(adapter);
            }else{
                new AlertDialog.Builder(getContext())
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
            dialog = new AlertDialog.Builder(getContext())
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
                        new ArrayList<ContactDto>(),getContext()));
            }else{
                new AlertDialog.Builder(getContext())
                        .setMessage(str)
                        .setCancelable(false)
                        .setPositiveButton("Ok",null)
                        .create()
                        .show();
            }
        }
    }
}
