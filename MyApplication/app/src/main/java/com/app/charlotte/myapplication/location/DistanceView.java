package com.app.charlotte.myapplication.location;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.app.charlotte.myapplication.R;

/**
 * Created by charlotte on 21.06.16.
 */
public class DistanceView extends View {


    public static final float DEFAULT_MARKER_SIZE = 50.0f;
    private  int textsize;
    private  float markerSize;
    private int color;
    private  float distance;
    private String distanceAnnotation;
    private Paint paint;
    private Path path;
    private Rect r;
    private float lineLength;
    private float startX;
    private int x=0;
    private int initialWidth=0;
    private float textOffset;
    public static final float LINE_LENGTH_MIN_DIP=50;

    public DistanceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DistanceView,
                0, 0);
 distance = Float.parseFloat(a.getString(R.styleable.DistanceView_distanceFraction));
distanceAnnotation=a.getString(R.styleable.DistanceView_distanceAnnotation);
        color=a.getColor(R.styleable.DistanceView_textColor, Color.BLACK);
        markerSize=convertDpToPixel(Float.parseFloat(a.getString(R.styleable.DistanceView_markerSize)), getContext());
        textsize = a.getInt(R.styleable.DistanceView_textSize,36);



        Log.d("TAG", "distance: " + distance);

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DistanceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setDistanceFraction(float distance)
    {
       this.distance=distance;


    }

    public void setDistanceAnnotation(String distance)
    {
        this.distanceAnnotation=distance;

    }
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
public void init()
{

    paint = new Paint();
    paint.setColor(getResources().getColor(R.color.colorPrimary));

    int[] attrs = new int[] { android.R.attr.textColorSecondary };
    TypedArray a = getContext().getTheme().obtainStyledAttributes(R.style.AppTheme, attrs);
    color = a.getColor(0, Color.RED);
    a.recycle();


   r = new Rect();

    paint.setStrokeWidth(5.0f);

    path = new Path();
    textOffset=30.0f;



}

/*
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (initialWidth==0)
        {
            initialWidth= (int) ( distance*(0.9*parentWidth));
        }

        Log.d("TAG", "initial width in dinstanceview: "+initialWidth);


        this.setMeasuredDimension(initialWidth
                , parentHeight);

    }*/
public static float convertPixelsToDp(float px, Context context){
    Resources resources = context.getResources();
    DisplayMetrics metrics = resources.getDisplayMetrics();
    float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    return dp;
}
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setTextSize(textsize);

        //das erste mal messen diesmal für maximalen text
        paint.getTextBounds(distanceAnnotation, 0, distanceAnnotation.length(), r);
        initialWidth=getWidth();

        int y = getHeight();
        float imageWidth=(markerSize>0.0f)?markerSize: DEFAULT_MARKER_SIZE;
        float startY=y/2+imageWidth/2;
        startX = markerSize/2;



     lineLength=(distance*initialWidth)-markerSize;
        Log.d("TAG", "original line length in px: "+lineLength);

       // float lineLengthInDip = convertPixelsToDp(lineLength, getContext());


        if (lineLength < (r.width()+markerSize)) {

            lineLength=r.width()+markerSize;
            Log.d("TAG", "set line length to "+r.width());

            Log.d("TAG", "line length in px: "+lineLength);

        }

        //das zweite mal messen
      //  paint.getTextBounds(distanceAnnotation, 0, distanceAnnotation.length(), r);


        //path.close();
        //canvas.drawPath(path, paint);

        paint.setColor(getResources().getColor(R.color.colorPrimary));



        float lineEndX=startX+lineLength;


        canvas.drawLine(startX, startY, lineEndX, startY, paint);

        //path.moveTo(lineEndX, lineEndY-imageWidth);
        //path.lineTo(lineEndX, lineEndY+imageWidth);
        //path.lineTo(lineEndX+imageWidth, lineEndY);
        //path.close();

        //paint.setColor(getResources().getColor(R.color.colorPrimary));
        //canvas.drawPath(path, paint);

        paint.setColor(Color.parseColor("#8a000000"));
        paint.setAntiAlias(true);





float yTextOffset=30.0f;

        canvas.drawText(distanceAnnotation, startX+lineLength/2.0f - r.width()/2.0f-r.left, startY-yTextOffset, paint);


        Drawable d = getResources().getDrawable(R.drawable.location_pin);
        if (d!=null) {
            //left, top, right, bottom
          //TODO: mit formel

            d.setBounds((int)(startX-imageWidth/2), (int) (startY-imageWidth), (int)(startX+imageWidth/2), (int)startY);
            Log.d("TAG", "draw canvas");
            d.draw(canvas);
            d.setBounds((int)(lineEndX-imageWidth/2),(int) (startY-imageWidth), (int) (lineEndX+imageWidth/2), (int) startY);
            d.draw(canvas);
        }
        //canvas.drawText("Eure Distanz", lineEndX+textOffset, startY, paint);
        //ViewGroup.LayoutParams params = getLayoutParams();
        //params.height=getHeight();
       // //params.width = (int) (lineEndX+imageWidth/2.0f);

        //Log.d("TAG", "idth: "+params.width+" height "+params.height);

//this.requestLayout();
    }
}
