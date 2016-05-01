package com.example.charlotte.myapplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by charlotte on 18.04.16.
 */
public interface MessagingServerService {

    @GET("users/")
    Call<List> getUsersForGroup(@Query("group") String group );


    @GET("users/addUser")
    Call<List> addUser(@Query("username") String username, @Query("displayName") String displayName);


}
