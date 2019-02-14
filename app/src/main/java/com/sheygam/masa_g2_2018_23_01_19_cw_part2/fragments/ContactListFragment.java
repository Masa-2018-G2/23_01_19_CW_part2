package com.sheygam.masa_g2_2018_23_01_19_cw_part2.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sheygam.masa_g2_2018_23_01_19_cw_part2.ContactListActivity;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.ContactListAdapter;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.R;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.OkHttpProvider;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.StoreProvider;
import com.sheygam.masa_g2_2018_23_01_19_cw_part2.data.dto.ContactDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactListFragment extends Fragment implements AdapterView.OnItemClickListener, RVContactsAdapter.AdapterCallback {
    private ListView contactList;
    private RecyclerView contactListRv;
    private RVContactsAdapter adapter;

    @Override
    public void onStart() {
        super.onStart();
        new LoadTask().execute();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_contact_list, container, false);
        contactList = view.findViewById(R.id.contact_list);
        contactList.setOnItemClickListener(this);
        contactListRv = view.findViewById(R.id.contact_list_rv);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), manager.getOrientation());
        contactListRv.setLayoutManager(manager);
        contactListRv.addItemDecoration(divider);
        ItemTouchHelper helper = new ItemTouchHelper(new MyTouchCallback());
        helper.attachToRecyclerView(contactListRv);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout_item) {
            logout();
        } else if (item.getItemId() == R.id.add_item) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new EditFragment())
                    .addToBackStack(null)
                    .commit();
        } else if (item.getItemId() == R.id.delete_all_item) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete all?")
                    .setMessage("Are you sure that you want delete all your contacts?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new ClearTask().execute();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .setCancelable(false)
                    .create()
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        StoreProvider.getInstance().logout();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new LoginFragment())
                .commit();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ContactDto contact = (ContactDto) parent.getAdapter().getItem(position);
        ViewFragment fragment = ViewFragment.of(contact);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment, "VIEW_FRAG")
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onRowClick(int position, ContactDto contact) {
        ViewFragment fragment = ViewFragment.of(contact);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment, "VIEW_FRAG")
                .addToBackStack(null)
                .commit();
    }


    class MyTouchCallback extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(0, ItemTouchHelper.END | ItemTouchHelper.START);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            final int position = viewHolder.getAdapterPosition();
            final ContactDto removed = adapter.remove(position);
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete?")
                    .setMessage("Delete this contact?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new DeleteContactTask(removed.getId(), position)
                            .execute();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.cancelLastRemove();
                        }
                    })
                    .create()
                    .show();
        }
    }

    class LoadTask extends AsyncTask<Void, Void, String> {
        private AlertDialog dialog;
        private boolean isSuccess = true;
        private List<ContactDto> list = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            dialog = new AlertDialog.Builder(getContext())
                    .setTitle("Loading...")
                    .setView(R.layout.frame_progress_dialog)
                    .setCancelable(false)
                    .create();
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String token = StoreProvider.getInstance().getToken();
            String res = "Response ok";
            try {
                list = OkHttpProvider.getInstance().getAllContacts(token);
            } catch (IOException e) {
                isSuccess = false;
                res = "Connection error!";
            } catch (Exception e) {
                isSuccess = false;
                res = e.getMessage();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String str) {
            dialog.dismiss();
            if (isSuccess) {
//                ContactListAdapter adapter = new ContactListAdapter(list, getContext());
//                contactList.setAdapter(adapter);
                adapter = new RVContactsAdapter(list, ContactListFragment.this);
                contactListRv.setAdapter(adapter);
            } else {
                new AlertDialog.Builder(getContext())
                        .setMessage(str)
                        .setCancelable(false)
                        .setPositiveButton("Ok", null)
                        .create()
                        .show();
            }
        }
    }

    class ClearTask extends AsyncTask<Void, Void, String> {
        private boolean isSuccess = true;
        private AlertDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new AlertDialog.Builder(getContext())
                    .setTitle("Loading...")
                    .setView(R.layout.frame_progress_dialog)
                    .setCancelable(false)
                    .create();
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String token = StoreProvider.getInstance().getToken();
            try {
                OkHttpProvider.getInstance().clear(token);
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
            dialog.dismiss();
            if (isSuccess) {
//                contactList.setAdapter(new ContactListAdapter(
//                        new ArrayList<ContactDto>(), getContext()));
                adapter = new RVContactsAdapter(new ArrayList<ContactDto>(), null);
                contactListRv.setAdapter(adapter);
            } else {
                new AlertDialog.Builder(getContext())
                        .setMessage(str)
                        .setCancelable(false)
                        .setPositiveButton("Ok", null)
                        .create()
                        .show();
            }
        }
    }

    class DeleteContactTask extends AsyncTask<Void, Void, String> {
        private boolean isSuccess = true;
        private AlertDialog progressDialog;
        private long id;
        private int position;

        public DeleteContactTask(long id, int position) {
            this.id = id;
            this.position = position;
        }

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
                OkHttpProvider.getInstance().delete(id, token);
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
            progressDialog.dismiss();
            if (isSuccess) {
                Toast.makeText(getContext(), "Contact was removed!", Toast.LENGTH_SHORT).show();
            } else {
                adapter.cancelLastRemove();
                new AlertDialog.Builder(getContext())
                        .setTitle("Error!")
                        .setMessage(str)
                        .setPositiveButton("Ok", null)
                        .setCancelable(false)
                        .create()
                        .show();
            }
        }
    }
}
