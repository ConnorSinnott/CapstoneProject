package com.pluviostudios.selfimage.mainActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Spectre on 6/20/2016.
 */
public class CalorieBar extends View {

    Paint mBasePaint;
    Paint mProgressPaint;
    Paint mBarPaint;

    int mMax = 2000;
    int mProgress = 0;

    public CalorieBar(Context context) {
        super(context);
        init();
    }

    public CalorieBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CalorieBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CalorieBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setMax(int max) {
        mMax = max;
        invalidate();
        requestLayout();

    }

    public void setProgress(int progress) {
        mProgress = progress;
        invalidate();
        requestLayout();

    }

    private void init() {
        mBasePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBasePaint.setColor(getResources().getColor(android.R.color.darker_gray));
        mBasePaint.setStyle(Paint.Style.FILL);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(getResources().getColor(android.R.color.holo_orange_light));
        mBasePaint.setStyle(Paint.Style.FILL);

        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setColor(getResources().getColor(android.R.color.holo_blue_bright));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int progressX = (int) ((getRight() - getLeft()) * (((float) mProgress / (float) mMax)));
        canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), mBasePaint);
        canvas.drawRect(getLeft(), getTop(), getLeft() + progressX, getBottom(), mProgressPaint);

    }
}
