package com.example.myhabits;

import android.animation.ValueAnimator;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CounterActivity extends AppCompatActivity {

    private TrackerEntity trackerEntity;
    private Tracker displayTracker;

    private TrackerRepository repository;
    private ThemeColors colors;

    private ProgressRing progressRing;
    private TextView valueText;

    private boolean animating;
    private int selectedOptionAmount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        colors = new ThemeColors(this);
        repository = new TrackerRepository(this);

        int trackerId = getIntent().getIntExtra("tracker_id", -1);

        if (trackerId < 0) {
            finish();
            return;
        }

        repository.getTrackerById(trackerId, tracker -> {
            if (tracker == null) {
                runOnUiThread(this::finish);
                return;
            }

            trackerEntity = tracker;
            displayTracker = toTracker(trackerEntity);

            runOnUiThread(this::buildScreen);
        });
    }

    private void buildScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(colors.background);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(24), dp(20), dp(24), 0);

        TextView back = new TextView(this);
        back.setText("‹ Voltar");
        back.setTextSize(18);
        back.setTextColor(colors.primary);
        back.setPadding(0, dp(8), 0, dp(8));
        back.setOnClickListener(v -> finish());

        header.addView(back, new LinearLayout.LayoutParams(-1, dp(48)));

        TextView title = new TextView(this);
        title.setText(trackerEntity.name);
        title.setTextSize(28);
        title.setTextColor(colors.textPrimary);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        header.addView(title, new LinearLayout.LayoutParams(-1, dp(48)));

        TextView type = new TextView(this);

        type.setText(
                (trackerEntity.isLimit ? "LIMITE" : "META")
                        + " • a cada "
                        + trackerEntity.periodAmount
                        + " "
                        + periodLabel(trackerEntity.periodUnit)
        );

        type.setTextSize(14);
        type.setGravity(Gravity.CENTER);
        type.setTypeface(null, Typeface.BOLD);

        type.setTextColor(
                trackerEntity.isLimit
                        ? colors.limit
                        : colors.goal
        );

        header.addView(type, new LinearLayout.LayoutParams(-1, dp(32)));

        root.addView(header, new LinearLayout.LayoutParams(-1, -2));

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        content.setPadding(dp(24), dp(8), dp(24), dp(30));

        valueText = new TextView(this);
        valueText.setTextSize(20);
        valueText.setTextColor(colors.textPrimary);
        valueText.setGravity(Gravity.CENTER);

        content.addView(
                valueText,
                new LinearLayout.LayoutParams(-1, dp(58))
        );

        progressRing = new ProgressRing(this, displayTracker);

        progressRing.setOnAddClickListener(
                () -> addAmount(selectedOptionAmount)
        );

        content.addView(
                progressRing,
                new LinearLayout.LayoutParams(dp(320), dp(320))
        );

        TextView instruction = new TextView(this);

        instruction.setText(
                "Escolha uma opção e toque no botão para adicionar"
        );

        instruction.setTextSize(16);
        instruction.setTextColor(colors.textSecondary);
        instruction.setGravity(Gravity.CENTER);
        instruction.setPadding(0, dp(12), 0, dp(6));

        content.addView(
                instruction,
                new LinearLayout.LayoutParams(-1, dp(52))
        );

        Spinner spinner = new Spinner(this);
        spinner.setBackgroundColor(colors.inputBackground);

        LinearLayout.LayoutParams spinnerParams =
                new LinearLayout.LayoutParams(-1, dp(56));

        spinnerParams.setMargins(0, dp(4), 0, 0);

        content.addView(spinner, spinnerParams);

        scrollView.addView(
                content,
                new ScrollView.LayoutParams(-1, -2)
        );

        root.addView(
                scrollView,
                new LinearLayout.LayoutParams(-1, 0, 1)
        );

        setContentView(root);

        updateText();
        loadOptions(spinner);
    }

    private void loadOptions(Spinner spinner) {
        repository.getOptionsForTracker(
                trackerEntity.id,
                options -> runOnUiThread(() -> {
                    List<String> labels = new ArrayList<>();

                    for (TrackerOptionEntity option : options) {
                        labels.add(
                                option.name
                                        + " ("
                                        + option.amount
                                        + " "
                                        + trackerEntity.unit
                                        + ")"
                        );
                    }

                    if (options.isEmpty()) {
                        labels.add("Adicionar 1 " + trackerEntity.unit);
                        selectedOptionAmount = 1;
                    } else {
                        selectedOptionAmount = options.get(0).amount;
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            CounterActivity.this,
                            android.R.layout.simple_spinner_item,
                            labels
                    );

                    adapter.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item
                    );

                    spinner.setAdapter(adapter);

                    spinner.setOnItemSelectedListener(
                            new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(
                                        AdapterView<?> parent,
                                        View view,
                                        int position,
                                        long id
                                ) {
                                    if (
                                            position >= 0
                                                    && position < options.size()
                                    ) {
                                        selectedOptionAmount =
                                                options.get(position).amount;
                                    }
                                }

                                @Override
                                public void onNothingSelected(
                                        AdapterView<?> parent
                                ) {
                                    selectedOptionAmount = 1;
                                }
                            }
                    );
                })
        );
    }

    private void addAmount(int amount) {
        if (animating) {
            return;
        }

        int oldValue = trackerEntity.current;
        int newValue = oldValue + amount;

        animating = true;

        ValueAnimator animator = ValueAnimator.ofInt(oldValue, newValue);
        animator.setDuration(700);

        animator.addUpdateListener(animation -> {
            trackerEntity.current =
                    (int) animation.getAnimatedValue();

            displayTracker.current = trackerEntity.current;

            updateText();
            progressRing.invalidate();
        });

        animator.addListener(
                new android.animation.Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(
                            android.animation.Animator animation
                    ) {
                    }

                    @Override
                    public void onAnimationEnd(
                            android.animation.Animator animation
                    ) {
                        animating = false;
                        repository.updateTracker(trackerEntity);
                    }

                    @Override
                    public void onAnimationCancel(
                            android.animation.Animator animation
                    ) {
                        animating = false;
                    }

                    @Override
                    public void onAnimationRepeat(
                            android.animation.Animator animation
                    ) {
                    }
                }
        );

        animator.start();
    }

    private void updateText() {
        String text =
                trackerEntity.current
                        + " / "
                        + trackerEntity.target
                        + " "
                        + trackerEntity.unit;

        if (trackerEntity.current > trackerEntity.target) {
            text +=
                    "\nExcedente: "
                            + (trackerEntity.current - trackerEntity.target)
                            + " "
                            + trackerEntity.unit;
        }

        valueText.setText(text);
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

    private String periodLabel(String periodUnit) {
        if ("SEMANAS".equals(periodUnit)) {
            return "semana(s)";
        }

        if ("MESES".equals(periodUnit)) {
            return "mês(es)";
        }

        return "dia(s)";
    }

    private int dp(int value) {
        return (int) (
                value
                        * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }
}