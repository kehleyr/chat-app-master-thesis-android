package com.example.charlotte.myapplication;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by charlotte on 4/5/16.
 */
public class ChatListAdapter extends ArrayAdapter<User> {

    private List itemList;

    public ChatListAdapter(Context context, int resource) {
        super(context, resource);
        initializeAdapter();


    }

    @Override
    public User getItem(int position) {
        return super.getItem(position);
    }

    public void initializeAdapter() {
        Call<List<User>> call = Application.getService().getUsersForGroup("g3");
        Log.d("TAG", "created call");

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {


                List<User> users = response.body();

//                Log.d("TAG", "on response called list isze is " + users.size());

                for (User user : users) {
                    Log.d("TAG", user.toString());

                }
                addAll(users);
                notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d("TAG", t.toString() + t.getMessage() + t.getStackTrace() + t.getCause());

            }
        });


        //TODO: test

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // return super.getView(position, convertView, parent);

        ViewHolder viewHolder;

        if (convertView == null) {

            // inflate the layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.contact_list_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.textViewItem = (TextView) convertView.findViewById(R.id.username);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();


        }
        User user = getItem(position);
        if (user!=null)
        {

           viewHolder.textViewItem.setText(user.getDisplayName());
        }

        return convertView;


    }



    static class ViewHolder {
        TextView textViewItem;
    }

}