package com.sheygam.masa_g2_2018_23_01_19_cw_part2;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class StoreProvider {
    private static final StoreProvider instance = new StoreProvider();
    public static final String SP_AUTH = "AUTH";
    public static final String SP_DATA = "DATA";
    public static final String CURR = "CURR";
    private Context context;

    private StoreProvider() {
    }

    public static StoreProvider getInstance() {
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
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
                .remove(CURR)
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
}
