package com.sheygam.masa_g2_2018_23_01_19_cw_part2.fragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sheygam.masa_g2_2018_23_01_19_cw_part2.R;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.dto.ContactDto;

import java.util.List;

public class RVContactsAdapter extends RecyclerView.Adapter<RVContactsAdapter.MyViewHolder> {
    private List<ContactDto> list;
    private AdapterCallback callback;
    private ContactDto lastRemoved;
    private int lastRemovedPosition = -1;

    public RVContactsAdapter(List<ContactDto> list, AdapterCallback callback) {
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup recyclerView, int type) {
        View view = LayoutInflater.from(recyclerView.getContext())
                .inflate(R.layout.row_contact, recyclerView, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        ContactDto contact = list.get(position);
        myViewHolder.nameTxt.setText(contact.getName());
        myViewHolder.phoneTxt.setText(contact.getPhone());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public ContactDto get(int position) {
        return list.get(position);
    }

    public void removeWithoutNotify(int position) {
        list.remove(position);
    }

    public ContactDto remove(int position) {
        lastRemoved = list.remove(position);
        lastRemovedPosition = position;
        notifyItemRemoved(position);
        return lastRemoved;
    }

    public void cancelLastRemove(){
        if(lastRemovedPosition >= 0 && lastRemoved!=null){
            list.add(lastRemovedPosition,lastRemoved);
            notifyItemInserted(lastRemovedPosition);
            lastRemovedPosition = -1;
            lastRemoved = null;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nameTxt, phoneTxt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.name_txt);
            phoneTxt = itemView.findViewById(R.id.phone_txt);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (callback != null) {
                callback.onRowClick(getAdapterPosition(), list.get(getAdapterPosition()));
            }
        }
    }

    interface AdapterCallback {
        void onRowClick(int position, ContactDto contact);
    }
}
