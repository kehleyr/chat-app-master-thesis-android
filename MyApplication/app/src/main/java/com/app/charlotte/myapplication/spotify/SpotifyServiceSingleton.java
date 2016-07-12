package com.app.charlotte.myapplication.spotify;

import android.util.Log;

import com.app.charlotte.myapplication.ApiRequest;
import com.app.charlotte.myapplication.ApiRequestInterface;
import com.app.charlotte.myapplication.Application;
import com.app.charlotte.myapplication.Result;
import com.app.charlotte.myapplication.SpotifyPhotoCallback;
import com.app.charlotte.myapplication.SpotifyTrackCallback;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;

/**
 * Created by charlotte on 01.05.16.
 */
public class SpotifyServiceSingleton implements ApiRequestInterface {
    private static SpotifyServiceSingleton ourInstance = new SpotifyServiceSingleton();
    private SpotifyService spotify;
    private ApiRequest request;

    public static SpotifyServiceSingleton getInstance() {
        return ourInstance;
    }

    private SpotifyServiceSingleton() {
    }

    public void initialize()
    {

        SpotifyApi api = new SpotifyApi();

// Most (but not all) of the Spotify Web API endpoints require authorisation.
// If you know you'll only use the ones that don't require authorisation you can skip this step
     //  api.setAccessToken("myAccessToken");
       spotify = api.getService();


    }


    public void getPhotoPathForTrack(String trackId, final SpotifyPhotoCallback spotifyAPICallBack )
    {
        final String[] path = {""};
        trackId=trackId.replace("spotify:track:","");
        spotify.getTrack(trackId, new Callback<Track>() {
            @Override
            public void success(Track track, Response response) {
            String firstImageURL =track.album.images.get(0).url;
                spotifyAPICallBack.photoFetched(firstImageURL);
            }


            @Override
            public void failure(RetrofitError error) {

                //TODO: error case
               spotifyAPICallBack.photoFetched("");
            }
        });

      //  return path[0];
    }


    public void getSpotifyIdForSongData(String artist, String songname, final SpotifyTrackCallback spotifyAPICallBack) {


        final String queryString = "artist:" + artist + " track:" + songname;
        Log.d("TAG", queryString);
        String type = "track";
        Call<TracksPager> call = Application.getSpotifyService().searchTracks(queryString, type);

        call.enqueue(new retrofit2.Callback<TracksPager>() {
            @Override
            public void onResponse(Call<TracksPager> call, retrofit2.Response<TracksPager> response) {

                Log.d("TAG", call.request().toString());

                TracksPager tracksPager = response.body();

                String trackId = null;
                if (tracksPager.tracks.items.size() > 0) {

                    Track track = tracksPager.tracks.items.get(0);
                    trackId = "spotify:track:"+track.id;
                    Log.d("TAG", "fetched trackid is: " + trackId);

                }
                spotifyAPICallBack.trackFetched(trackId);

            }

            @Override
            public void onFailure(Call<TracksPager> call, Throwable t) {

                spotifyAPICallBack.trackFetched(null);

            }
        });

    }


    public void updateSpotifyIDForMessage(String messageId, String spotifyID)
    {
        Call<Result> call = Application.getService().updateSpotifySongId(messageId, spotifyID);
        call.enqueue(new retrofit2.Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {


                Log.e("spotifyService", t.getMessage()+t.getCause());
            }
        });


    }

    @Override
    public void setRequest(ApiRequest request) {

        this.request=request;
    }

    @Override
    public ApiRequest getRequest() {
      return request;
    }
}
