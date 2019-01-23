package com.sheygam.masa_g2_2018_23_01_19_cw_part2;

import android.support.annotation.NonNull;

import java.util.Objects;

public class Contact {
    private String name, lastName, email, phone, address, desc;

    public Contact(String name, String lastName, String email, String phone, String address, String desc) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getDesc() {
        return desc;
    }

    public String toString(){
        return name + "," + lastName + "," + email + "," + phone + "," + address + "," + desc;
    }

    public static Contact of(@NonNull String str){
        Objects.requireNonNull(str);
        String[] arr = str.split(",");
        if(arr.length < 6){
            throw new IllegalArgumentException("Str need have 6 string value separated by coma");
        }
        Contact c = new Contact(arr[0],arr[1],arr[2],arr[3],arr[4],arr[5]);
        return c;
    }
}
