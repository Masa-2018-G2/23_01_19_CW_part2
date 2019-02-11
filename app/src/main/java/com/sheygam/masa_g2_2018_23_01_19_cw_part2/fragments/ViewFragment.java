package com.sheygam.masa_g2_2018_23_01_19_cw_part2.fragments;

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
import android.widget.TextView;
import android.widget.Toast;

import com.sheygam.masa_g2_2018_23_01_19_cw_part2.R;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.ViewActivity;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.OkHttpProvider;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.StoreProvider;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.dto.ContactDto;

import java.io.IOException;

public class ViewFragment extends Fragment {
    private TextView nameTxt,
            emailTxt,
            lastNameTxt,
            phoneTxt,
            addressTxt,
            descTxt;
    private ContactDto curr;

    public static ViewFragment of(ContactDto contact){
        ViewFragment fragment = new ViewFragment();
        fragment.curr = contact;
        return fragment;
    }

    public void setCurr(ContactDto contact){
        curr = contact;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_view,container,false);
        nameTxt = view.findViewById(R.id.name_txt);
        emailTxt = view.findViewById(R.id.email_txt);
        lastNameTxt = view.findViewById(R.id.last_name_txt);
        phoneTxt = view.findViewById(R.id.phone_txt);
        addressTxt = view.findViewById(R.id.address_txt);
        descTxt = view.findViewById(R.id.desc_txt);

        nameTxt.setText(curr.getName());
        lastNameTxt.setText(curr.getLastName());
        emailTxt.setText(curr.getEmail());
        phoneTxt.setText(curr.getPhone());
        addressTxt.setText(curr.getAddress());
        descTxt.setText(curr.getDescription());
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.info_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.delete_item) {
            new DeleteContactTask().execute();
        } else if (item.getItemId() == R.id.edit_item) {
            EditFragment fragment = EditFragment.of(curr);
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container,fragment)
                    .addToBackStack(null)
                    .commit();
        }
        return super.onOptionsItemSelected(item);
    }

    class DeleteContactTask extends AsyncTask<Void,Void,String> {
        private boolean isSuccess = true;
        private AlertDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog = new AlertDialog.Builder(getContext())
                    .setTitle("Deleting...")
                    .setView(R.layout.frame_progress_dialog)
                    .setCancelable(false)
                    .create();
            progressDialog.show();
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
            progressDialog.dismiss();
            if(isSuccess) {
                getFragmentManager().popBackStack();
            }else{
                new AlertDialog.Builder(getContext())
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
