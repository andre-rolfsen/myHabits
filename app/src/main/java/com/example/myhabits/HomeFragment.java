package com.example.myhabits;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.List;

public class HomeFragment extends Fragment {

    private LinearLayout cardsContainer;
    private TrackerRepository repository;
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
        root.setPadding(dp(32), dp(42), dp(32), dp(24));
        root.setBackgroundColor(colors.background);

        TextView title = new TextView(requireContext());
        title.setText("Meu dia");
        title.setTextColor(colors.textPrimary);
        title.setTextSize(30);
        title.setTypeface(null, Typeface.BOLD);

        root.addView(title, new LinearLayout.LayoutParams(-1, -2));

        TextView subtitle = new TextView(requireContext());
        subtitle.setText("Acompanhe suas metas e limites");
        subtitle.setTextColor(colors.textSecondary);
        subtitle.setTextSize(16);

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(-1, -2);

        subtitleParams.setMargins(0, dp(4), 0, 0);
        root.addView(subtitle, subtitleParams);

        Button addButton = new Button(requireContext());
        addButton.setText("+ Novo acompanhamento");
        addButton.setTextColor(Color.WHITE);
        addButton.setTextSize(15);
        addButton.setAllCaps(false);
        addButton.setBackground(roundDrawable(colors.primary, dp(24)));

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    requireContext(),
                    CreateTrackerActivity.class
            );

            startActivity(intent);
        });

        LinearLayout.LayoutParams addButtonParams =
                new LinearLayout.LayoutParams(-1, dp(58));

        addButtonParams.setMargins(0, dp(28), 0, dp(18));
        root.addView(addButton, addButtonParams);

        ScrollView scrollView = new ScrollView(requireContext());

        cardsContainer = new LinearLayout(requireContext());
        cardsContainer.setOrientation(LinearLayout.VERTICAL);

        scrollView.addView(
                cardsContainer,
                new ScrollView.LayoutParams(-1, -2)
        );

        root.addView(
                scrollView,
                new LinearLayout.LayoutParams(-1, 0, 1)
        );

        loadTrackers();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (repository != null && cardsContainer != null) {
            colors = new ThemeColors(requireContext());
            loadTrackers();
        }
    }

    private void loadTrackers() {
        repository.getAllTrackers(entities -> {
            if (getActivity() == null) {
                return;
            }

            getActivity().runOnUiThread(() -> renderCards(entities));
        });
    }

    private void renderCards(List<TrackerEntity> entities) {
        cardsContainer.removeAllViews();

        if (entities.isEmpty()) {
            TextView emptyText = new TextView(requireContext());
            emptyText.setText("Nenhum acompanhamento criado ainda.");
            emptyText.setTextSize(17);
            emptyText.setTextColor(colors.textSecondary);
            emptyText.setGravity(Gravity.CENTER);
            emptyText.setPadding(0, dp(50), 0, dp(50));

            cardsContainer.addView(
                    emptyText,
                    new LinearLayout.LayoutParams(-1, -2)
            );

            return;
        }

        for (TrackerEntity entity : entities) {
            Tracker tracker = toTracker(entity);

            LinearLayout card = new LinearLayout(requireContext());
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(26), dp(24), dp(26), dp(24));
            card.setElevation(dp(5));
            card.setBackground(roundDrawable(colors.surface, dp(28)));

            TextView name = new TextView(requireContext());
            name.setText(tracker.name);
            name.setTextColor(colors.textPrimary);
            name.setTextSize(21);
            name.setTypeface(null, Typeface.BOLD);

            card.addView(name);

            TextView type = new TextView(requireContext());
            type.setText(
                    (tracker.isLimit ? "LIMITE" : "META")
                            + " • a cada "
                            + tracker.getPeriodLabel()
            );

            type.setTextColor(
                    tracker.isLimit
                            ? colors.limit
                            : colors.goal
            );

            type.setTextSize(13);
            type.setTypeface(null, Typeface.BOLD);

            card.addView(type);

            TextView progress = new TextView(requireContext());
            progress.setText(formatProgress(tracker));
            progress.setTextSize(16);
            progress.setTextColor(colors.textSecondary);
            progress.setPadding(0, dp(8), 0, 0);

            card.addView(progress);

            Button openButton = new Button(requireContext());
            openButton.setText("Abrir contador");
            openButton.setTextColor(Color.WHITE);
            openButton.setTextSize(15);
            openButton.setAllCaps(false);
            openButton.setBackground(roundDrawable(colors.primary, dp(22)));

            openButton.setOnClickListener(v -> {
                Intent intent = new Intent(
                        requireContext(),
                        CounterActivity.class
                );

                intent.putExtra("tracker_id", entity.id);
                startActivity(intent);
            });

            LinearLayout.LayoutParams openButtonParams =
                    new LinearLayout.LayoutParams(-1, dp(54));

            openButtonParams.setMargins(0, dp(18), 0, 0);

            card.addView(openButton, openButtonParams);

            Button removeButton = new Button(requireContext());
            removeButton.setText("Remover acompanhamento");
            removeButton.setTextColor(colors.limit);
            removeButton.setTextSize(14);
            removeButton.setAllCaps(false);

            GradientDrawable removeBackground =
                    roundDrawable(colors.surface, dp(22));

            removeBackground.setStroke(dp(1), colors.limit);

            removeButton.setBackground(removeBackground);

            removeButton.setOnClickListener(
                    v -> showRemoveConfirmation(entity)
            );

            LinearLayout.LayoutParams removeButtonParams =
                    new LinearLayout.LayoutParams(-1, dp(50));

            removeButtonParams.setMargins(0, dp(10), 0, 0);

            card.addView(removeButton, removeButtonParams);

            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(-1, -2);

            cardParams.setMargins(0, 0, 0, dp(18));

            cardsContainer.addView(card, cardParams);
        }
    }

    private void showRemoveConfirmation(TrackerEntity entity) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Remover acompanhamento?")
                .setMessage(
                        "Deseja remover \""
                                + entity.name
                                + "\"? Esta ação não pode ser desfeita."
                )
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Remover", (dialog, which) -> {
                    repository.deleteTracker(entity);

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            loadTrackers();
                        });
                    }
                })
                .show();
    }

    private Tracker toTracker(TrackerEntity entity) {
        Tracker.PeriodUnit periodUnit = Tracker.PeriodUnit.DIAS;

        if ("SEMANAS".equals(entity.periodUnit)) {
            periodUnit = Tracker.PeriodUnit.SEMANAS;
        } else if ("MESES".equals(entity.periodUnit)) {
            periodUnit = Tracker.PeriodUnit.MESES;
        }

        Tracker tracker = new Tracker(
                entity.name,
                entity.unit,
                entity.target,
                entity.isLimit,
                periodUnit,
                entity.periodAmount,
                entity.ringColor
        );

        tracker.current = entity.current;

        return tracker;
    }

    private String formatProgress(Tracker tracker) {
        String text =
                tracker.current
                        + " / "
                        + tracker.target
                        + " "
                        + tracker.unit;

        if (tracker.current > tracker.target) {
            text +=
                    " • excedente: "
                            + (tracker.current - tracker.target)
                            + " "
                            + tracker.unit;
        }

        return text;
    }

    private int dp(int value) {
        return (int) (
                value
                        * requireContext()
                        .getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    private GradientDrawable roundDrawable(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }
}