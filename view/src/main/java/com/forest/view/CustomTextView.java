package com.forest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * TODO: document your custom view class.
 */
public class CustomTextView extends TextView {
    private static final int[] SELECTED_STATE_SET = new int[]{android.R.attr.state_selected, android.R.attr.state_enabled};
    private static final int[] PRESSED_STATE_SET = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
    private static final int[] DISABLED_STATE_SET = new int[]{-android.R.attr.state_enabled};
    private static final int[] DEFAULT_STATE_SET = new int[]{android.R.attr.state_enabled};

    private StateListDrawable mBgDrawable;

    public CustomTextView(Context context) {
        super(context);
        init(null, 0);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CustomTextView, defStyle, 0);

        mBgDrawable = new StateListDrawable();

        if (a.hasValue(R.styleable.CustomTextView_disableBackground)) {
            mBgDrawable.addState(DISABLED_STATE_SET, a.getDrawable(R.styleable.CustomTextView_disableBackground));
        }
        if (a.hasValue(R.styleable.CustomTextView_pressedBackground)) {
            mBgDrawable.addState(PRESSED_STATE_SET, a.getDrawable(R.styleable.CustomTextView_pressedBackground));
        }
        if (a.hasValue(R.styleable.CustomTextView_selectedBackground)) {
            mBgDrawable.addState(SELECTED_STATE_SET, a.getDrawable(R.styleable.CustomTextView_selectedBackground));
        }
        if (a.hasValue(R.styleable.CustomTextView_normalBackground)) {
            mBgDrawable.addState(DEFAULT_STATE_SET, a.getDrawable(R.styleable.CustomTextView_normalBackground));
            setBackground(mBgDrawable);
        }
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
