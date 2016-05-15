package com.example.charlotte.myapplication;

/**
 * Created by charlotte on 01.05.16.
 */
public class Song {

    String artist;
    String songname;

    public String getSpotifyID() {
        return spotifyID;
    }

    public void setSpotifyID(String spotifyID) {
        this.spotifyID = spotifyID;
    }

    String spotifyID;

    public Song(String artist, String songname) {
        this.artist = artist;
        this.songname = songname;
    }

    public Song(String artist, String songname, String spotifyID) {
        this.artist = artist;
        this.songname = songname;
        this.spotifyID=spotifyID;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    @Override
    public String toString() {
        return "Song{" +
                "artist='" + artist + '\'' +
                ", songname='" + songname + '\'' +
                '}';
    }
}
