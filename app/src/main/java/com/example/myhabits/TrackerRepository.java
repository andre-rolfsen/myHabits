package com.example.myhabits;

import android.content.Context;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrackerRepository {

    private final AppDao dao;
    private final ExecutorService executor;

    public interface Callback<T> {
        void onResult(T result);
    }

    public TrackerRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        dao = database.appDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public void insertTracker(
            TrackerEntity tracker,
            Callback<Long> callback
    ) {
        executor.execute(() -> {
            long id = dao.insertTracker(tracker);

            if (callback != null) {
                callback.onResult(id);
            }
        });
    }

    public void getAllTrackers(
            Callback<List<TrackerEntity>> callback
    ) {
        executor.execute(() -> {
            List<TrackerEntity> trackers = dao.getAllTrackers();

            if (callback != null) {
                callback.onResult(trackers);
            }
        });
    }

    public void getTrackerById(
            int trackerId,
            Callback<TrackerEntity> callback
    ) {
        executor.execute(() -> {
            TrackerEntity tracker = dao.getTrackerById(trackerId);

            if (callback != null) {
                callback.onResult(tracker);
            }
        });
    }

    public void updateTracker(TrackerEntity tracker) {
        executor.execute(() -> dao.updateTracker(tracker));
    }

    public void deleteTracker(TrackerEntity tracker) {
        executor.execute(() -> dao.deleteTracker(tracker));
    }

    public void insertOption(
            TrackerOptionEntity option,
            Callback<Long> callback
    ) {
        executor.execute(() -> {
            long id = dao.insertTrackerOption(option);

            if (callback != null) {
                callback.onResult(id);
            }
        });
    }

    public void getOptionsForTracker(
            int trackerId,
            Callback<List<TrackerOptionEntity>> callback
    ) {
        executor.execute(() -> {
            List<TrackerOptionEntity> options =
                    dao.getOptionsForTracker(trackerId);

            if (callback != null) {
                callback.onResult(options);
            }
        });
    }

    public void saveDailyRecord(
            int trackerId,
            long date,
            boolean success
    ) {
        executor.execute(() -> dao.insertDailyRecord(
                new DailyRecordEntity(trackerId, date, success)
        ));
    }

    public void getRecordsForTracker(
            int trackerId,
            Callback<List<DailyRecordEntity>> callback
    ) {
        executor.execute(() -> {
            List<DailyRecordEntity> records =
                    dao.getRecordsForTracker(trackerId);

            if (callback != null) {
                callback.onResult(records);
            }
        });
    }

    public void getRecordsFromDays(
            long startDate,
            Callback<List<DailyRecordEntity>> callback
    ) {
        executor.execute(() -> {
            List<DailyRecordEntity> records =
                    dao.getRecordsFromDays(startDate);

            if (callback != null) {
                callback.onResult(records);
            }
        });
    }
}