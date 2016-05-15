package com.example.charlotte.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by charlotte on 01.05.16.
 */
public class SpotifyView extends LinearLayout {
    private  String songTitle;
    private  int imageResId=0;

    public SpotifyView(Context context) {
        super(context);
        initializeViews(context);

    }

    private void initializeViews(Context context)

    {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.spotify_view, this);



    }
    public SpotifyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SpotifyView,
                0, 0);
     applyAttrs(a);

    }

    public SpotifyView(Context context,
                                 AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SpotifyView,
                0, 0);
        applyAttrs(a);

    }

    private void applyAttrs(TypedArray a) {
        try {
            imageResId = a.getResourceId(R.styleable.SpotifyView_src, 0);
            songTitle = a.getString(R.styleable.SpotifyView_songTitle);
            setImageRes(imageResId);
            setSongTitle(songTitle);
        } finally {
            a.recycle();
        }
    }

    public void setImageBitmap(Bitmap b)
    {

        ImageView imageView = (ImageView) findViewById(R.id.artistImage);
        imageView.setImageBitmap(b);

    }

    public void setImageRes(int resId)
    {
        ImageView imageView = (ImageView) findViewById(R.id.artistImage);
        if (resId>0)
            imageView.setImageResource(resId);

    }

    public void setSongTitle(String text)
    {
        TextView textView = (TextView) findViewById(R.id.textSong);
        textView.setText(text);
    }

    public ImageView getArtistImageView()
    {
        return (ImageView) findViewById(R.id.artistImage);

    }

}
