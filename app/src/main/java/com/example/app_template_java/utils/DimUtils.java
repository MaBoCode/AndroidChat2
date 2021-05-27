package com.example.app_template_java.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowInsets;

public class DimUtils {

    public static float dp2px(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public static int getWindowHeight(Activity context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // Allow Fullscreen BottomSheet to expand beyond system windows and draw under status bar.
        int height = displayMetrics.heightPixels;
        WindowInsets windowInsets = view.getRootWindowInsets();
        if (windowInsets != null) {
            height += windowInsets.getSystemWindowInsetTop();
            height += windowInsets.getSystemWindowInsetBottom();
        }
        return height;
    }

}
