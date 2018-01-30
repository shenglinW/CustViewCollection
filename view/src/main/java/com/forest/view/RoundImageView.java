package com.forest.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by forest on 3/18/17.
 */

public class RoundImageView extends ImageView {
    private static final PorterDuffXfermode duffMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

    private int borderColor = 0x44000000;
    private Paint borderPaint;
    private Path boundPath;
    private Path borderPath;
    private RectF rect = new RectF();
    private float borderWidth = 1f;
    private float radius = 10;

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public RoundImageView(Context context) {
        super(context);
        setup(null);
    }

    private void setup(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RoundImageView);
            borderColor = typedArray.getColor(R.styleable.RoundImageView_borderColor, borderColor);
            borderWidth = typedArray.getDimension(R.styleable.RoundImageView_borderWidth, borderWidth);
            radius = typedArray.getDimension(R.styleable.RoundImageView_borderRadius, radius);
            typedArray.recycle();
        }

        borderPath = new Path();
        boundPath = new Path();
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStrokeWidth(borderWidth);

    }

    @Override
    protected void onDetachedFromWindow() {
        setImageDrawable(null);
        super.onDetachedFromWindow();
    }

    protected void drawBorder(Canvas canvas) {
        borderPaint.setStyle(Style.STROKE);
        borderPaint.setColor(borderColor);
        canvas.drawPath(borderPath, borderPaint);
    }

    public void buildBoundPath(Path boundPath) {
        boundPath.reset();

        final int width = getWidth();
        final int height = getHeight();
        radius = Math.min(radius, Math.min(width, height) * 0.5f);

        RectF rect = new RectF(0, 0, width, height);
        boundPath.addRoundRect(rect, radius, radius, Direction.CW);
    }

    public void buildBorderPath(Path borderPath) {
        borderPath.reset();

        final float halfBorderWidth = borderWidth * 0.5f;

        final int width = getWidth();
        final int height = getHeight();
        radius = Math.min(radius, Math.min(width, height) * 0.5f);

        RectF rect = new RectF(halfBorderWidth, halfBorderWidth,
                width - halfBorderWidth, height - halfBorderWidth);
        borderPath.addRoundRect(rect, radius, radius, Direction.CW);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            buildBoundPath(boundPath);
            buildBorderPath(borderPath);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable maiDrawable = getDrawable();
        if (!isInEditMode() && maiDrawable instanceof BitmapDrawable) {
            Paint paint = ((BitmapDrawable) maiDrawable).getPaint();

            Rect bitmapBounds = maiDrawable.getBounds();
            rect.set(bitmapBounds);

            int saveCount = canvas.saveLayer(rect, null,
                    Canvas.MATRIX_SAVE_FLAG |
                            Canvas.CLIP_SAVE_FLAG |
                            Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                            Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                            Canvas.CLIP_TO_LAYER_SAVE_FLAG);
            getImageMatrix().mapRect(rect);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            final int color = 0xffffffff;
            paint.setColor(color);
            canvas.drawPath(boundPath, paint);

            Xfermode oldMode = paint.getXfermode();
            paint.setXfermode(duffMode);
            super.onDraw(canvas);
            paint.setXfermode(oldMode);
            canvas.restoreToCount(saveCount);

            drawBorder(canvas);
        } else {
            super.onDraw(canvas);
        }
    }
}
