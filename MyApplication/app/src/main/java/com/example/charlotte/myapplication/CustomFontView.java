package com.example.charlotte.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/**
 * Created by charlotte on 4/5/16.
 */
public class CustomFontView extends View {
    public CustomFontView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        Path p = new Path();
        p.moveTo(20, 20);
        p.lineTo(100, 200);
        p.lineTo(200, 100);
        p.lineTo(240, 155);
        p.lineTo(250, 175);
        p.lineTo(20, 20);
        canvas.drawPath(p, paint);
    }
}
