package com.forest.view;

import android.content.Context;

import java.lang.reflect.Field;

/**
 * Created by forest on 1/19/18.
 */

public class Utils {

    public static int dp2px(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public static int sp2px(Context context, int spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale);
    }

    public static int getResourceId(String idName) {
        Class idClass = R.id.class;
        try {
            Field field = idClass.getField(idName);
            int resId = field.getInt(idName);
            return resId;
        } catch (NoSuchFieldException e) {
            return 0;
        } catch (IllegalAccessException e) {
            return 0;
        }

    }
}
