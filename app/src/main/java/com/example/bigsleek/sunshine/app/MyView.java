package com.example.bigsleek.sunshine.app;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by BigSleek on 4/14/15.
 */
public class MyView extends View {

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int wMeasureSpec, int hMeasureSpec) {
        int hSpecMode = MeasureSpec.getMode(hMeasureSpec);
        int hSpecSize  = MeasureSpec.getSize(hMeasureSpec);
        int myHeight = hSpecSize;

        int wSpecMode = MeasureSpec.getMode(wMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(wMeasureSpec);
        int myWidth = wSpecSize;

        if (hSpecMode == MeasureSpec.EXACTLY)
            myHeight = hSpecSize;
        else if (hSpecMode == MeasureSpec.AT_MOST) // view can define it’s size
        {  // wrap Content

        }
       // Repeat for width
        if (wSpecMode == MeasureSpec.EXACTLY)
            myWidth = wSpecSize;
        else if (wSpecMode == MeasureSpec.AT_MOST) // view can define it’s size
        {  // wrap Content

        }

        setMeasuredDimension(myWidth, myHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
