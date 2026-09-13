package com.example.myhabits;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreakFragment extends Fragment {

    private TrackerRepository repository;
    private LinearLayout calendarContainer;
    private TextView streakText;
    private ThemeColors colors;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        repository = new TrackerRepository(requireContext());
        colors = new ThemeColors(requireContext());

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(32), dp(24), dp(24));
        root.setBackgroundColor(colors.background);

        TextView title = new TextView(requireContext());
        title.setText("Seu streak");
        title.setTextColor(colors.textPrimary);
        title.setTextSize(28);
        title.setTypeface(null, Typeface.BOLD);
        root.addView(title, new LinearLayout.LayoutParams(-1, -2));

        TextView subtitle = new TextView(requireContext());
        subtitle.setText("Dias seguidos batendo metas e respeitando limites");
        subtitle.setTextColor(colors.textSecondary);
        subtitle.setTextSize(15);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(-1, -2);
        subtitleParams.setMargins(0, dp(4), 0, dp(20));
        root.addView(subtitle, subtitleParams);

        streakText = new TextView(requireContext());
        streakText.setText("0 dias");
        streakText.setTextColor(colors.textPrimary);
        streakText.setTextSize(42);
        streakText.setTypeface(null, Typeface.BOLD);
        streakText.setGravity(Gravity.CENTER);
        root.addView(streakText, new LinearLayout.LayoutParams(-1, dp(80)));

        TextView calendarTitle = new TextView(requireContext());
        calendarTitle.setText("Últimos 90 dias");
        calendarTitle.setTextColor(colors.textSecondary);
        calendarTitle.setTextSize(16);
        calendarTitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams calendarTitleParams = new LinearLayout.LayoutParams(-1, -2);
        calendarTitleParams.setMargins(0, 0, 0, dp(12));
        root.addView(calendarTitle, calendarTitleParams);

        ScrollView scrollView = new ScrollView(requireContext());
        calendarContainer = new LinearLayout(requireContext());
        calendarContainer.setOrientation(LinearLayout.VERTICAL);
        calendarContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        scrollView.addView(calendarContainer, new ScrollView.LayoutParams(-1, -2));
        root.addView(scrollView, new LinearLayout.LayoutParams(-1, 0, 1));

        loadStreak();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (repository != null && calendarContainer != null) {
            colors = new ThemeColors(requireContext());
            loadStreak();
        }
    }

    private void loadStreak() {
        long today = startOfDay(System.currentTimeMillis());
        long startDate = addDays(today, -89);

        repository.getAllTrackers(trackers -> repository.getRecordsFromDays(
                startDate,
                records -> {
                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().runOnUiThread(() -> {
                        streakText.setText(calculateStreak(trackers, records, today) + " dias");
                        renderCalendar(trackers, records, startDate, today);
                    });
                }
        ));
    }

    private int calculateStreak(
            List<TrackerEntity> trackers,
            List<DailyRecordEntity> records,
            long today
    ) {
        if (trackers.isEmpty()) {
            return 0;
        }

        Map<Long, Map<Integer, Boolean>> map = createRecordMap(records);
        int streak = 0;

        for (long day = today; day >= addDays(today, -89); day = addDays(day, -1)) {
            if (!isSuccessfulDay(trackers, map.get(day))) {
                break;
            }
            streak++;
        }

        return streak;
    }

    private void renderCalendar(
            List<TrackerEntity> trackers,
            List<DailyRecordEntity> records,
            long startDate,
            long endDate
    ) {
        calendarContainer.removeAllViews();
        Map<Long, Map<Integer, Boolean>> map = createRecordMap(records);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDate);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        long currentDay = addDays(startDate, -(dayOfWeek - Calendar.SUNDAY));

        while (currentDay <= endDate) {
            LinearLayout weekRow = new LinearLayout(requireContext());
            weekRow.setOrientation(LinearLayout.HORIZONTAL);
            weekRow.setGravity(Gravity.CENTER);

            for (int i = 0; i < 7; i++) {
                View cell = new View(requireContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(28), dp(28));
                params.setMargins(dp(3), dp(3), dp(3), dp(3));

                if (currentDay <= endDate) {
                    Map<Integer, Boolean> dayMap = map.get(currentDay);
                    int cellColor = colors.border;

                    if (dayMap != null && !trackers.isEmpty()) {
                        cellColor = isSuccessfulDay(trackers, dayMap)
                                ? colors.goal
                                : colors.limit;
                    }

                    cell.setBackground(roundDrawable(cellColor, dp(6)));
                }

                weekRow.addView(cell, params);
                currentDay = addDays(currentDay, 1);
            }

            calendarContainer.addView(weekRow, new LinearLayout.LayoutParams(-1, -2));
        }
    }

    private Map<Long, Map<Integer, Boolean>> createRecordMap(List<DailyRecordEntity> records) {
        Map<Long, Map<Integer, Boolean>> map = new HashMap<>();
        for (DailyRecordEntity record : records) {
            map.putIfAbsent(record.date, new HashMap<>());
            map.get(record.date).put(record.trackerId, record.success);
        }
        return map;
    }

    private boolean isSuccessfulDay(
            List<TrackerEntity> trackers,
            Map<Integer, Boolean> dayMap
    ) {
        if (dayMap == null) {
            return false;
        }

        for (TrackerEntity tracker : trackers) {
            Boolean success = dayMap.get(tracker.id);
            if (success == null || !success) {
                return false;
            }
        }
        return true;
    }

    private long startOfDay(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long addDays(long millis, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return startOfDay(calendar.getTimeInMillis());
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
