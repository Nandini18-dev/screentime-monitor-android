package com.nandini.screentime.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.nandini.screentime.data.DateTotal;
import com.nandini.screentime.data.PackageTotal;
import com.nandini.screentime.data.entity.DailyAppUsage;

import java.util.List;

@Dao
public interface UsageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertAll(List<DailyAppUsage> rows);

    @Query("SELECT * FROM daily_app_usage WHERE date = :date ORDER BY foregroundTimeMs DESC")
    LiveData<List<DailyAppUsage>> getForDate(String date);

    @Query("SELECT COALESCE(SUM(foregroundTimeMs), 0) FROM daily_app_usage WHERE date = :date")
    LiveData<Long> getTotalForDate(String date);

    @Query("SELECT packageName, foregroundTimeMs AS total FROM daily_app_usage " +
            "WHERE date = :date ORDER BY foregroundTimeMs DESC LIMIT :limit")
    LiveData<List<PackageTotal>> getTopAppsForDate(String date, int limit);

    @Query("SELECT date, SUM(foregroundTimeMs) AS total FROM daily_app_usage " +
            "WHERE date BETWEEN :startDate AND :endDate GROUP BY date ORDER BY date ASC")
    LiveData<List<DateTotal>> getDailyTotals(String startDate, String endDate);

    @Query("SELECT COALESCE(SUM(foregroundTimeMs), 0) FROM daily_app_usage " +
            "WHERE date = :date AND packageName = :packageName")
    long getTotalForAppSync(String date, String packageName);

    @Query("SELECT COALESCE(SUM(foregroundTimeMs), 0) FROM daily_app_usage " +
            "WHERE date = :date AND packageName = :packageName")
    LiveData<Long> getTotalForApp(String date, String packageName);

    @Query("DELETE FROM daily_app_usage WHERE date < :cutoffDate")
    void deleteOlderThan(String cutoffDate);

    @Query("DELETE FROM daily_app_usage")
    void deleteAll();
}
