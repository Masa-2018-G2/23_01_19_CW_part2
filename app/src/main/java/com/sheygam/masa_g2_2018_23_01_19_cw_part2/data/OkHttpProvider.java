package com.sheygam.masa_g2_2018_23_01_19_cw_part2.data;

import android.util.Log;

import com.google.gson.Gson;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.dto.AuthRequestDto;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.dto.AuthResponseDto;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.dto.ContactDto;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.dto.ContactListDto;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.dto.ErrorResponseDto;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpProvider {
    private static final OkHttpProvider ourInstance = new OkHttpProvider();
    private static final String BASE_URL = "https://contacts-telran.herokuapp.com/";
    private static final String TAG = "MY_TAG";
    public static final String SERVER_ERROR = "Server error! Call to support";
    public static final String AUTHORIZATION = "Authorization";
    private Gson gson;
    private OkHttpClient client;
    private MediaType JSON;

    public static OkHttpProvider getInstance() {
        return ourInstance;
    }

    private OkHttpProvider() {
        gson = new Gson();
        JSON = MediaType.get("application/json; charset=utf-8");
//        client = new OkHttpClient();
        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15,TimeUnit.SECONDS)
                .build();
    }

    public String registration(String email, String password) throws Exception {
        AuthRequestDto requestDto = new AuthRequestDto(email,password);
        String requestJson = gson.toJson(requestDto);

        RequestBody requestBody = RequestBody.create(JSON,requestJson);

        Request request = new Request.Builder()
                .url(BASE_URL+"api/registration")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        ResponseBody responseBody = response.body();
        String responseJson = responseBody.string();
        if(response.isSuccessful()){
            AuthResponseDto responseDto = gson.fromJson(responseJson,AuthResponseDto.class);
            return responseDto.getToken();
        }else if(response.code() == 400 || response.code() == 409){
            ErrorResponseDto error = gson.fromJson(responseJson,ErrorResponseDto.class);
            throw new Exception(error.getMessage());
        }else{
            Log.e("MY_TAG", "registrationOkHttp error : " + responseJson);
            throw new Exception(SERVER_ERROR);
        }
    }

    public String login(String email, String password) throws Exception {
        AuthRequestDto requestDto = new AuthRequestDto(email,password);
        String requestJson = gson.toJson(requestDto);

        RequestBody requestBody = RequestBody.create(JSON,requestJson);

        Request request = new Request.Builder()
                .url(BASE_URL + "api/login")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        String responseJson = response.body().string();
        if(response.isSuccessful()){
            AuthResponseDto responseDto = gson.fromJson(responseJson,AuthResponseDto.class);
            return responseDto.getToken();
        }else if(response.code() == 400 || response.code() == 401){
            ErrorResponseDto error = gson.fromJson(responseJson,ErrorResponseDto.class);
            throw new Exception(error.getMessage());
        }else{
            Log.e(TAG, "login error: " + responseJson);
            throw new Exception(SERVER_ERROR);
        }
    }

    public List<ContactDto> getAllContacts(String token) throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL+"api/contact")
                .addHeader(AUTHORIZATION,token)
                .build();
        Response response = client.newCall(request).execute();
        String responseJson = response.body().string();
        if(response.isSuccessful()){
            ContactListDto listDto = gson.fromJson(responseJson,ContactListDto.class);
            return listDto.getContacts();
        }else if(response.code() == 401){
            ErrorResponseDto error = gson.fromJson(responseJson,ErrorResponseDto.class);
            throw new Exception(error.getMessage());
        }else{
            Log.e(TAG, "getAllContacts error: " + responseJson);
            throw new Exception(SERVER_ERROR);
        }
    }

    public ContactDto addContact(ContactDto contact, String token) throws Exception {
        String requestJson = gson.toJson(contact);

        RequestBody requestBody = RequestBody.create(JSON,requestJson);

        Request request = new Request.Builder()
                .url(BASE_URL+"api/contact")
                .post(requestBody)
                .addHeader(AUTHORIZATION,token)
                .build();

        Response response = client.newCall(request).execute();
        String responseJson = response.body().string();
        if (response.isSuccessful()){
            return gson.fromJson(responseJson,ContactDto.class);
        }else if(response.code() == 400 || response.code() == 401 || response.code() == 409){
            ErrorResponseDto error = gson.fromJson(requestJson,ErrorResponseDto.class);
            throw new Exception(error.getMessage());
        }else{
            Log.e(TAG, "addContact error: " + responseJson);
            throw new Exception(SERVER_ERROR);
        }
    }

    public ContactDto updateContact(ContactDto contact, String token) throws Exception {
        String requestJson = gson.toJson(contact);
        RequestBody requestBody = RequestBody.create(JSON,requestJson);

        Request request = new Request.Builder()
                .url(BASE_URL + "api/contact")
                .put(requestBody)
                .addHeader(AUTHORIZATION,token)
                .build();
        Response response = client.newCall(request).execute();
        String responseJson = response.body().string();
        if(response.isSuccessful()){
            return gson.fromJson(responseJson,ContactDto.class);
        }else if(response.code() == 400 || response.code() == 401 || response.code() == 404){
            ErrorResponseDto error = gson.fromJson(responseJson,ErrorResponseDto.class);
            throw new Exception(error.getMessage());
        }else{
            Log.e(TAG, "updateContact error: "+ responseJson);
            throw new Exception(SERVER_ERROR);
        }
    }

    public void delete(long id, String token) throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + "api/contact/" + id)
                .delete()
                .addHeader(AUTHORIZATION,token)
                .build();
        Response response = client.newCall(request).execute();
        String responseJson = response.body().string();
        if(response.code() == 400 || response.code() == 401 || response.code() == 404){
            ErrorResponseDto error = gson.fromJson(responseJson,ErrorResponseDto.class);
            throw new Exception(error.getMessage());
        }else if (!response.isSuccessful()){
            Log.e(TAG, "delete contact by id error: " + responseJson);
            throw new Exception(SERVER_ERROR);
        }
    }

    public void clear(String token) throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + "api/clear")
                .delete()
                .addHeader(AUTHORIZATION,token)
                .build();
        Response response = client.newCall(request).execute();
        String responseJson = response.body().string();
        if(response.code() == 401){
            ErrorResponseDto error = gson.fromJson(responseJson,ErrorResponseDto.class);
            throw new Exception(error.getMessage());
        }else if(!response.isSuccessful()){
            Log.e(TAG, "clear error: " + responseJson);
            throw new Exception(SERVER_ERROR);
        }
    }
}
