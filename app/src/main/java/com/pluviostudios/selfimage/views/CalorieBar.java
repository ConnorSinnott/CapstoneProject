package com.pluviostudios.selfimage.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.pluviostudios.selfimage.R;

/**
 * Created by Spectre on 6/20/2016.
 */
public class CalorieBar extends View {

    Paint mBasePaint;
    Paint mProgressPaint;
    Paint mTextPaint;
    Paint mBarPaint;

    float mMax = 2000;
    float mProgress = 0;
    float mTextHeight = 0;

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

    public void setTextHeight(int textHeight) {
        mTextHeight = textHeight;
    }

    private void init() {

        mBasePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBasePaint.setColor(getResources().getColor(R.color.calbar_remaining));
        mBasePaint.setStyle(Paint.Style.FILL);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(getResources().getColor(R.color.calbar_progress));
        mProgressPaint.setStyle(Paint.Style.FILL);

        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setColor(getResources().getColor(R.color.calbar_divider));
        mBarPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(getResources().getColor(R.color.calbar_textColor));
        if (mTextHeight == 0) {
            mTextPaint.setTextSize(60);
            mTextHeight = mTextPaint.getTextSize();
        } else {
            mTextPaint.setTextSize(mTextHeight);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float progressX = (getRight() - getLeft()) * ((mProgress / mMax));

        canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), mBasePaint);
        canvas.drawRect(getLeft(), getTop(), getLeft() + progressX, getBottom(), mProgressPaint);
        canvas.drawRect((getLeft() + progressX) - 5, getTop(), (getLeft() + progressX) + 5, getBottom(), mBarPaint);

        String text = "Cal: " + Math.round(mProgress) + "/" + Math.round(mMax);

        canvas.drawText(text,
                getLeft() + ((getRight() - getLeft()) / 2) - mTextPaint.measureText(text) / 2,
                getTop() + getBottom() / 2 + mTextHeight / 2,
                mTextPaint);

    }


}
