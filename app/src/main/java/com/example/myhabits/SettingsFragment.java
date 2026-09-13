package com.example.myhabits;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        ThemeColors colors = new ThemeColors(requireContext());

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(32), dp(24), dp(24));
        root.setBackgroundColor(colors.background);

        TextView title = new TextView(requireContext());
        title.setText("Configurações");
        title.setTextColor(colors.textPrimary);
        title.setTextSize(28);
        title.setTypeface(null, Typeface.BOLD);
        root.addView(title, new LinearLayout.LayoutParams(-1, -2));

        TextView subtitle = new TextView(requireContext());
        subtitle.setText("Personalize a aparência do aplicativo");
        subtitle.setTextColor(colors.textSecondary);
        subtitle.setTextSize(15);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(-1, -2);
        subtitleParams.setMargins(0, dp(4), 0, dp(28));
        root.addView(subtitle, subtitleParams);

        LinearLayout settingCard = new LinearLayout(requireContext());
        settingCard.setOrientation(LinearLayout.HORIZONTAL);
        settingCard.setGravity(Gravity.CENTER_VERTICAL);
        settingCard.setPadding(dp(20), dp(16), dp(16), dp(16));
        settingCard.setBackground(roundDrawable(colors.surface, dp(20)));

        LinearLayout textContainer = new LinearLayout(requireContext());
        textContainer.setOrientation(LinearLayout.VERTICAL);

        TextView settingTitle = new TextView(requireContext());
        settingTitle.setText("Modo escuro");
        settingTitle.setTextSize(17);
        settingTitle.setTextColor(colors.textPrimary);
        settingTitle.setTypeface(null, Typeface.BOLD);

        TextView description = new TextView(requireContext());
        description.setText("Use cores escuras para reduzir o brilho da tela");
        description.setTextSize(14);
        description.setTextColor(colors.textSecondary);

        LinearLayout.LayoutParams descriptionParams = new LinearLayout.LayoutParams(-1, -2);
        descriptionParams.setMargins(0, dp(4), 0, 0);

        textContainer.addView(settingTitle);
        textContainer.addView(description, descriptionParams);

        Switch darkModeSwitch = new Switch(requireContext());
        darkModeSwitch.setChecked(colors.isDark);

        settingCard.addView(textContainer, new LinearLayout.LayoutParams(0, -2, 1));
        settingCard.addView(darkModeSwitch, new LinearLayout.LayoutParams(-2, -2));
        root.addView(settingCard, new LinearLayout.LayoutParams(-1, -2));

        darkModeSwitch.setOnCheckedChangeListener((button, enabled) -> {
            SharedPreferences preferences = requireContext().getSharedPreferences(
                    ThemeColors.PREFS_NAME,
                    requireContext().MODE_PRIVATE
            );

            preferences.edit()
                    .putBoolean(ThemeColors.DARK_MODE_KEY, enabled)
                    .apply();

            AppCompatDelegate.setDefaultNightMode(
                    enabled
                            ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        return root;
    }

    private int dp(int value) {
        return (int) (value * requireContext().getResources()
                .getDisplayMetrics().density);
    }

    private GradientDrawable roundDrawable(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }
}
