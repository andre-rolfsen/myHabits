package com.example.myhabits;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

public class ThemeColors {

    public static final String PREFS_NAME = "myhabits_preferences";
    public static final String DARK_MODE_KEY = "dark_mode";

    public final boolean isDark;
    public final int background;
    public final int surface;
    public final int textPrimary;
    public final int textSecondary;
    public final int border;
    public final int primary;
    public final int goal;
    public final int limit;
    public final int inputBackground;

    public ThemeColors(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
        );

        isDark = preferences.getBoolean(DARK_MODE_KEY, false);

        primary = Color.rgb(100, 115, 255);
        goal = Color.rgb(67, 198, 140);
        limit = Color.rgb(245, 92, 108);

        if (isDark) {
            background = Color.rgb(20, 21, 26);
            surface = Color.rgb(42, 44, 53);
            textPrimary = Color.rgb(245, 245, 247);
            textSecondary = Color.rgb(190, 192, 202);
            border = Color.rgb(75, 78, 91);
            inputBackground = Color.rgb(53, 55, 66);
        } else {
            background = Color.rgb(247, 248, 252);
            surface = Color.WHITE;
            textPrimary = Color.rgb(32, 35, 45);
            textSecondary = Color.rgb(88, 90, 101);
            border = Color.rgb(225, 227, 235);
            inputBackground = Color.WHITE;
        }
    }
}
