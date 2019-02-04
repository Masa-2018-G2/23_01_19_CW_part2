package com.sheygam.masa_g2_2018_23_01_19_cw_part2.data;

import android.content.Context;

import com.sheygam.masa_g2_2018_23_01_19_cw_part2.Contact;

import java.util.ArrayList;
import java.util.List;

public class StoreProvider {
    private static final StoreProvider instance = new StoreProvider();
    private static final String SP_AUTH = "AUTH";
    private static final String SP_DATA = "DATA";
    private static final String CURR = "CURR";
    private static final String TOKEN = "TOKEN";
    private Context context;

    private StoreProvider() {
    }

    public static StoreProvider getInstance() {
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void save(String token){
        context.getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE)
                .edit()
                .putString(TOKEN, token)
                .commit();
    }

    public String getToken(){
        return context.getSharedPreferences(SP_AUTH,Context.MODE_PRIVATE)
                .getString(TOKEN,null);
    }

    public boolean login(String email, String password) {
        return context.getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE)
                .edit()
                .putString(CURR, email + "&" + password)
                .commit();
    }

    public boolean logout() {
        return context.getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE)
                .edit()
                .remove(TOKEN)
                .commit();
    }

    public String getUserId() {
        return context.getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE)
                .getString(CURR, null);
    }

    public List<Contact> contacts() {
        String userId = getUserId();
        if (userId == null) {
            throw new IllegalStateException();
        }
        String data = context.getSharedPreferences(SP_DATA, Context.MODE_PRIVATE)
                .getString(userId, null);
        List<Contact> list = new ArrayList<>();
        if (data != null) {
            String[] contactsStr = data.split(";");
            for (String str : contactsStr) {
                list.add(Contact.of(str));
            }
        }
        return list;
    }

    public void add(Contact contact){
        List<Contact> list = contacts();
        list.add(contact);
        saveContacts(list);
    }

    public void update(Contact contact, int position){
        List<Contact> list = contacts();
        list.set(position,contact);
        saveContacts(list);
    }

    public void saveContacts(List<Contact> list){
        String userId = getUserId();
        if(userId!=null){
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                builder.append(list.get(i).toString());
                if(i < list.size()-1){
                    builder.append(";");
                }
            }
            if(builder.toString().isEmpty()){
                context.getSharedPreferences(SP_DATA,Context.MODE_PRIVATE)
                        .edit()
                        .remove(userId)
                        .commit();
            }else{
                context.getSharedPreferences(SP_DATA,Context.MODE_PRIVATE)
                        .edit()
                        .putString(userId,builder.toString())
                        .commit();
            }
        }
    }

    public void remove(int position){
        List<Contact> list = contacts();
        if(position < 0 || position >= list.size()){
            throw new IndexOutOfBoundsException(String.valueOf(position));
        }
        list.remove(position);
        saveContacts(list);
    }

    public Contact getByPosition(int position){
        List<Contact> list = contacts();
        if(position < 0 || position >= list.size()){
            throw new IndexOutOfBoundsException(String.valueOf(position));
        }
        return list.get(position);
    }
}
