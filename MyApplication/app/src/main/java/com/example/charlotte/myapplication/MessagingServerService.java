package com.example.charlotte.myapplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by charlotte on 18.04.16.
 */
public interface MessagingServerService {

    @GET("users")
    Call<List<User>> getUsersForGroup(@Query("group") String group);


    @GET("users/addUser")
    Call<List> addUser(@Query("username") String username, @Query("displayName") String displayName);


    ///message/getConversation?fromUserName=user1234&toUserName=user456&queryLimit=10&skip=0
    @GET("message/getConversation")
    Call<List<Message>> getConversation(@Query("fromUserName") String fromUserName, @Query("toUserName") String toUserName, @Query("skip") int skip);

///message/writeMessage?fromUserName=user1234&toUserName=user456&messageText=HelloWorld
    @GET("message/writeMessage")
    Call<Result> writeMessage(@Query("fromUserName") String fromUserName, @Query("toUserName") String toUserName, @Query("messageText") String messageText);


    @POST("message/writeMessagePost")
    Call<Message> writeMessagePost(@Body Message message);

    @GET("pushes/addTokenForUser")
    Call<Result> sendToken(@Query("username") String username, @Query("token") String token);

    @GET("spotify_auth/refresh_token")
    Call<RefreshAndAccessToken> getAccessTokenForRefreshToken(@Query("client_id") String clientId, @Query("client_secret") String clientSecret, @Query("refresh_token") String refreshToken);

    @GET("/spotify_auth/callback")
    Call<RefreshAndAccessToken> getRefreshAndAccessTokens(@Query("code") String code, @Query("state") String state);


    @GET("message/updateMessageDistance")
    Call<Result> updateMessageDistance(@Query("messageId") String messageId, @Query("usersDistance") float userDistance);


    @GET("users/updateDisplayName")
    Call<Result> updateDisplayName(@Query("username") String username, @Query("displayName") String displayName);

    @GET("message/updateSpotifySongId")
    Call<Result> updateSpotifySongId(@Query("messageId") String messageId, @Query("spotifyID") String spotifyId);
}
