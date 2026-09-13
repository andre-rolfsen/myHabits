package com.example.myhabits;

import java.util.ArrayList;

public class Tracker {

    public enum PeriodUnit {
        DIAS("dia(s)"),
        SEMANAS("semana(s)"),
        MESES("mês(es)");

        public final String label;

        PeriodUnit(String label) {
            this.label = label;
        }
    }

    public String name;
    public String unit;
    public int target;
    public int current;
    public boolean isLimit;

    public PeriodUnit periodUnit;
    public int periodAmount;

    public int ringColor;

    public final ArrayList<ItemOption> options = new ArrayList<>();

    public Tracker(
            String name,
            String unit,
            int target,
            boolean isLimit,
            PeriodUnit periodUnit,
            int periodAmount,
            int ringColor
    ) {
        this.name = name;
        this.unit = unit;
        this.target = target;
        this.isLimit = isLimit;
        this.periodUnit = periodUnit;
        this.periodAmount = periodAmount;
        this.ringColor = ringColor;
        this.current = 0;
    }

    public void addOption(String name, int amount) {
        options.add(new ItemOption(name, amount));
    }

    public String getPeriodLabel() {
        if (periodUnit == null) {
            return "";
        }
        return periodAmount + " " + periodUnit.label;
    }

    public static class ItemOption {
        public String name;
        public int amount;

        public ItemOption(String name, int amount) {
            this.name = name;
            this.amount = amount;
        }
    }
}
