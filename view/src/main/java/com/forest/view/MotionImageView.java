package com.forest.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by forest on 11/23/16.
 */
public class MotionImageView extends ImageView {

    private static final String TAG = MotionImageView.class.getSimpleName();

    /**
     * If the x and y axis' intensities are scaled to the image's aspect ratio (true) or
     * equal to the smaller of the axis' intensities (false). If true, the image will be able to
     * translate up to it's view bounds, independent of aspect ratio. If not true,
     * the image will limit it's translation equally so that motion in either axis results
     * in proportional translation.
     */
    private boolean mScaledIntensities = false;

    /**
     * The intensity of the motion effect, giving the perspective of depth.
     */
    private float mMotionIntensity = 1.2f;

    /**
     * The maximum percentage of offset translation that the image can move for each
     * sensor input. Set to a negative number to disable.
     */
    private float mMaximumJump = .2f;

    // Instance variables used during matrix manipulation.
    private Matrix mTranslationMatrix;
    private float mXTranslation;
    private float mYTranslation;
    private float mXOffset;
    private float mYOffset;

    public MotionImageView(Context context) {
        this(context, null);
    }

    public MotionImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MotionImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Instantiate future objects
        mTranslationMatrix = new Matrix();

        // Sets scale type
        setScaleType(ScaleType.MATRIX);

        // Set available attributes
        if (attrs != null) {
            final TypedArray customAttrs = context.obtainStyledAttributes(attrs, R.styleable.MotionImageView);

            if (customAttrs != null) {
                if (customAttrs.hasValue(R.styleable.MotionImageView_motionIntensity)) {
                    setMotionIntensity(customAttrs.getFloat(R.styleable.MotionImageView_motionIntensity, mMotionIntensity));
                }

                if (customAttrs.hasValue(R.styleable.MotionImageView_motionScaledIntensity)) {
                    setScaledIntensities(customAttrs.getBoolean(R.styleable.MotionImageView_motionScaledIntensity, mScaledIntensities));
                }

                customAttrs.recycle();
            }
        }

        // Configure matrix as early as possible by posting to MessageQueue
        post(new Runnable() {
            @Override
            public void run() {
                configureMatrix();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        configureMatrix();
    }

    /**
     * Sets the intensity of the parallax effect. The stronger the effect, the more distance
     * the image will have to move around.
     *
     * @param intensity the new intensity
     */
    public void setMotionIntensity(float intensity) {
        if (intensity < 1) {
            throw new IllegalArgumentException("Motion effect must have a intensity of 1.0 or greater");
        }

        mMotionIntensity = intensity;
        configureMatrix();
    }


    /**
     * Sets whether translation should be limited to the image's bounds or should be limited
     * to the smaller of the two axis' translation limits.
     *
     * @param scaledIntensities the scaledIntensities flag
     */
    public void setScaledIntensities(boolean scaledIntensities) {
        mScaledIntensities = scaledIntensities;
    }

    /**
     * Sets the maximum percentage of the image that image matrix is allowed to translate
     * for each sensor reading.
     *
     * @param maximumJump the new maximum jump
     */
    public void setMaximumJump(float maximumJump) {
        mMaximumJump = maximumJump;
    }

    /**
     * Sets the image view's translation coordinates. These values must be between -1 and 1,
     * representing the transaction percentage from the center.
     *
     * @param x the horizontal translation
     * @param y the vertical translation
     */
    public void setTranslate(float x, float y) {
        if (Math.abs(x) > 1 || Math.abs(y) > 1) {
            throw new IllegalArgumentException("Parallax effect cannot translate more than 100% of its off-screen size");
        }

        float xScale, yScale;

        if (mScaledIntensities) {
            // Set both scales to their offset values
            xScale = mXOffset;
            yScale = mYOffset;
        } else {
            // Set both scales to the max offset (should be negative, so smaller absolute value)
            xScale = Math.max(mXOffset, mYOffset);
            yScale = Math.max(mXOffset, mYOffset);
        }

        // Make sure below maximum jump limit
        if (mMaximumJump > .0f) {
            // Limit x jump
            if (x - mXTranslation / xScale > mMaximumJump) {
                x = mXTranslation / xScale + mMaximumJump;
            } else if (x - mXTranslation / xScale < -mMaximumJump) {
                x = mXTranslation / xScale - mMaximumJump;
            }

            // Limit y jump
            if (y - mYTranslation / yScale > mMaximumJump) {
                y = mYTranslation / yScale + mMaximumJump;
            } else if (y - mYTranslation / yScale < -mMaximumJump) {
                y = mYTranslation / yScale - mMaximumJump;
            }
        }

        mXTranslation = x * xScale;
        mYTranslation = y * yScale;

        configureMatrix();
    }

    /**
     * Configures the ImageView's imageMatrix to allow for movement of the
     * source image.
     */
    private void configureMatrix() {
        if (getDrawable() == null || getWidth() == 0 || getHeight() == 0) return;

        int dWidth = getDrawable().getIntrinsicWidth();
        int dHeight = getDrawable().getIntrinsicHeight();
        int vWidth = getWidth();
        int vHeight = getHeight();

        float scale;
        float dx, dy;

        if (dWidth * vHeight > vWidth * dHeight) {
            scale = (float) vHeight / (float) dHeight;
            mXOffset = (vWidth - dWidth * scale * mMotionIntensity) * 0.5f;
            mYOffset = (vHeight - dHeight * scale * mMotionIntensity) * 0.5f;
        } else {
            scale = (float) vWidth / (float) dWidth;
            mXOffset = (vWidth - dWidth * scale * mMotionIntensity) * 0.5f;
            mYOffset = (vHeight - dHeight * scale * mMotionIntensity) * 0.5f;
        }

        dx = mXOffset + mXTranslation;
        dy = mYOffset + mYTranslation;

        mTranslationMatrix.set(getImageMatrix());
        mTranslationMatrix.setScale(mMotionIntensity * scale, mMotionIntensity * scale);
        mTranslationMatrix.postTranslate(dx, dy);
        setImageMatrix(mTranslationMatrix);
    }

}
