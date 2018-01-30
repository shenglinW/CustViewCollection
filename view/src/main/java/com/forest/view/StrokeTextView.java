package com.forest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by forest on 3/15/17.
 */

public class StrokeTextView extends TextView {
    private TextView outlineTextView = null;

    public StrokeTextView(Context context) {
        this(context, null);
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StrokeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        outlineTextView = new TextView(context, attrs, defStyle);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        TextPaint paint = outlineTextView.getPaint();
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.StrokeTextView);

        int color = mTypedArray.getColor(R.styleable.StrokeTextView_strokeColor, Color.WHITE);
        float strokeWidth = mTypedArray.getDimension(R.styleable.StrokeTextView_strokeWidth, 1);
        mTypedArray.recycle();

        paint.setStrokeWidth(strokeWidth);// 描边宽度
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        outlineTextView.setTextColor(color);// 描边颜色
        outlineTextView.setGravity(getGravity());
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        outlineTextView.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 设置轮廓文字
        CharSequence outlineText = outlineTextView.getText();
        if (outlineText == null || !outlineText.equals(this.getText())) {
            outlineTextView.setText(getText());
            postInvalidate();
        }
        outlineTextView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        outlineTextView.layout(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(TextUtils.isEmpty(outlineTextView.getText()) || !outlineTextView.getText().equals(getText())){
            outlineTextView.setText(getText());
        }

        outlineTextView.draw(canvas);
        super.onDraw(canvas);
    }
}
