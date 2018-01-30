
package com.forest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

public class ArcProgress extends View {

    private final static int DEFAULT_MAX = 527;
    private final static int DEFAULT_MIN = 50;

    public static final int TEMP_UNIT_F = 0;
    public static final int TEMP_UNIT_C = 1;

    public static final int STATE_NORMAL = 1;
    public static final int STATE_EDIT = 2;
    private static final String TAG = ArcProgress.class.getName();
    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_STROKE_WIDTH = "stroke_width";
    private static final String INSTANCE_SUFFIX_TEXT_SIZE = "suffix_text_size";
    private static final String INSTANCE_SUFFIX_TEXT_PADDING = "suffix_text_padding";
    private static final String INSTANCE_BOTTOM_TEXT_SIZE = "bottom_text_size";
    private static final String INSTANCE_BOTTOM_TEXT = "bottom_text";
    private static final String INSTANCE_TEXT_SIZE = "text_size";
    private static final String INSTANCE_TEXT_COLOR = "text_color";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_MAX = "max";
    private static final String INSTANCE_FINISHED_STROKE_COLOR = "finished_stroke_color";
    private static final String INSTANCE_UNFINISHED_STROKE_COLOR = "unfinished_stroke_color";
    private static final String INSTANCE_ARC_ANGLE = "arc_angle";
    private static final String INSTANCE_SUFFIX = "suffix";
    private static final String INSTANCE_ICON = "icon";
    private final int default_finished_color = Color.WHITE;
    private final int default_unfinished_color = Color.rgb(72, 106, 176);
    private final int default_text_color = Color.rgb(66, 145, 241);
    private final Typeface default_font;
    private final float default_suffix_text_size;
    private final float default_suffix_padding;
    private final float default_bottom_text_size;
    private final float default_stroke_width;
    private final String default_suffix_text;
    private final int default_max = 100;
    private final float default_arc_angle = 360 * 0.85f;
    private final int min_size;
    protected Paint textPaint;
    protected Paint mThinTextPaint;
    private Paint paint;
    private Paint mLinePaint;
    private RectF rectF = new RectF();
    private int centerIcon;
    private Bitmap centerBitmap;
    private float strokeWidth;
    private float suffixTextSize;
    private float bottomTextSize;
    private float mLabelTextSize;
    private String bottomText;
    private float textSize;
    private int textColor;
    private int progress = 0;

    private int mTargetProgress = 0;

    private int max;
    private int finishedStrokeColor;
    private int unfinishedStrokeColor;
    private int mTargetStrokeColor;
    private float arcAngle;
    private String suffixText = "%";
    private float suffixTextPadding;
    private float arcBottomHeight;
    private float arcBottomEnd;
    private float arcBottomStart;
    private float default_text_size;
    private float mTemperaturePrimaryTextSize;
    private float mTemperatureSecondaryTextSize;
    private float mHeatStateBottom;
    private float mHeatStateTextSize;
    private float mCurrentTemperature = 200;
    private float mTargetTemperature = 250;
    private int mState = STATE_NORMAL;

    protected void initPainters() {
        textPaint = new TextPaint();
        textPaint.setColor(textColor);
        textPaint.setTypeface(default_font);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);

        paint = new Paint();
        paint.setColor(default_unfinished_color);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeCap(Paint.Cap.ROUND);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(Color.parseColor("#3699d2"));
        mLinePaint.setStrokeWidth(Utils.dp2px(getContext(), 2));
        mLinePaint.setStyle(Paint.Style.STROKE);

        mThinTextPaint = new TextPaint();
        mThinTextPaint.setTextSize(textSize);
        mThinTextPaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        mThinTextPaint.setColor(textColor);
        mThinTextPaint.setTextSize(textSize);
        mThinTextPaint.setAntiAlias(true);
    }

    public ArcProgress(Context context) {
        this(context, null);
    }

    @Override
    public void invalidate() {
        initPainters();
        super.invalidate();
    }

    public ArcProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public ArcProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        default_font = Typeface.create("sans-serif-medium", Typeface.NORMAL);
        default_text_size = Utils.sp2px(context, 18);
        min_size = (int) Utils.dp2px(context, 100);
        default_text_size = Utils.sp2px(context, 40);
        default_suffix_text_size = Utils.sp2px(context, 15);
        default_suffix_padding = Utils.dp2px(context, 4);
        default_suffix_text = "%";
        default_bottom_text_size = Utils.sp2px(context, 10);
        default_stroke_width = Utils.dp2px(context, 4);

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcProgress, defStyleAttr, 0);
        initByAttributes(attributes);
        attributes.recycle();

        initPainters();

        mLabelTextSize = Utils.sp2px(context, 12);
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        this.invalidate();
    }

    protected void initByAttributes(TypedArray attributes) {
        finishedStrokeColor = attributes.getColor(R.styleable.ArcProgress_arc_finished_color, default_finished_color);
        unfinishedStrokeColor = attributes.getColor(R.styleable.ArcProgress_arc_unfinished_color, default_unfinished_color);

        mTargetStrokeColor = Color.parseColor("#808080");

        textColor = attributes.getColor(R.styleable.ArcProgress_arc_text_color, default_text_color);
        textSize = attributes.getDimension(R.styleable.ArcProgress_arc_text_size, default_text_size);
        arcAngle = attributes.getFloat(R.styleable.ArcProgress_arc_angle, default_arc_angle);
        setMax(attributes.getInt(R.styleable.ArcProgress_arc_max, default_max));
        setProgress(attributes.getInt(R.styleable.ArcProgress_arc_progress, 1));
        strokeWidth = attributes.getDimension(R.styleable.ArcProgress_arc_stroke_width, default_stroke_width);
        suffixTextSize = attributes.getDimension(R.styleable.ArcProgress_arc_suffix_text_size, default_suffix_text_size);
        suffixText = TextUtils.isEmpty(attributes.getString(R.styleable.ArcProgress_arc_suffix_text)) ? default_suffix_text : attributes
                .getString(R.styleable.ArcProgress_arc_suffix_text);
        suffixTextPadding = attributes.getDimension(R.styleable.ArcProgress_arc_suffix_text_padding, default_suffix_padding);
        bottomTextSize = attributes.getDimension(R.styleable.ArcProgress_arc_bottom_text_size, default_bottom_text_size);
        bottomText = attributes.getString(R.styleable.ArcProgress_arc_bottom_text);
        int resId = attributes.getResourceId(R.styleable.ArcProgress_arc_center_src, 0);
        setIcon(resId);

        mTemperaturePrimaryTextSize = attributes.getDimension(R.styleable.ArcProgress_arc_temperature_primary_text_size, default_bottom_text_size);
        mTemperatureSecondaryTextSize = attributes.getDimension(R.styleable.ArcProgress_arc_temperature_secondary_text_size, default_bottom_text_size);

        mHeatStateBottom = attributes.getDimension(R.styleable.ArcProgress_arc_heat_state_bottom, default_bottom_text_size);
        mHeatStateTextSize = attributes.getDimension(R.styleable.ArcProgress_arc_heat_state_text_size, default_bottom_text_size);
    }

    public float getSuffixTextSize() {
        return suffixTextSize;
    }

    public Bitmap getBitmap() {
        return centerBitmap;
    }

    public void setSuffixTextSize(float suffixTextSize) {
        this.suffixTextSize = suffixTextSize;
        this.invalidate();
    }

    public Bitmap setBitmap(Bitmap bitmap) {
        Bitmap bmpOld = this.centerBitmap;
        this.centerBitmap = bitmap;
        if (bmpOld != centerBitmap) {
            invalidate();
        }
        return bmpOld;
    }

    public String getBottomText() {
        return bottomText;
    }

    public int getState() {
        return mState;
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
        this.invalidate();
    }

    public void setState(int mState) {
        this.mState = mState;
        this.invalidate();
    }

    public int getIcon() {
        return centerIcon;
    }

    static Bitmap decodeBitmap(Context context, int resId) {
        Drawable drawable = ContextCompat.getDrawable(context, resId);
        if (null != drawable && drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        return null;
    }

    public void setIcon(int resId) {
        if (this.centerIcon != resId) {
            if (resId > 0) {
                setBitmap(decodeBitmap(getContext(), resId));
            } else {
                setBitmap(null);
            }
            this.centerIcon = resId;
        }
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (this.progress > getMax()) {
            this.progress = getMax();
        } else if (this.progress <= 0) {
            this.progress = 1;
        }
        invalidate();
    }

    public void setTargetProgress(int progress) {
        this.mTargetProgress = progress;
        if (this.mTargetProgress > getMax()) {
            this.mTargetProgress = getMax();
        } else if (this.mTargetProgress <= 0) {
            this.mTargetProgress = 1;
        }
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        if (max > 0) {
            this.max = max;
            invalidate();
        }
    }

    public float getBottomTextSize() {
        return bottomTextSize;
    }

    public void setBottomTextSize(float bottomTextSize) {
        this.bottomTextSize = bottomTextSize;
        this.invalidate();
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        this.invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        this.invalidate();
    }

    public int getFinishedStrokeColor() {
        return finishedStrokeColor;
    }

    public void setFinishedStrokeColor(int finishedStrokeColor) {
        this.finishedStrokeColor = finishedStrokeColor;
        this.invalidate();
    }

    public int getUnfinishedStrokeColor() {
        return unfinishedStrokeColor;
    }

    public void setUnfinishedStrokeColor(int unfinishedStrokeColor) {
        this.unfinishedStrokeColor = unfinishedStrokeColor;
        this.invalidate();
    }

    public float getArcAngle() {
        return arcAngle;
    }

    public void setArcAngle(float arcAngle) {
        this.arcAngle = arcAngle;
        this.invalidate();
    }

    public String getSuffixText() {
        return suffixText;
    }

    public void setSuffixText(String suffixText) {
        this.suffixText = suffixText;
        this.invalidate();
    }

    public float getSuffixTextPadding() {
        return suffixTextPadding;
    }

    public void setSuffixTextPadding(float suffixTextPadding) {
        this.suffixTextPadding = suffixTextPadding;
        this.invalidate();
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return min_size;
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return min_size;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        rectF.set(strokeWidth / 2f, strokeWidth / 2f, width - strokeWidth / 2f, MeasureSpec.getSize(heightMeasureSpec) - strokeWidth / 2f);
        float radius = width / 2f;
        float angle = (360 - arcAngle) / 2f;
        arcBottomHeight = radius * (float) (1 - Math.cos(angle / 180 * Math.PI));
    }

    private void drawRightLabelText(Canvas canvas) {
        textPaint.setColor(Color.parseColor("#000000"));
        textPaint.setTextSize(mLabelTextSize);

        float textHeight = textPaint.descent() + textPaint.ascent();
        float baseline = getHeight() - arcBottomHeight - textHeight / 2;
        float offset = Utils.dp2px(getContext(), 2);

        String text = "Max";

        float angle = (360 - arcAngle) / 2f;
        float radius = getWidth() / 2f;
        float x = radius * (float) (Math.sin(angle / 180 * Math.PI));
        arcBottomEnd = getWidth() / 2 + x;
        canvas.drawText(text, arcBottomEnd + textPaint.measureText(text) / 2 + offset, baseline, textPaint);
    }

    private void drawLeftLabelText(Canvas canvas) {
        textPaint.setColor(Color.parseColor("#3699d2"));
        textPaint.setTextSize(mLabelTextSize);

        float textHeight = textPaint.descent() + textPaint.ascent();
        float baseline = getHeight() - arcBottomHeight - textHeight / 2;
        float offset = Utils.dp2px(getContext(), 2);

        String text = "Min";

        float angle = (360 - arcAngle) / 2f;
        float radius = getWidth() / 2f;
        float x = radius * (float) (Math.sin(angle / 180 * Math.PI));
        arcBottomStart = getWidth() / 2 - x;

        canvas.drawText(text, arcBottomStart - textPaint.measureText(text) / 2 * 3, baseline, textPaint);
    }

    public float getCurrentTemperature() {
        return mCurrentTemperature;
    }

    public void setCurrentTemperature(int temp) {
        this.mCurrentTemperature = temp;

        if (this.mCurrentTemperature > DEFAULT_MIN) {
            setProgress((int) (mCurrentTemperature - DEFAULT_MIN) * getMax() / (DEFAULT_MAX - DEFAULT_MIN));
        } else
            setProgress(0);
        this.invalidate();
    }

    public float getTargetTemperature() {
        return mTargetTemperature;
    }

    public void setTargetTemperature(float value) {

        if (value <= DEFAULT_MIN)
            mTargetTemperature = DEFAULT_MIN;
        else if (value >= DEFAULT_MAX)
            mTargetTemperature = DEFAULT_MAX;
        else
            mTargetTemperature = value;

        if (this.mTargetTemperature > DEFAULT_MIN) {
            setTargetProgress((int) (mTargetTemperature - DEFAULT_MIN) * getMax() / (DEFAULT_MAX - DEFAULT_MIN));
        } else
            setTargetProgress(0);

        this.invalidate();
    }

    private int mUnit;

    public void setUnit(int unit) {
        mUnit = unit;
//        mValueStr = Utils.getTemperatureStringByMode(getContext(), mCurrentTemperature, mUnit);
//        mDefaultValueStr = Utils.getTemperatureStringByMode(getContext(), mTargetTemperature, mUnit);
        this.invalidate();
    }

    public int getUnit() {
        return mUnit;
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

    private void drawNormalStateText(Canvas canvas, float xBase, float yBase, String text1, float value) {
        mThinTextPaint.setTextSize(mTemperaturePrimaryTextSize * 1.4f);

        String text = getTemperatureStringByMode(value, mUnit);
        float textHeight = textPaint.descent() + textPaint.ascent();

        float baseline = yBase;
        canvas.drawText(text, xBase - mThinTextPaint.measureText(text) / 2.0f, baseline, mThinTextPaint);

        textPaint.setTextSize(mTemperatureSecondaryTextSize);
        float offset1 = Utils.dp2px(getContext(), 20);
        float offset2 = Utils.dp2px(getContext(), 4);
        textHeight = textPaint.descent() + textPaint.ascent();
        baseline = yBase - textHeight + offset1;
        //draw line1
        canvas.drawText(text1, xBase - textPaint.measureText(text1) / 2.0f, baseline, textPaint);

        //draw line2
        baseline = baseline - textHeight + offset2;
        String text2 = "TEMPERATURE";
        canvas.drawText(text2, xBase - textPaint.measureText(text2) / 2.0f, baseline, textPaint);
    }

    private void drawEditorText(Canvas canvas, float xBase, float yBase, String text1, float value) {
        mThinTextPaint.setTextSize(mTemperaturePrimaryTextSize);

        String text = getTemperatureStringByMode(value, mUnit);
        float textHeight = textPaint.descent() + textPaint.ascent();

        float baseline = yBase;
        canvas.drawText(text, xBase - mThinTextPaint.measureText(text) / 2.0f, baseline, mThinTextPaint);

        textPaint.setTextSize(mTemperatureSecondaryTextSize);
        float offset1 = Utils.dp2px(getContext(), 20);
        float offset2 = Utils.dp2px(getContext(), 4);
        textHeight = textPaint.descent() + textPaint.ascent();
        baseline = yBase - textHeight + offset1;
        //draw line1
        canvas.drawText(text1, xBase - textPaint.measureText(text1) / 2.0f, baseline, textPaint);

        //draw line2
        baseline = baseline - textHeight + offset2;
        String text2 = "TEMPERATURE";
        canvas.drawText(text2, xBase - textPaint.measureText(text2) / 2.0f, baseline, textPaint);
    }

    private void drawHeatState(Canvas canvas) {
        textPaint.setTextSize(mHeatStateTextSize);
        float textHeight = textPaint.descent() + textPaint.ascent();
        String text = "heating...";
        float textWidth = textPaint.measureText(text);

        float bmpW = centerBitmap.getWidth();
        float bmpH = centerBitmap.getHeight();

        float offset = Utils.dp2px(getContext(), 10);
        float totalWidth = bmpW + textWidth + offset;
        float totalHeight = bmpH;//(bmpH > Math.abs(textHeight)) ? bmpH : Math.abs(textHeight);

        float x = (getWidth() - totalWidth) / 2;
        float y = mHeatStateBottom - totalHeight;
        canvas.drawBitmap(centerBitmap, x, y, null);

        float baseline = mHeatStateBottom - totalHeight / 2 - textHeight / 2;
        //draw line1
        canvas.drawText(text, x + bmpW + offset, baseline, textPaint);
    }

    public void setHeating(boolean mHeating) {
        isHeating = mHeating;
        this.invalidate();
    }

    private boolean isHeating;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float startAngle = 270 - arcAngle / 2f;
        float finishedSweepAngle = progress / (float) getMax() * arcAngle;
        float targetSweepAngle = mTargetProgress / (float) getMax() * arcAngle;
        float finishedStartAngle = startAngle;
        paint.setColor(unfinishedStrokeColor);
        canvas.drawArc(rectF, startAngle, arcAngle, false, paint);

        if (mTargetProgress >= progress) {
            paint.setColor(mTargetStrokeColor);
            canvas.drawArc(rectF, finishedStartAngle, targetSweepAngle, false, paint);
            paint.setColor(finishedStrokeColor);
            canvas.drawArc(rectF, finishedStartAngle, finishedSweepAngle, false, paint);
        } else {
            paint.setColor(finishedStrokeColor);
            canvas.drawArc(rectF, finishedStartAngle, finishedSweepAngle, false, paint);
            paint.setColor(mTargetStrokeColor);
            canvas.drawArc(rectF, finishedStartAngle, targetSweepAngle, false, paint);
        }

//        paint.setStrokeWidth(4);
//        canvas.drawLine(mCircleRaidus, mCircleRaidus, mCircleRaidus, getHeight(), paint);

        drawLeftLabelText(canvas);
        drawRightLabelText(canvas);

        if (/*mState == STATE_NORMAL*/false) {
            textPaint.setColor(textColor);
            drawNormalStateText(canvas, getWidth() / 2, getWidth() / 2, "CURRENT", getCurrentTemperature());
        } else {
            canvas.drawLine(getWidth() / 2, getHeight() / 3, getWidth() / 2, getHeight() * 0.75f, mLinePaint);
            textPaint.setColor(Color.parseColor("#6e6f74"));
            drawEditorText(canvas, getWidth() / 4 * 3, getWidth() / 2, "TARGET", getTargetTemperature());
            textPaint.setColor(textColor);
            drawEditorText(canvas, getWidth() / 4, getWidth() / 2, "CURRENT", getCurrentTemperature());
        }

        if (isHeating) {
            textPaint.setColor(textColor);
            drawHeatState(canvas);
        }

        drawOther(canvas);
    }

    private void drawOther(Canvas canvas) {
        if (false) {
            String text = String.valueOf(progress);
            if (!TextUtils.isEmpty(text)) {
                if (null == centerBitmap) {
                    textPaint.setColor(textColor);
                    textPaint.setTextSize(textSize);
                    float textHeight = textPaint.descent() + textPaint.ascent();
                    float textBaseline = (getHeight() - textHeight) / 2.0f;
                    canvas.drawText(text, (getWidth() - textPaint.measureText(text)) / 2.0f, textBaseline, textPaint);
                    textPaint.setTextSize(suffixTextSize);
                    float suffixHeight = textPaint.descent() + textPaint.ascent();
                    canvas.drawText(suffixText,
                            getWidth() / 2.0f + textPaint.measureText(text) + suffixTextPadding, textBaseline + textHeight - suffixHeight,
                            textPaint);
                    bottomText = null;
                } else {
                    float x = (getWidth() - centerBitmap.getWidth()) / 2;
                    float y = (getHeight() - centerBitmap.getHeight()) / 2;
                    canvas.drawBitmap(centerBitmap, x, y, null);
                    bottomText = text + suffixText;
                }
            }

            if (!TextUtils.isEmpty(bottomText)) {
                textPaint.setTextSize(bottomTextSize);
                float bottomTextBaseline = getHeight() - arcBottomHeight - (textPaint.descent() + textPaint.ascent()) / 2;
                canvas.drawText(bottomText, (getWidth() - textPaint.measureText(bottomText)) / 2.0f, bottomTextBaseline, textPaint);
            }
        }
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putFloat(INSTANCE_STROKE_WIDTH, getStrokeWidth());
        bundle.putFloat(INSTANCE_SUFFIX_TEXT_SIZE, getSuffixTextSize());
        bundle.putFloat(INSTANCE_SUFFIX_TEXT_PADDING, getSuffixTextPadding());
        bundle.putFloat(INSTANCE_BOTTOM_TEXT_SIZE, getBottomTextSize());
        bundle.putString(INSTANCE_BOTTOM_TEXT, getBottomText());
        bundle.putFloat(INSTANCE_TEXT_SIZE, getTextSize());
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor());
        bundle.putInt(INSTANCE_PROGRESS, getProgress());
        bundle.putInt(INSTANCE_MAX, getMax());
        bundle.putInt(INSTANCE_FINISHED_STROKE_COLOR, getFinishedStrokeColor());
        bundle.putInt(INSTANCE_UNFINISHED_STROKE_COLOR, getUnfinishedStrokeColor());
        bundle.putFloat(INSTANCE_ARC_ANGLE, getArcAngle());
        bundle.putString(INSTANCE_SUFFIX, getSuffixText());
        bundle.putInt(INSTANCE_ICON, getIcon());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            strokeWidth = bundle.getFloat(INSTANCE_STROKE_WIDTH);
            setIcon(bundle.getInt(INSTANCE_ICON, 0));
            suffixTextSize = bundle.getFloat(INSTANCE_SUFFIX_TEXT_SIZE);
            suffixTextPadding = bundle.getFloat(INSTANCE_SUFFIX_TEXT_PADDING);
            bottomTextSize = bundle.getFloat(INSTANCE_BOTTOM_TEXT_SIZE);
            bottomText = bundle.getString(INSTANCE_BOTTOM_TEXT);
            textSize = bundle.getFloat(INSTANCE_TEXT_SIZE);
            textColor = bundle.getInt(INSTANCE_TEXT_COLOR);
            setMax(bundle.getInt(INSTANCE_MAX));
            setProgress(bundle.getInt(INSTANCE_PROGRESS));
            finishedStrokeColor = bundle.getInt(INSTANCE_FINISHED_STROKE_COLOR);
            unfinishedStrokeColor = bundle.getInt(INSTANCE_UNFINISHED_STROKE_COLOR);
            suffixText = bundle.getString(INSTANCE_SUFFIX);
            initPainters();
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
