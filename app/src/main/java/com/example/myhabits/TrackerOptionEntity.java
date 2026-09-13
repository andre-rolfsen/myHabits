package com.example.myhabits;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "tracker_options",
        foreignKeys = @ForeignKey(
                entity = TrackerEntity.class,
                parentColumns = "id",
                childColumns = "trackerId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index("trackerId")
)
public class TrackerOptionEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int trackerId;
    public String name;
    public int amount;

    public TrackerOptionEntity(
            int trackerId,
            String name,
            int amount
    ) {
        this.trackerId = trackerId;
        this.name = name;
        this.amount = amount;
    }
}