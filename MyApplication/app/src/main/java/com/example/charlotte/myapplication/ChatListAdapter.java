package com.example.charlotte.myapplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by charlotte on 4/5/16.
 */
public class ChatListAdapter extends ArrayAdapter {

    private List itemList;
    public ChatListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
