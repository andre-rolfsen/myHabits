package com.example.myhabits;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "daily_records",
        foreignKeys = @ForeignKey(
                entity = TrackerEntity.class,
                parentColumns = "id",
                childColumns = "trackerId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = {"trackerId", "date"}, unique = true)}
)
public class DailyRecordEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int trackerId;
    public long date;
    public boolean success;

    public DailyRecordEntity(int trackerId, long date, boolean success) {
        this.trackerId = trackerId;
        this.date = date;
        this.success = success;
    }
}
