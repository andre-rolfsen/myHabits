package com.example.myhabits;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateTrackerActivity extends AppCompatActivity {

    private final int[] palette = {
            Color.rgb(78, 91, 213),
            Color.rgb(54, 177, 125),
            Color.rgb(230, 76, 93),
            Color.rgb(243, 156, 18),
            Color.rgb(155, 89, 182),
            Color.rgb(231, 84, 128)
    };

    private ThemeColors colors;

    private EditText nameField;
    private EditText unitField;
    private EditText targetField;
    private EditText periodAmountField;

    private RadioGroup typeGroup;
    private RadioGroup periodGroup;

    private int limitId;
    private int weeksId;
    private int monthsId;

    private int selectedColor;
    private final List<View> colorButtons = new ArrayList<>();

    private LinearLayout optionsContainer;
    private TrackerRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        colors = new ThemeColors(this);
        repository = new TrackerRepository(this);
        selectedColor = palette[0];

        buildScreen();
    }

    private void buildScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(colors.background);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(24), dp(20), dp(24), dp(4));

        TextView cancel = new TextView(this);
        cancel.setText("‹ Cancelar");
        cancel.setTextSize(18);
        cancel.setTextColor(colors.primary);
        cancel.setPadding(0, dp(8), 0, dp(8));
        cancel.setOnClickListener(v -> finish());

        header.addView(cancel, new LinearLayout.LayoutParams(-1, dp(48)));

        TextView title = new TextView(this);
        title.setText("Criar acompanhamento");
        title.setTextSize(26);
        title.setTextColor(colors.textPrimary);
        title.setTypeface(null, Typeface.BOLD);

        header.addView(title, new LinearLayout.LayoutParams(-1, -2));
        root.addView(header, new LinearLayout.LayoutParams(-1, -2));

        ScrollView scrollView = new ScrollView(this);

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(24), dp(8), dp(24), dp(30));

        nameField = createField("Nome: Água, Cafeína...");
        unitField = createField("Unidade: mg, ml, garrafinhas...");
        targetField = createField("Quantidade da meta ou limite");
        targetField.setInputType(InputType.TYPE_CLASS_NUMBER);

        typeGroup = new RadioGroup(this);
        typeGroup.setOrientation(RadioGroup.HORIZONTAL);

        RadioButton goalRadio = createRadio("Meta");
        goalRadio.setId(View.generateViewId());

        RadioButton limitRadio = createRadio("Limite");
        limitId = View.generateViewId();
        limitRadio.setId(limitId);

        typeGroup.addView(
                goalRadio,
                new RadioGroup.LayoutParams(0, -2, 1)
        );

        typeGroup.addView(
                limitRadio,
                new RadioGroup.LayoutParams(0, -2, 1)
        );

        goalRadio.setChecked(true);

        TextView periodTitle = sectionTitle("Período de repetição");

        LinearLayout periodRow = new LinearLayout(this);
        periodRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView everyText = new TextView(this);
        everyText.setText("A cada");
        everyText.setTextSize(15);
        everyText.setTextColor(colors.textPrimary);

        periodAmountField = createField("Ex.: 3");
        periodAmountField.setText("1");
        periodAmountField.setInputType(InputType.TYPE_CLASS_NUMBER);

        periodRow.addView(
                everyText,
                new LinearLayout.LayoutParams(-2, dp(56))
        );

        LinearLayout.LayoutParams amountParams =
                new LinearLayout.LayoutParams(dp(100), dp(56));

        amountParams.setMargins(dp(12), 0, 0, 0);

        periodRow.addView(periodAmountField, amountParams);

        periodGroup = new RadioGroup(this);
        periodGroup.setOrientation(RadioGroup.HORIZONTAL);

        RadioButton daysRadio = createRadio("Dias");
        daysRadio.setId(View.generateViewId());

        RadioButton weeksRadio = createRadio("Semanas");
        weeksId = View.generateViewId();
        weeksRadio.setId(weeksId);

        RadioButton monthsRadio = createRadio("Meses");
        monthsId = View.generateViewId();
        monthsRadio.setId(monthsId);

        periodGroup.addView(
                daysRadio,
                new RadioGroup.LayoutParams(0, -2, 1)
        );

        periodGroup.addView(
                weeksRadio,
                new RadioGroup.LayoutParams(0, -2, 1)
        );

        periodGroup.addView(
                monthsRadio,
                new RadioGroup.LayoutParams(0, -2, 1)
        );

        daysRadio.setChecked(true);

        TextView colorTitle = sectionTitle("Cor do ciclo");

        LinearLayout paletteRow = new LinearLayout(this);
        paletteRow.setGravity(Gravity.CENTER_VERTICAL);

        for (int color : palette) {
            View dot = new View(this);

            dot.setOnClickListener(v -> {
                selectedColor = color;
                updateColorSelection();
            });

            colorButtons.add(dot);

            LinearLayout.LayoutParams dotParams =
                    new LinearLayout.LayoutParams(dp(38), dp(38));

            dotParams.setMargins(0, 0, dp(12), 0);

            paletteRow.addView(dot, dotParams);
        }

        updateColorSelection();

        TextView optionsTitle = sectionTitle("Opções de consumo");

        TextView optionsSubtitle = new TextView(this);
        optionsSubtitle.setText(
                "Ex.: Copo = 250 ml, Expresso = 80 mg"
        );
        optionsSubtitle.setTextSize(14);
        optionsSubtitle.setTextColor(colors.textSecondary);

        optionsContainer = new LinearLayout(this);
        optionsContainer.setOrientation(LinearLayout.VERTICAL);

        addOptionRow();

        Button addOptionButton = new Button(this);
        addOptionButton.setText("+ Adicionar opção");
        addOptionButton.setTextColor(colors.primary);
        addOptionButton.setTextSize(15);
        addOptionButton.setAllCaps(false);

        GradientDrawable addOptionBackground =
                roundDrawable(colors.surface, dp(18));

        addOptionBackground.setStroke(dp(1), colors.primary);

        addOptionButton.setBackground(addOptionBackground);
        addOptionButton.setOnClickListener(v -> addOptionRow());

        LinearLayout.LayoutParams addOptionParams =
                new LinearLayout.LayoutParams(-1, dp(52));

        addOptionParams.setMargins(0, dp(10), 0, 0);

        Button createButton = new Button(this);
        createButton.setText("Criar");
        createButton.setTextColor(Color.WHITE);
        createButton.setTextSize(16);
        createButton.setAllCaps(false);
        createButton.setBackground(roundDrawable(colors.primary, dp(24)));
        createButton.setOnClickListener(v -> createTracker());

        LinearLayout.LayoutParams createParams =
                new LinearLayout.LayoutParams(-1, dp(58));

        createParams.setMargins(0, dp(30), 0, 0);

        form.addView(nameField);
        form.addView(unitField);
        form.addView(targetField);
        form.addView(typeGroup);
        form.addView(periodTitle);
        form.addView(periodRow);
        form.addView(periodGroup);
        form.addView(colorTitle);
        form.addView(paletteRow);
        form.addView(optionsTitle);
        form.addView(optionsSubtitle);
        form.addView(optionsContainer);
        form.addView(addOptionButton, addOptionParams);
        form.addView(createButton, createParams);

        scrollView.addView(
                form,
                new ScrollView.LayoutParams(-1, -2)
        );

        root.addView(scrollView, new LinearLayout.LayoutParams(-1, 0, 1));

        setContentView(root);
    }

    private void addOptionRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        EditText optionName = createField("Ex.: Copo");
        optionName.setInputType(InputType.TYPE_CLASS_TEXT);

        EditText optionAmount = createField("Quantidade");
        optionAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

        Button removeButton = new Button(this);
        removeButton.setText("×");
        removeButton.setTextSize(20);
        removeButton.setTextColor(colors.limit);
        removeButton.setAllCaps(false);
        removeButton.setBackground(roundDrawable(colors.surface, dp(14)));
        removeButton.setOnClickListener(v -> {
            if (optionsContainer.getChildCount() > 1) {
                optionsContainer.removeView(row);
            } else {
                Toast.makeText(
                        this,
                        "Mantenha pelo menos uma opção",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        LinearLayout.LayoutParams nameParams =
                new LinearLayout.LayoutParams(0, dp(56), 1.3f);

        nameParams.setMargins(0, dp(4), dp(8), dp(4));

        LinearLayout.LayoutParams amountParams =
                new LinearLayout.LayoutParams(0, dp(56), 1f);

        amountParams.setMargins(0, dp(4), dp(8), dp(4));

        LinearLayout.LayoutParams removeParams =
                new LinearLayout.LayoutParams(dp(52), dp(56));

        removeParams.setMargins(0, dp(4), 0, dp(4));

        row.addView(optionName, nameParams);
        row.addView(optionAmount, amountParams);
        row.addView(removeButton, removeParams);

        optionsContainer.addView(row);
    }

    private void createTracker() {
        String name = nameField.getText().toString().trim();
        String unit = unitField.getText().toString().trim();
        String targetText = targetField.getText().toString().trim();
        String periodText = periodAmountField.getText().toString().trim();

        if (name.isEmpty()) {
            nameField.setError("Informe um nome");
            return;
        }

        if (unit.isEmpty()) {
            unitField.setError("Informe uma unidade");
            return;
        }

        int target;
        int periodAmount;

        try {
            target = Integer.parseInt(targetText);
            periodAmount = Integer.parseInt(periodText);
        } catch (NumberFormatException error) {
            Toast.makeText(
                    this,
                    "Informe números válidos",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (target <= 0 || periodAmount <= 0) {
            Toast.makeText(
                    this,
                    "Os valores devem ser maiores que zero",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        List<PendingOption> options = readOptions();

        if (options.isEmpty()) {
            Toast.makeText(
                    this,
                    "Adicione pelo menos uma opção válida",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String periodUnit = "DIAS";

        if (periodGroup.getCheckedRadioButtonId() == weeksId) {
            periodUnit = "SEMANAS";
        } else if (periodGroup.getCheckedRadioButtonId() == monthsId) {
            periodUnit = "MESES";
        }

        long now = System.currentTimeMillis();

        TrackerEntity tracker = new TrackerEntity(
                name,
                unit,
                target,
                0,
                typeGroup.getCheckedRadioButtonId() == limitId,
                periodUnit,
                periodAmount,
                selectedColor,
                now,
                startOfDay(now)
        );

        repository.insertTracker(tracker, trackerId -> {
            for (PendingOption option : options) {
                repository.insertOption(
                        new TrackerOptionEntity(
                                trackerId.intValue(),
                                option.name,
                                option.amount
                        ),
                        null
                );
            }

            runOnUiThread(() -> {
                Toast.makeText(
                        CreateTrackerActivity.this,
                        "Acompanhamento criado!",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            });
        });
    }

    private List<PendingOption> readOptions() {
        List<PendingOption> result = new ArrayList<>();

        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            View child = optionsContainer.getChildAt(i);

            if (!(child instanceof LinearLayout)) {
                continue;
            }

            LinearLayout row = (LinearLayout) child;

            EditText nameField = (EditText) row.getChildAt(0);
            EditText amountField = (EditText) row.getChildAt(1);

            String name = nameField.getText().toString().trim();
            String amountText = amountField.getText().toString().trim();

            if (name.isEmpty() || amountText.isEmpty()) {
                continue;
            }

            try {
                int amount = Integer.parseInt(amountText);

                if (amount > 0) {
                    result.add(new PendingOption(name, amount));
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return result;
    }

    private EditText createField(String hint) {
        EditText field = new EditText(this);

        field.setHint(hint);
        field.setHintTextColor(colors.textSecondary);
        field.setTextColor(colors.textPrimary);
        field.setTextSize(15);
        field.setSingleLine(true);
        field.setPadding(dp(14), 0, dp(14), 0);
        field.setBackground(roundDrawable(colors.inputBackground, dp(14)));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, dp(56));

        params.setMargins(0, dp(6), 0, dp(6));

        field.setLayoutParams(params);

        return field;
    }

    private RadioButton createRadio(String text) {
        RadioButton radioButton = new RadioButton(this);
        radioButton.setText(text);
        radioButton.setTextSize(16);
        radioButton.setTextColor(colors.textPrimary);

        return radioButton;
    }

    private TextView sectionTitle(String text) {
        TextView title = new TextView(this);
        title.setText(text);
        title.setTextSize(17);
        title.setTextColor(colors.textPrimary);
        title.setTypeface(null, Typeface.BOLD);
        title.setPadding(0, dp(18), 0, dp(6));

        return title;
    }

    private void updateColorSelection() {
        for (int i = 0; i < colorButtons.size(); i++) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);
            drawable.setColor(palette[i]);

            if (palette[i] == selectedColor) {
                drawable.setStroke(dp(3), colors.textPrimary);
            }

            colorButtons.get(i).setBackground(drawable);
        }
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

    private int dp(int value) {
        return (int) (
                value
                        * getResources()
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

    private static class PendingOption {
        String name;
        int amount;

        PendingOption(String name, int amount) {
            this.name = name;
            this.amount = amount;
        }
    }
}