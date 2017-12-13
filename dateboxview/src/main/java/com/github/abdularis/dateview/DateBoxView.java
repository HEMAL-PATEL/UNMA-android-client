package com.github.abdularis.dateview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by abdularis on 09/12/17.
 */

public class DateBoxView extends LinearLayout {
    private static final int DEF_DAY_BG_COLOR = 0xFFe1e1e1;
    private static final int DEF_YEAR_BG_COLOR = 0xFFa1a1a1;
    private static final int DEF_DAY_TEXT_COLOR = 0XFF000000;
    private static final int DEF_YEAR_TEXT_COLOR = 0XFFFFFFFF;
    private static final int DEFAULT_DAY_TEXT_SIZE = 32;
    private static final int DEFAULT_YEAR_TEXT_SIZE = 14;

    private TextView mDayTextView;
    private RelativeLayout mDayLayout;

    private TextView mYearTextView;
    private RelativeLayout mYearLayout;

    private float mDayTextSize;
    private float mYearTextSize;

    private int mDayBgColor;
    private int mYearBgColor;

    private int mDayBg;
    private int mYearBg;

    private int mDayColor;
    private int mYearColor;

    private int mDayPadding;
    private int mDayPaddingTop;
    private int mDayPaddingBottom;
    private int mDayPaddingLeft;
    private int mDayPaddingRight;

    private int mYearPadding;
    private int mYearPaddingTop;
    private int mYearPaddingBottom;
    private int mYearPaddingLeft;
    private int mYearPaddingRight;
    private float mRoundedRadius;

    public DateBoxView(Context context) {
        super(context);
        init(context, null);
    }

    public DateBoxView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DateBoxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd", Locale.US);
    private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("MMM yy", Locale.US);
    public void setDate(@NonNull Date date) {
        mDayTextView.setText(DAY_FORMAT.format(date));
        mYearTextView.setText(YEAR_FORMAT.format(date));
        invalidate();
        requestLayout();
    }

    public void setDayTextSize(float dayTextSize) {
        mDayTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, dayTextSize);
    }

    public void setYearTextSize(float yearTextSize) {
        mYearTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, yearTextSize);
    }

    public void setDayBg(int resId) {
        mDayLayout.setBackgroundResource(resId);
    }

    public void setYearBg(int resId) {
        mYearLayout.setBackgroundResource(resId);
    }

    public void setDayBgColor(int dayBgColor) {
        mDayLayout.getBackground().setColorFilter(new PorterDuffColorFilter(dayBgColor, PorterDuff.Mode.MULTIPLY));
    }

    public void setYearBgColor(int yearBgColor) {
        mYearLayout.getBackground().setColorFilter(new PorterDuffColorFilter(yearBgColor, PorterDuff.Mode.MULTIPLY));
    }

    public void setDayColor(int dayColor) {
        mDayTextView.setTextColor(dayColor);
    }

    public void setYearColor(int yearColor) {
        mYearTextView.setTextColor(yearColor);
    }

    public void setDayPadding(int dayPadding) {
        mDayTextView.setPadding(dayPadding, dayPadding, dayPadding, dayPadding);
    }

    public void setDayPaddingTop(int dayPaddingTop) {
        mDayTextView.setPadding(
                mDayTextView.getPaddingLeft(), dayPaddingTop,
                mDayTextView.getPaddingRight(), mDayTextView.getPaddingBottom()
        );
    }

    public void setDayPaddingBottom(int dayPaddingBottom) {
        mDayTextView.setPadding(
                mDayTextView.getPaddingLeft(), mDayTextView.getPaddingTop(),
                mDayTextView.getPaddingRight(), dayPaddingBottom
        );
    }

    public void setDayPaddingLeft(int dayPaddingLeft) {
        mDayTextView.setPadding(
                dayPaddingLeft, mDayTextView.getPaddingTop(),
                mDayTextView.getPaddingRight(), mDayTextView.getPaddingBottom()
        );
    }

    public void setDayPaddingRight(int dayPaddingRight) {
        mDayTextView.setPadding(
                mDayTextView.getPaddingLeft(), mDayTextView.getPaddingTop(),
                dayPaddingRight, mDayTextView.getPaddingBottom()
        );
    }

    public void setYearPadding(int yearPadding) {
        mYearTextView.setPadding(yearPadding, yearPadding, yearPadding, yearPadding);
    }

    public void setYearPaddingTop(int yearPaddingTop) {
        mYearTextView.setPadding(
                mYearTextView.getPaddingLeft(), yearPaddingTop,
                mYearTextView.getPaddingRight(), mYearTextView.getPaddingBottom()
        );
    }

    public void setYearPaddingBottom(int yearPaddingBottom) {
        mYearTextView.setPadding(
                mYearTextView.getPaddingLeft(), mYearTextView.getPaddingTop(),
                mYearTextView.getPaddingRight(), yearPaddingBottom
        );
    }

    public void setYearPaddingLeft(int yearPaddingLeft) {
        mYearTextView.setPadding(
                yearPaddingLeft, mYearTextView.getPaddingTop(),
                mYearTextView.getPaddingRight(), mYearTextView.getPaddingBottom()
        );
    }

    public void setYearPaddingRight(int yearPaddingRight) {
        mYearTextView.setPadding(
                mYearTextView.getPaddingLeft(), mYearTextView.getPaddingTop(),
                yearPaddingRight, mYearTextView.getPaddingBottom()
        );
    }

    public float getDayTextSize() {
        return mDayTextView.getTextSize();
    }

    public float getYearTextSize() {
        return mYearTextView.getTextSize();
    }

    public int getDayColor() {
        return mDayTextView.getCurrentTextColor();
    }

    public int getYearColor() {
        return mYearTextView.getCurrentTextColor();
    }

    public int getDayPaddingTop() {
        return mDayTextView.getPaddingTop();
    }

    public int getDayPaddingBottom() {
        return mDayTextView.getPaddingBottom();
    }

    public int getDayPaddingLeft() {
        return mDayTextView.getPaddingLeft();
    }

    public int getDayPaddingRight() {
        return mDayTextView.getPaddingRight();
    }

    public int getYearPaddingTop() {
        return mYearTextView.getPaddingTop();
    }

    public int getYearPaddingBottom() {
        return mYearTextView.getPaddingBottom();
    }

    public int getYearPaddingLeft() {
        return mYearTextView.getPaddingLeft();
    }

    public int getYearPaddingRight() {
        return mYearTextView.getPaddingRight();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mDayTextView = findViewById(R.id.date_box_day);
        mDayLayout = findViewById(R.id.layout_day);

        mYearTextView = findViewById(R.id.date_box_year);
        mYearLayout = findViewById(R.id.layout_year);

        mDayTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDayTextSize);
        mYearTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mYearTextSize);

        mDayTextView.setTextColor(mDayColor);
        mYearTextView.setTextColor(mYearColor);

        setDayBg(mDayBg);
        setYearBg(mYearBg);
        setDayBgColor(mDayBgColor);
        setYearBgColor(mYearBgColor);

        mDayTextView.setPadding(mDayPaddingLeft, mDayPaddingTop, mDayPaddingRight, mDayPaddingBottom);
        if (mDayPadding != 0)
            mDayTextView.setPadding(mDayPadding, mDayPadding, mDayPadding, mDayPadding);

        mYearTextView.setPadding(mYearPaddingLeft, mYearPaddingTop, mYearPaddingRight, mYearPaddingBottom);
        if (mYearPadding != 0)
            mYearTextView.setPadding(mYearPadding, mYearPadding, mYearPadding, mYearPadding);

        setDate(new Date(System.currentTimeMillis()));
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            this.setOrientation(LinearLayout.VERTICAL);
            inflater.inflate(R.layout.datebox_layout, this, true);
        }

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DateBoxView, 0, 0);

            mDayBgColor = a.getColor(R.styleable.DateBoxView_dayBackgroundColor, DEF_DAY_BG_COLOR);
            mYearBgColor = a.getColor(R.styleable.DateBoxView_yearBackgroundColor, DEF_YEAR_BG_COLOR);

            mDayBg = a.getResourceId(R.styleable.DateBoxView_dayBackground, R.drawable.date_box_day);
            mYearBg = a.getResourceId(R.styleable.DateBoxView_yearBackground, R.drawable.date_box_year);

            mDayColor = a.getColor(R.styleable.DateBoxView_dayTextColor, DEF_DAY_TEXT_COLOR);
            mYearColor = a.getColor(R.styleable.DateBoxView_yearTextColor, DEF_YEAR_TEXT_COLOR);

            mDayTextSize = a.getDimensionPixelSize(R.styleable.DateBoxView_dayTextSize, DEFAULT_DAY_TEXT_SIZE);
            mYearTextSize = a.getDimensionPixelSize(R.styleable.DateBoxView_yearTextSize, DEFAULT_YEAR_TEXT_SIZE);

            mDayPaddingTop = a.getDimensionPixelSize(R.styleable.DateBoxView_dayPaddingTop, 0);
            mDayPaddingBottom = a.getDimensionPixelSize(R.styleable.DateBoxView_dayPaddingBottom, 0);
            mDayPaddingLeft = a.getDimensionPixelSize(R.styleable.DateBoxView_dayPaddingLeft, 0);
            mDayPaddingRight = a.getDimensionPixelSize(R.styleable.DateBoxView_dayPaddingBottom, 0);

            mYearPaddingTop = a.getDimensionPixelSize(R.styleable.DateBoxView_yearPaddingTop, 0);
            mYearPaddingBottom = a.getDimensionPixelSize(R.styleable.DateBoxView_yearPaddingBottom, 0);
            mYearPaddingLeft = a.getDimensionPixelSize(R.styleable.DateBoxView_yearPaddingLeft, 0);
            mYearPaddingRight = a.getDimensionPixelSize(R.styleable.DateBoxView_yearPaddingBottom, 0);

            mDayPadding = a.getDimensionPixelSize(R.styleable.DateBoxView_dayPadding, 0);
            mYearPadding = a.getDimensionPixelSize(R.styleable.DateBoxView_yearPadding, 0);

            a.recycle();
        }
        else {
            mDayBgColor = DEF_DAY_BG_COLOR;
            mYearBgColor = DEF_YEAR_BG_COLOR;

            mDayColor = DEF_DAY_TEXT_COLOR;
            mYearColor = DEF_YEAR_TEXT_COLOR;

            mDayTextSize = DEFAULT_DAY_TEXT_SIZE;
            mYearTextSize = DEFAULT_YEAR_TEXT_SIZE;
        }
    }
}
