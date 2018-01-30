package com.forest.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ForestWang on 2016/3/12.
 */
public class TemperatureCircleView extends View {
    private float mStrokeWidth;

    public static final int TEMP_UNIT_F = 0;
    public static final int TEMP_UNIT_C = 1;

    private final static int DEFAULT_MAX = 500;
    private final static int DEFAULT_MIN = 300;
    private final static int MAX_LINE = 100;

    private float mLine1TextSize;
    private float mLine2TextSize;
    private float mTextSize;
    private final String mTextStr = "default";

    private int mBackgroundColor;
    private int mTextColor1;
    private int mTextColor2;
    private int mCircleColor;
    private int mMaxValue;
    private int mMinValue;
    private int mDefaultValue;
    private float mValue;
    private String mValueStr;
    private String mDefaultValueStr;
    private int mUnit;

    public TemperatureCircleView(Context context) {
        super(context);
        init();
    }

    public TemperatureCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TemperatureCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mMaxValue = DEFAULT_MAX;
        mMinValue = DEFAULT_MIN;
        mDefaultValue = DEFAULT_MAX;
        mTextSize = Utils.sp2px(getContext(), 24);
        mLine1TextSize = Utils.sp2px(getContext(), 24);
        mLine2TextSize = Utils.sp2px(getContext(), 20);
    }

    public void setValueRange(int min, int defaultValue, int max) {
        if (min >= max)
            return;
        mMaxValue = max;
        mMinValue = min;
        mDefaultValue = defaultValue;
        mDefaultValueStr = getTemperatureStringByMode(mDefaultValue, mUnit);
        invalidate();
    }

    public float getValue() {
        return mValue;
    }

    public static float CtoF(float tempC) {
        return (tempC * 1.8f + 32f);
    }

    public static float FtoC(float tempF) {
        return ((tempF - 32f) / 1.8f);
    }

    private static String getTemperatureStringByMode(float tempF, int unit) {
        StringBuilder sb = new StringBuilder();
        if (unit == TEMP_UNIT_C) {
            float tempC = FtoC(tempF);
            sb.append(Math.round(tempC));
            sb.append("°C");
        } else {
            sb.append(Math.round(tempF));
            sb.append("°F");
        }
        return sb.toString();
    }

    public void setValue(float value) {
        if (value <= mMinValue)
            mValue = mMinValue;
        else if (value >= mMaxValue)
            mValue = mMaxValue;
        else
            mValue = value;
        mValueStr = getTemperatureStringByMode(mValue, mUnit);
        mDefaultValueStr = getTemperatureStringByMode(mDefaultValue, mUnit);

        mTextColor1 = Color.parseColor("#F44336");
        mTextColor2 = Color.parseColor("#9E9E9E");
        mCircleColor =Color.parseColor("#9E9E9E");
        mBackgroundColor = Color.parseColor("#00000000");
        this.invalidate();
    }

    public void setUnit(int unit) {
        mUnit = unit;
        mValueStr = getTemperatureStringByMode(mValue, mUnit);
        mDefaultValueStr = getTemperatureStringByMode(mDefaultValue, mUnit);
        this.invalidate();
    }

    public int getUnit() {
        return mUnit;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mSmallRect != null && mSmallRect.contains(event.getX(), event.getY())) {
                setValue(mDefaultValue);
                return true;
            } else if (mTextRect != null && mTextRect.contains(event.getX(), event.getY())) {
                int unit = getUnit();
                unit = (unit == TEMP_UNIT_C) ? TEMP_UNIT_F : TEMP_UNIT_C;
                setUnit(unit);
                return true;
            }
        }

        return super.onTouchEvent(event);
    }

    private float mBigR;
    private float mSmallR;
    private RectF mSmallRect;
    private RectF mTextRect;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mStrokeWidth = Utils.dp2px(getContext(), 3);

        float w = canvas.getWidth();
        float h = canvas.getHeight();
        float size = (Math.min(w, h));
        size -= (mStrokeWidth * 2);
        mSmallR = size / 6;
        mBigR = mSmallR * 2;
        mSmallRect = new RectF();
        mSmallRect.left = w / 2 + mBigR - mSmallR;
        mSmallRect.right = w / 2 + mBigR + mSmallR;
        mSmallRect.top = h / 2 + mBigR - mSmallR;
        mSmallRect.bottom = h / 2 + mBigR + mSmallR;

        drawCircleView(canvas);
        drawCircleView2(canvas);
        drawText(canvas);
        drawTextLine1(canvas);
        drawTextLine2(canvas);

    }

    private int measureTextWidth(float textsize, String text) {
        Paint txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setTextAlign(Paint.Align.LEFT);
        txtPaint.setTextSize(textsize);
        txtPaint.setColor(mTextColor1);
        Rect bounds = new Rect();
        txtPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    private float calculateTextSize(float textsize, String text, float maxWidth) {
        float value = textsize;
        while (true) {
            int w = measureTextWidth(value, text);
            if (w < maxWidth) {
                return value;
            }
            value--;
            if (value <= 0) {
                return textsize;
            }
        }
    }

    private void drawText(Canvas canvas) {
        Paint txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setTextAlign(Paint.Align.LEFT);
        float textSize = calculateTextSize(mTextSize, mValueStr, mBigR * 2 * 0.75f);
        if (textSize != mTextSize) {
            mTextSize = textSize;
        }
        txtPaint.setTextSize(mTextSize);
        txtPaint.setColor(mTextColor1);

        Rect bounds = new Rect();
        txtPaint.getTextBounds(mValueStr, 0, mValueStr.length(), bounds);
        Paint.FontMetricsInt fontMetrics = txtPaint.getFontMetricsInt();
        int mHeight = getMeasuredHeight();
        int mWidth = getMeasuredWidth();
        int width = bounds.width();
        int height = bounds.height();

        int baseline = (mHeight - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(mValueStr, mWidth / 2 - width / 2, baseline, txtPaint);

        mTextRect = new RectF();
        mTextRect.left = (mWidth - width) / 2;
        mTextRect.right = (mWidth + width) / 2;
        mTextRect.top = (mHeight - height) / 2;
        mTextRect.bottom = (mHeight + height) / 2;
    }

    private void drawCircleView(Canvas canvas) {
        canvas.save();
        canvas.drawColor(mBackgroundColor);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(mTextColor1);
        paint.setStrokeWidth(mStrokeWidth);

        canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);

        float y = mBigR;
        int count = MAX_LINE;

        Paint circlePaint = new Paint();
        circlePaint.setColor(mBackgroundColor);
        circlePaint.setStrokeJoin(Paint.Join.ROUND);
        circlePaint.setStrokeCap(Paint.Cap.ROUND);
        circlePaint.setStrokeWidth(1);
        canvas.drawCircle(0, 0, mBigR, circlePaint);

        canvas.rotate(180, 0f, 0f);
        int range = (mMaxValue - mMinValue);
        int value = Math.round(mValue);
        int total = ((value - mMinValue) * MAX_LINE) / range;
        for (int i = 0; i < MAX_LINE; i++) {
            if (i >= total) {
                paint.setColor(mCircleColor);
            }
            canvas.drawLine(0f, y - Utils.dp2px(getContext(), 1) - i * 0.5f, 0, y, paint);
            canvas.rotate((360f / count), 0f, 0f);
        }
        canvas.restore();

    }

    private void drawCircleView2(Canvas canvas) {
        canvas.save();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(mCircleColor);
        paint.setStrokeWidth(mStrokeWidth);
        double angle = Math.acos(5 / (4f * Math.sqrt(2f)));
        float angleF = new Double(angle * 180 / Math.PI).floatValue();
        canvas.drawArc(mSmallRect, (180f + 45f + angleF), (360f - 2 * angleF), false, paint);

        // draw circle
        canvas.restore();
    }


    private void drawTextLine1(Canvas canvas) {
        Paint txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setTextAlign(Paint.Align.LEFT);
        float textSize = calculateTextSize(mLine1TextSize, mDefaultValueStr, mSmallR * 2 * 0.7f);
        if (textSize != mLine1TextSize) {
            mLine1TextSize = textSize;
        }
        txtPaint.setTextSize(mLine1TextSize);
        txtPaint.setColor(mTextColor2);

        int centerX = new Float(mSmallRect.centerX()).intValue();
        int centerY = new Float(mSmallRect.centerY()).intValue();

        Rect bounds = new Rect();
        txtPaint.getTextBounds(mDefaultValueStr, 0, mDefaultValueStr.length(), bounds);
        Paint.FontMetricsInt fontMetrics = txtPaint.getFontMetricsInt();
        int fontHeight = fontMetrics.bottom - fontMetrics.top;
        int baseline = (centerY - fontHeight) - fontMetrics.top;
        baseline += Utils.dp2px(getContext(), 2);
        canvas.drawText(mDefaultValueStr, centerX - bounds.width() / 2, baseline, txtPaint);
    }


    private void drawTextLine2(Canvas canvas) {
        Paint txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setTextAlign(Paint.Align.LEFT);
        float textSize = calculateTextSize(mLine2TextSize, mTextStr, mSmallR * 2 * 0.6f);
        if (textSize != mLine2TextSize) {
            mLine2TextSize = textSize;
        }
        txtPaint.setTextSize(mLine2TextSize);
        txtPaint.setColor(mTextColor2);

        int centerX = new Float(mSmallRect.centerX()).intValue();
        int centerY = new Float(mSmallRect.centerY()).intValue();

        Rect bounds = new Rect();
        txtPaint.getTextBounds(mTextStr, 0, mTextStr.length(), bounds);
        Paint.FontMetricsInt fontMetrics = txtPaint.getFontMetricsInt();
        int fontHeight = fontMetrics.bottom - fontMetrics.top;
        int baseline = (centerY + fontHeight) - fontMetrics.bottom;
        baseline -= Utils.dp2px(getContext(), 2);
        canvas.drawText(mTextStr, centerX - bounds.width() / 2, baseline, txtPaint);
    }
}
