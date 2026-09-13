package com.example.myhabits;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final int HOME_ID = 1;
    private static final int STREAK_ID = 2;
    private static final int SETTINGS_ID = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(
                ThemeColors.PREFS_NAME,
                MODE_PRIVATE
        );

        boolean darkModeEnabled = preferences.getBoolean(
                ThemeColors.DARK_MODE_KEY,
                false
        );

        AppCompatDelegate.setDefaultNightMode(
                darkModeEnabled
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ThemeColors colors = new ThemeColors(this);
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setBackgroundColor(colors.surface);

        navigation.getMenu().clear();
        navigation.getMenu()
                .add(0, HOME_ID, 0, "Hoje")
                .setIcon(android.R.drawable.ic_menu_today);
        navigation.getMenu()
                .add(0, STREAK_ID, 1, "Streak")
                .setIcon(android.R.drawable.ic_menu_month);
        navigation.getMenu()
                .add(0, SETTINGS_ID, 2, "Config")
                .setIcon(android.R.drawable.ic_menu_manage);

        navigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == HOME_ID) {
                showFragment(new HomeFragment());
                return true;
            }

            if (item.getItemId() == STREAK_ID) {
                showFragment(new StreakFragment());
                return true;
            }

            if (item.getItemId() == SETTINGS_ID) {
                showFragment(new SettingsFragment());
                return true;
            }

            return false;
        });

        if (savedInstanceState == null) {
            navigation.setSelectedItemId(HOME_ID);
        }
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
