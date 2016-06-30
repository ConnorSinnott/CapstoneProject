package com.pluviostudios.selfimage.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.preference.PreferenceManager;
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

    public void setProgress(int progress) {
        mProgress = progress;
        invalidate();
        requestLayout();

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

        mTextPaint.setTextSize(60);
        mTextHeight = mTextPaint.getTextSize();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int max = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString(
                getContext().getString(R.string.pref_cal_quota_key),
                getContext().getString(R.string.pref_cal_quota_default)));
        float progressX = (getRight() - getLeft()) * (Math.min((mProgress / max), max));
        String text = getContext().getString(R.string.calorie_string) + ": " +
                Math.round(mProgress) + "/" + Math.round(max);

        canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), mBasePaint);
        canvas.drawRect(getLeft(), getTop(), getLeft() + progressX, getBottom(), mProgressPaint);
        canvas.drawRect((getLeft() + progressX) - 5, getTop(), (getLeft() + progressX) + 5, getBottom(), mBarPaint);
        canvas.drawText(text,
                getLeft() + ((getRight() - getLeft()) / 2) - mTextPaint.measureText(text) / 2,
                getTop() + getBottom() / 2 + mTextHeight / 2,
                mTextPaint);

    }


}
