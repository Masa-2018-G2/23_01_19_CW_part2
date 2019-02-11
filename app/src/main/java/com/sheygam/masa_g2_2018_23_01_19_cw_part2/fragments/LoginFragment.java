package com.sheygam.masa_g2_2018_23_01_19_cw_part2.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.sheygam.masa_g2_2018_23_01_19_cw_part2.R;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.OkHttpProvider;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.StoreProvider;

import java.io.IOException;

public class LoginFragment extends Fragment implements View.OnClickListener {
//    private LoginFragmentListener listener;
    private EditText inputEmail, inputPassword;
    private Button loginBtn, regBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main,container,false);
        inputEmail = view .findViewById(R.id.input_email);
        inputPassword = view.findViewById(R.id.input_password);
        loginBtn = view.findViewById(R.id.login_btn);
        regBtn = view.findViewById(R.id.reg_btn);
        loginBtn.setOnClickListener(this);
        regBtn.setOnClickListener(this);
        return view;
    }

//    public void setListener(LoginFragmentListener listener) {
//        this.listener = listener;
//    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.login_btn){
            new LoginTask().execute();
        }else if(v.getId() == R.id.reg_btn){
            new RegTask().execute();
        }
    }
//
//    interface LoginFragmentListener{
//        void onLoginClick();
//        void onRegistrationClick();
//        void onLoginError(String error);
//        void onLoginSuccess();
//    }

    class LoginTask extends AsyncTask<Void,Void,String> {
        private String email, password;
        private boolean isSuccess = true;
        private AlertDialog progressDialog;

        @Override
        protected void onPreExecute() {
            email = inputEmail.getText().toString();
            password = inputPassword.getText().toString();
            progressDialog = new AlertDialog.Builder(getContext())
                    .setTitle("Login...")
                    .setView(R.layout.frame_progress_dialog)
                    .setCancelable(false)
                    .create();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String res = "Login ok";
            try {
                String token = OkHttpProvider.getInstance().login(email,password);
                StoreProvider.getInstance().save(token);
            }catch (IOException ex){
                ex.printStackTrace();
                isSuccess = false;
                res = "Connection Error!";
            }catch (Exception e) {
                isSuccess = false;
                res = e.getMessage();
            }

            return res;
        }

        @Override
        protected void onPostExecute(String str) {
            progressDialog.dismiss();
            if(isSuccess) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new ContactListFragment())
                        .commit();
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

    class RegTask extends AsyncTask<Void,Void,String>{
        private String email, password;
        private boolean isSuccess = true;
        private AlertDialog progressDialog;

        @Override
        protected void onPreExecute() {
            email = inputEmail.getText().toString();
            password = inputPassword.getText().toString();
            progressDialog = new AlertDialog.Builder(getContext())
                    .setTitle("Registration...")
                    .setView(R.layout.frame_progress_dialog)
                    .setCancelable(false)
                    .create();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String res = "Registration ok";
            try {
                String token = OkHttpProvider.getInstance().registration(email,password);
                StoreProvider.getInstance().save(token);
            }catch (IOException ex){
                isSuccess = false;
                res = "Connection Error!";
            }catch (Exception e) {
                isSuccess = false;
                res = e.getMessage();
            }

            return res;
        }

        @Override
        protected void onPostExecute(String str) {
            progressDialog.dismiss();
            if(isSuccess) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container,new ContactListFragment())
                        .commit();
            }else {
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
