package com.sheygam.masa_g2_2018_23_01_19_cw_part2.fragments;

import android.content.Intent;
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
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.EditActivity;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.R;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.OkHttpProvider;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.StoreProvider;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.dto.ContactDto;

import java.io.IOException;

public class EditFragment extends Fragment {
    private ContactDto curr;
    private EditText inputName,
            inputLastName,
            inputEmail,
            inputPhone,
            inputAddress,
            inputDesc;
    private AlertDialog progressDialog;

    public static EditFragment of(ContactDto contact) {
        EditFragment fragment = new EditFragment();
        fragment.curr = contact;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (curr == null) {
            curr = new ContactDto(-1, "", "", "", "", "", "");
        }
        progressDialog = new AlertDialog.Builder(getContext())
                .setTitle("Progress ...")
                .setView(R.layout.frame_progress_dialog)
                .setCancelable(false)
                .create();

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_edit, container, false);

        inputName = view.findViewById(R.id.input_name);
        inputLastName = view.findViewById(R.id.input_last_name);
        inputEmail = view.findViewById(R.id.input_email);
        inputPhone = view.findViewById(R.id.input_phone);
        inputAddress = view.findViewById(R.id.input_address);
        inputDesc = view.findViewById(R.id.input_desc);

        inputName.setText(curr.getName());
        inputLastName.setText(curr.getLastName());
        inputEmail.setText(curr.getEmail());
        inputPhone.setText(curr.getPhone());
        inputAddress.setText(curr.getAddress());
        inputDesc.setText(curr.getDescription());
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.done_item) {

            String name = inputName.getText().toString().trim();
            String lastName = inputLastName.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String phone = inputPhone.getText().toString().trim();
            String address = inputAddress.getText().toString().trim();
            String desc = inputDesc.getText().toString().trim();

            if (isValid(name, lastName, email, phone, address, desc)) {
                curr.setName(name);
                curr.setLastName(lastName);
                curr.setEmail(email);
                curr.setPhone(phone);
                curr.setAddress(address);
                curr.setDescription(desc);
                new SaveTask().execute();
            } else {
                Toast.makeText(getContext(), "All fields need by fill!", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void isProgressEnabled(boolean res) {
        if (res) {
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }

    private boolean isValid(String name,
                            String lastName,
                            String email,
                            String phone,
                            String address,
                            String desc) {
        return !name.isEmpty()
                && !lastName.isEmpty()
                && !email.isEmpty()
                && !phone.isEmpty()
                && !address.isEmpty()
                && !desc.isEmpty();
    }

    class SaveTask extends AsyncTask<Void, Void, String> {
        private boolean isSuccess = true;
        private ContactDto res;

        @Override
        protected void onPreExecute() {
            isProgressEnabled(true);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String token = StoreProvider.getInstance().getToken();
            try {
                if (curr.getId() < 0) {
                    res = OkHttpProvider.getInstance().addContact(curr, token);
                } else {
                    res = OkHttpProvider.getInstance().updateContact(curr, token);
                }
            } catch (IOException e) {
                e.printStackTrace();
                isSuccess = false;
                return "Connection error! Check your internet!";
            } catch (Exception e) {
                isSuccess = false;
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String str) {
            isProgressEnabled(false);
            if (isSuccess) {
                ViewFragment fragment = (ViewFragment) getFragmentManager().findFragmentByTag("VIEW_FRAG");
                if (fragment != null) {
                    fragment.setCurr(res);
                }
                getFragmentManager().popBackStack();
            } else {
                new AlertDialog.Builder(getContext())
                        .setTitle("Error")
                        .setMessage(str)
                        .setPositiveButton("Ok", null)
                        .setCancelable(false)
                        .create()
                        .show();
            }
        }
    }
}
