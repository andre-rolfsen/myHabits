package com.example.myhabits;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AppDao {

    @Insert
    long insertTracker(TrackerEntity tracker);

    @Update
    void updateTracker(TrackerEntity tracker);

    @Delete
    void deleteTracker(TrackerEntity tracker);

    @Query("SELECT * FROM trackers ORDER BY createdAt ASC")
    List<TrackerEntity> getAllTrackers();

    @Query("SELECT * FROM trackers WHERE id = :trackerId LIMIT 1")
    TrackerEntity getTrackerById(int trackerId);

    @Insert
    long insertTrackerOption(TrackerOptionEntity option);

    @Query("SELECT * FROM tracker_options WHERE trackerId = :trackerId ORDER BY id ASC")
    List<TrackerOptionEntity> getOptionsForTracker(int trackerId);

    @Query("DELETE FROM tracker_options WHERE trackerId = :trackerId")
    void deleteOptionsForTracker(int trackerId);

    @Query("SELECT * FROM daily_records WHERE trackerId = :trackerId ORDER BY date DESC")
    List<DailyRecordEntity> getRecordsForTracker(int trackerId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDailyRecord(DailyRecordEntity record);

    @Query("SELECT * FROM daily_records WHERE trackerId = :trackerId AND date = :date")
    DailyRecordEntity getRecordForDay(int trackerId, long date);

    @Query("SELECT * FROM daily_records WHERE date >= :startDate ORDER BY date ASC")
    List<DailyRecordEntity> getRecordsFromDays(long startDate);

    @Query("DELETE FROM daily_records WHERE date < :cutoffDate")
    void deleteOldRecords(long cutoffDate);
}