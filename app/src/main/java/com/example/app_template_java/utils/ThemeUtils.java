package com.example.app_template_java.utils;

import android.content.Context;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.color.MaterialColors;

public abstract class ThemeUtils {

    public static void setTheme(String value) {
        if (value.contentEquals(Prefs.PREFS_THEME_LIGHT)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (value.contentEquals(Prefs.PREFS_THEME_DARK)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (value.contentEquals(Prefs.PREFS_THEME_DEFAULT)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    public static int getThemeColor(Context context, int attr) {
        return MaterialColors.getColor(context, attr, Color.GRAY);
    }

}
