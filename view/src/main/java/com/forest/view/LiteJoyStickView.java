package com.forest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class LiteJoyStickView extends View {

    private float mBgCircleRadius, mStickCircleRadius;
    private int mStickGradientEndColor, mStickGradientStartColor;
    private int mBgGradientEndColor, mBgGradientStartColor;

    private Paint mBgCirclePaint, mStickCirclePaint;
    private Shader mBgCircleShader, mStickCircleShader;

    private Paint mSelectPaint;

    private int mPosX = 0, mPosY = 0;

    public LiteJoyStickView(Context context) {
        super(context);
        init(null, 0);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.LiteJoyStickView, defStyle, 0);

        mBgCircleRadius = a.getDimension(R.styleable.LiteJoyStickView_outerRadius, 100);
        mStickCircleRadius = a.getDimension(R.styleable.LiteJoyStickView_innerRadius, 20);

        mStickGradientStartColor = a.getColor(R.styleable.LiteJoyStickView_stickStartColor, Color.BLACK);
        mStickGradientEndColor = a.getColor(R.styleable.LiteJoyStickView_stickEndColor, Color.BLACK);

        mBgGradientStartColor = a.getColor(R.styleable.LiteJoyStickView_backgroundStartColor, Color.BLACK);
        mBgGradientEndColor = a.getColor(R.styleable.LiteJoyStickView_backgroundEndColor, Color.BLACK);

        a.recycle();

        mBgCirclePaint = new Paint();
        mBgCirclePaint.setStyle(Paint.Style.FILL);
        mBgCirclePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        mStickCirclePaint = new Paint();
        mStickCirclePaint.setStyle(Paint.Style.FILL);
        mStickCirclePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    public LiteJoyStickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public LiteJoyStickView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }


    /**
     * @param x from -1 ~ 1
     * @param y from -1 ~ 1
     */
    public void setPos(float x, float y) {
        float z = (float) Math.sqrt(x * x + y * y);
        float stickX = 0f;
        float sticky = 0f;
        if (z >= 1f) {
            stickX = x * mBgCircleRadius / z;
            sticky = y * mBgCircleRadius / z;
        } else if (z > 0f) {
            stickX = x * mBgCircleRadius;
            sticky = y * mBgCircleRadius;
        }
        stickX += getWidth() / 2;
        sticky += getHeight() / 2;

        mStickCircleShader = new RadialGradient(
                stickX, sticky, mStickCircleRadius,
                mStickGradientStartColor, mStickGradientEndColor,
                Shader.TileMode.MIRROR);

        mPosX = (int)stickX;
        mPosY = (int)sticky;

        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();
        int centerX = w / 2;
        int centerY = h / 2;

        if(mPosX == 0 && mPosY == 0){
            mPosX = centerX;
            mPosY = centerY;
        }

        if (mBgCircleShader == null) {
            mBgCircleShader = new RadialGradient(
                    centerX, centerY, mBgCircleRadius,
                    mBgGradientStartColor, mBgGradientEndColor,
                    Shader.TileMode.MIRROR);
        }

        mBgCirclePaint.setShader(mBgCircleShader);

        canvas.drawCircle(centerX, centerY, mBgCircleRadius, mBgCirclePaint);

        if (mStickCircleShader == null) {
            mStickCircleShader = new RadialGradient(
                    mPosX, mPosY, mStickCircleRadius,
                    mStickGradientStartColor, mStickGradientEndColor,
                    Shader.TileMode.MIRROR);
        }
        mStickCirclePaint.setShader(mStickCircleShader);

        canvas.drawCircle(mPosX, mPosY, mStickCircleRadius, mStickCirclePaint);
    }

}
