package com.app.charlotte.myapplication.spotify;

import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by charlotte on 19.05.16.
 */
public interface MySpotifyService {

    @GET("/v1/search")
    Call<TracksPager> searchTracks(@Query(value = "q") String q, @Query("type") String type);
}
