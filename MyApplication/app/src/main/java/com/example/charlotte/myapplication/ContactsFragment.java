package com.example.charlotte.myapplication;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by charlotte on 4/5/16.
 */
public class ContactsFragment extends ListFragment {


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //these are example values
        //TODO: load real values via asynctask
        String[] values = new String[] { "Lisa", "Tom", "Fritz", "Lara"};
        ArrayAdapter<User> adapter = new ChatListAdapter(getActivity(), android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //TODO: send the position in bundle to new activity

        User user= (User) getListView().getItemAtPosition(position);

        Intent intent = new Intent(getActivity(), SingleConversationActivity.class);
        intent.putExtra("username", user.getUsername());
        startActivity(intent);

    }




}
