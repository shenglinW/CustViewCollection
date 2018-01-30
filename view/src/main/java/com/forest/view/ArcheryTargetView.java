package com.forest.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

import static android.animation.PropertyValuesHolder.ofFloat;

/**
 * Created by forest on 1/21/18.
 */

public class ArcheryTargetView extends View {

    private Paint mCirclePaint;
    private Paint mLinePaint;
    private Paint mPointPaint;
    private TextPaint mTextPaint;

    private float mCenterX, mCenterY;
    private float mMaxRadius;
    private float mCircleStep;
    private List<PointF> mPointFList;
    private PointF mPendingPointF;
    private float mPointScale;
    private ValueAnimator mValueAnimator;

    private static final int mCircleCount = 11;

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLUE = Color.parseColor("#FF2196F3");
    private static final int RED = Color.parseColor("#FFF44336");
    private static final int YELLOW = Color.parseColor("#FFFFEB3B");
    private static final int GREEN = Color.parseColor("#FF4CAF50");

    // for center to edge
    private static final int mColorList[] = new int[]{
            GREEN, YELLOW, YELLOW, RED, RED, BLUE, BLUE, BLACK, BLACK, WHITE, WHITE
    };

    public ArcheryTargetView(Context context) {
        super(context);
        init(null, 0);
    }

    public ArcheryTargetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ArcheryTargetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);

        mPointPaint = new Paint(mCirclePaint);
        mPointPaint.setColor(Color.parseColor("#FF5D4037"));

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(Utils.dp2px(getContext(), 2));
        mLinePaint.setColor(Color.BLACK);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        mPointFList = new ArrayList<>();
        if (true) {
            mPointFList.add(new PointF(0.4f, 0.3f));
            mPointFList.add(new PointF(-0.2f, 0.3f));
            mPointFList.add(new PointF(0.7f, -0.3f));
            mPointFList.add(new PointF(-0.5f, 0.3f));
            mPointFList.add(new PointF(-0.4f, -0.3f));
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC);
        drawCircles(canvas);
        mLinePaint.setColor(Color.BLACK);
        drawCross(canvas, mLinePaint, mCenterX, mCenterY, mCircleStep / 2);
        drawTexts(canvas);
        drawPoints(canvas);
        if (mPendingPointF != null) {
            drawPoint(canvas, mPendingPointF, mPointScale * mCircleStep);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                //  抓CANVAS範圍內的X,Y座標
                float touchX = event.getAxisValue(0);
                float touchY = event.getAxisValue(1);

                float distanceX = touchX - mCenterX;
                float distanceY = touchY - mCenterY;

                addPoint(distanceX / mMaxRadius, distanceY / mMaxRadius);
            }
        }

        return super.onTouchEvent(event);
    }

    public void addPoint(float x, float y) {

        if (mPendingPointF != null) {
            mPointFList.add(mPendingPointF);
            if (mValueAnimator != null)
                mValueAnimator.cancel();
            mValueAnimator = null;
        }

        mPendingPointF = new PointF(x, y);


        PropertyValuesHolder scale = ofFloat("scale", 0.3f, 1.5f, 1.0f);
        mValueAnimator = ValueAnimator.ofPropertyValuesHolder(scale).setDuration(1000);
        mValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPointScale = (float) animation.getAnimatedValue("scale");
                invalidate();
            }
        });
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mPointFList != null) {
                    mPointFList.add(mPendingPointF);
                    mPendingPointF = null;
                    invalidate();
                }
            }
        });
        mValueAnimator.start();
    }

    private void drawPoints(Canvas canvas) {
        for (PointF pointF : mPointFList) {
            drawPoint(canvas, pointF, mCircleStep);
        }
    }

    private void drawPoint(Canvas canvas, PointF pointF, float pointSize) {
        float x = pointF.x * mMaxRadius + mCenterX;
        float y = pointF.y * mMaxRadius + mCenterY;
        float z = (float) Math.sqrt(pointF.x * pointF.x + pointF.y * pointF.y);

        int color = Color.WHITE;
        if (z > 9f / 11f || (z < 3f / 11f && z > 1f / 11f)) {
            color = Color.BLACK;
        }
        mLinePaint.setColor(color);
        mPointPaint.setColor(color);
        canvas.save();
        canvas.translate(x, y);
        canvas.rotate(45);
        drawCross(canvas, mLinePaint, 0, 0, pointSize / 3);
        canvas.restore();
        canvas.drawCircle(x, y, pointSize / 6, mPointPaint);
    }

    private void calculate() {
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();

        mCenterX = w / 2;
        mCenterY = h / 2;
        float maxR = Math.min(mCenterX, mCenterY);

        mCircleStep = (int) maxR / mCircleCount;
        mMaxRadius = mCircleStep * mCircleCount;

        mTextPaint.setTextSize(Utils.sp2px(getContext(),24));

        float textWidth = getTextWidth(mTextPaint, "10");

        float scale = (mCircleStep / 2) / textWidth;
        mTextPaint.setTextSize(Utils.sp2px(getContext(),24) * scale);
    }

    private void drawTexts(Canvas canvas) {
        float halfStep = mCircleStep / 2;
        for (int i = 1; i < mCircleCount; i++) {
            int color = mColorList[i] == BLACK ? WHITE : BLACK;
            String text = String.valueOf(mCircleCount - i);
            // center to right
            drawText(canvas, mCenterX + i * mCircleStep + halfStep, mCenterY, color, text);
            // center to left
            drawText(canvas, mCenterX - i * mCircleStep - halfStep, mCenterY, color, text);
            // center to top
            drawText(canvas, mCenterX, mCenterY - i * mCircleStep - halfStep, color, text);
            // center to bottom
            drawText(canvas, mCenterX, mCenterY + i * mCircleStep + halfStep, color, text);
        }
    }

    private void drawText(Canvas canvas, float centerX, float centerY, int color, String txt) {
        float textW = getTextWidth(mTextPaint, txt);
        float textH = getTextHeight(mTextPaint, txt);
        mTextPaint.setColor(color);
        canvas.drawText(txt, centerX - textW / 2, centerY - textH / 2, mTextPaint);
    }

    private float getTextWidth(Paint paint, String text) {
        return paint.measureText(text);
    }

    private float getTextHeight(Paint paint, String text) {
        return paint.descent() + paint.ascent();
    }

    private void drawCircles(Canvas canvas) {

        int lineWidth = Utils.dp2px(getContext(),1);
        for (int i = mCircleCount - 1; i >= 0; i--) {
            int color = mColorList[i];
            float radius = mCircleStep * (i + 1);
            drawColorCircle(canvas, mCirclePaint, color == BLACK ? WHITE : BLACK, mCenterX, mCenterY, radius);
            drawColorCircle(canvas, mCirclePaint, color, mCenterX, mCenterY, radius - lineWidth);
        }
    }

    private void drawCross(Canvas canvas, Paint paint, float centerX, float centerY, float length) {
        canvas.drawLine(centerX - length, centerY, centerX + length, centerY, paint);
        canvas.drawLine(centerX, centerY - length, centerX, centerY + length, paint);
    }

    private void drawColorCircle(Canvas canvas, Paint paint, int color, float x, float y, float r) {
        paint.setColor(color);
        canvas.drawCircle(x, y, r, mCirclePaint);
    }
}
