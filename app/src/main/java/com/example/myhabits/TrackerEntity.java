package com.example.myhabits;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "trackers")
public class TrackerEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String unit;
    public int target;
    public int current;
    public boolean isLimit;

    public String periodUnit;
    public int periodAmount;

    public int ringColor;

    public long createdAt;
    public long lastResetDate;

    public TrackerEntity(
            String name,
            String unit,
            int target,
            int current,
            boolean isLimit,
            String periodUnit,
            int periodAmount,
            int ringColor,
            long createdAt,
            long lastResetDate
    ) {
        this.name = name;
        this.unit = unit;
        this.target = target;
        this.current = current;
        this.isLimit = isLimit;
        this.periodUnit = periodUnit;
        this.periodAmount = periodAmount;
        this.ringColor = ringColor;
        this.createdAt = createdAt;
        this.lastResetDate = lastResetDate;
    }
}