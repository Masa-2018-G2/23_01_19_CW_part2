package com.sheygam.masa_g2_2018_23_01_19_cw_part2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ContactListAdapter extends BaseAdapter {
    private List<Contact> list;
    private Context context;

    public ContactListAdapter(List<Contact> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Contact getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.row_contact,parent,false);
        }
        Contact contact = list.get(position);
        TextView nameTxt = convertView.findViewById(R.id.name_txt);
        TextView phoneTxt = convertView.findViewById(R.id.phone_txt);
        nameTxt.setText(contact.getName());
        phoneTxt.setText(contact.getPhone());
        return convertView;
    }
}
