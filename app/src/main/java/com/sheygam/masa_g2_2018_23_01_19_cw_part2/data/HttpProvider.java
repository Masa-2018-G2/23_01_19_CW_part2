package com.sheygam.masa_g2_2018_23_01_19_cw_part2.data;

import android.util.Log;

import com.google.gson.Gson;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.dto.AuthRequestDto;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.dto.AuthResponseDto;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.dto.ContactDto;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.dto.ContactListDto;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.dto.ErrorResponseDto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class HttpProvider {
    private static final HttpProvider instance = new HttpProvider();
    private static final String BASE_URL = "https://contacts-telran.herokuapp.com/";
    private static final String TYPE_JSON = "application/json";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String AUTHORIZATION = "Authorization";
    private Gson gson;

    private HttpProvider(){
        gson = new Gson();
    }

    public static HttpProvider getInstance(){
        return instance;
    }

    public String registration(String email, String password) throws Exception {
        AuthRequestDto requestDto = new AuthRequestDto(email,password);
        String requestBody = gson.toJson(requestDto);

        HttpURLConnection connection = post("/api/registration");

        OutputStream os = connection.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
        bw.write(requestBody);
        bw.close();

        int responseCode = connection.getResponseCode();

        String responseBody;

        if(responseCode >= 200 && responseCode < 300){
            responseBody = read(connection.getInputStream());
            AuthResponseDto responseDto = gson.fromJson(responseBody,AuthResponseDto.class);
            return responseDto.getToken();
        }else if(responseCode == 400 || responseCode == 409){
            responseBody = read(connection.getErrorStream());
            ErrorResponseDto error = gson.fromJson(responseBody,ErrorResponseDto.class);
            throw new Exception(error.getMessage());
        }else{
            responseBody = read(connection.getErrorStream());
            Log.e("MY_TAG", "registration error: " + responseBody);
            throw new Exception("Server error!Try again later!");
        }
    }

    public String login(String email, String password) throws Exception {
        AuthRequestDto requestDto = new AuthRequestDto(email,password);
        String requestBody = gson.toJson(requestDto);

        HttpURLConnection connection = post("/api/login");

        OutputStream os = connection.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
        bw.write(requestBody);
        bw.close();

        int responseCode = connection.getResponseCode();
        String responseBody;

        if(responseCode >= 200 && responseCode < 300){
            responseBody = read(connection.getInputStream());
            AuthResponseDto responseDto = gson.fromJson(responseBody,AuthResponseDto.class);
            return responseDto.getToken();
        }else if(responseCode == 400 || responseCode == 401){
            responseBody = read(connection.getErrorStream());
            ErrorResponseDto error = gson.fromJson(responseBody,ErrorResponseDto.class);
            throw new Exception(error.getMessage());
        }else{
            responseBody = read(connection.getErrorStream());
            Log.e("MY_TAG", "login error: " + responseBody);
            throw new Exception("Server error! Try again later!");
        }
    }

    public List<ContactDto> getAllContacts(String token) throws Exception {
        HttpURLConnection connection = get("/api/contact",token);
        connection.connect();
        int responseCode = connection.getResponseCode();
        String responseBody;
        if(responseCode >= 200 && responseCode < 300){
            responseBody = read(connection.getInputStream());
            ContactListDto listDto = gson.fromJson(responseBody,ContactListDto.class);
            return listDto.getContacts();
        }else if(responseCode == 401){
            responseBody = read(connection.getErrorStream());
            ErrorResponseDto error = gson.fromJson(responseBody,ErrorResponseDto.class);
            throw new Exception(error.getMessage());
        }else{
            responseBody = read(connection.getErrorStream());
            Log.e("MY_TAG", "getAllContacts error: " + responseBody);
            throw new Exception("Server error! Call to support!");
        }
    }

    public ContactDto addContact(ContactDto contact, String token) throws Exception {
        String requestBody = gson.toJson(contact);
        HttpURLConnection connection = post("/api/contact",token);

        OutputStream os = connection.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
        bw.write(requestBody);
        bw.close();

        int responseCode = connection.getResponseCode();
        String responseBody;

        if(responseCode >= 200 && responseCode < 300){
            responseBody = read(connection.getInputStream());
            ContactDto contactDto = gson.fromJson(responseBody,ContactDto.class);
            return contactDto;
        }else if(responseCode == 400 || responseCode == 401 || responseCode == 409){
            responseBody = read(connection.getErrorStream());
            ErrorResponseDto error = gson.fromJson(responseBody,ErrorResponseDto.class);
            throw new Exception(error.getMessage());
        }else{
            responseBody = read(connection.getErrorStream());
            Log.e("MY_TAG", "addContact error: " + responseBody);
            throw new Exception("Server error! Call to support!");
        }
    }

    private HttpURLConnection post(String path) throws IOException {
        URL url = new URL(BASE_URL + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.addRequestProperty(CONTENT_TYPE,TYPE_JSON);
        return connection;
    }

    private HttpURLConnection post(String path, String token) throws IOException {
        HttpURLConnection connection = post(path);
        connection.addRequestProperty(AUTHORIZATION,token);
        return connection;
    }

    private HttpURLConnection get(String path, String token) throws IOException {
        URL url = new URL(BASE_URL+path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.addRequestProperty(AUTHORIZATION,token);
        return connection;
    }

    private HttpURLConnection delete(String path, String token) throws IOException {
        URL url = new URL(BASE_URL+path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.addRequestProperty(AUTHORIZATION,token);
        return connection;
    }

    private HttpURLConnection put(String path, String token) throws IOException {
        URL url = new URL(BASE_URL+path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.addRequestProperty(AUTHORIZATION,token);
        return connection;
    }

    public String read(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder response = new StringBuilder();
        while((line = br.readLine()) != null){
            response.append(line);
        }
        br.close();
        return response.toString();
    }
}
