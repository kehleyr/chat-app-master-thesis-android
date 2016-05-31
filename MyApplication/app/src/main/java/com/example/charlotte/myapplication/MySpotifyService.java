package com.example.charlotte.myapplication;

import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by charlotte on 19.05.16.
 */
public interface MySpotifyService {

    @GET("/v1/search")
    Call<TracksPager> searchTracks(@Query(value = "q") String q, @Query("type") String type);
}
