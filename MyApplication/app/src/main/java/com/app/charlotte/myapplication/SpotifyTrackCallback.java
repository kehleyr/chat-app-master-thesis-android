package com.app.charlotte.myapplication;

import kaaes.spotify.webapi.android.models.Track;

public interface SpotifyTrackCallback {

    void trackFetched(Track trackId);
}
