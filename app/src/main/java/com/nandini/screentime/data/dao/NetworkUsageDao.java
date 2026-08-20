package com.nandini.screentime.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.nandini.screentime.data.DateTotal;
import com.nandini.screentime.data.PackageTotal;
import com.nandini.screentime.data.entity.DailyNetworkUsage;

import java.util.List;

@Dao
public interface NetworkUsageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertAll(List<DailyNetworkUsage> rows);

    @Query("SELECT * FROM daily_network_usage WHERE date = :date ORDER BY (rxBytes + txBytes) DESC")
    LiveData<List<DailyNetworkUsage>> getForDate(String date);

    @Query("SELECT COALESCE(SUM(rxBytes + txBytes), 0) FROM daily_network_usage WHERE date = :date")
    LiveData<Long> getTotalBytesForDate(String date);

    @Query("SELECT COALESCE(SUM(rxBytes + txBytes), 0) FROM daily_network_usage " +
            "WHERE date = :date AND packageName = :packageName")
    LiveData<Long> getTotalBytesForApp(String date, String packageName);

    @Query("SELECT packageName, (rxBytes + txBytes) AS total FROM daily_network_usage " +
            "WHERE date = :date ORDER BY total DESC LIMIT :limit")
    LiveData<List<PackageTotal>> getTopAppsForDate(String date, int limit);

    @Query("SELECT date, SUM(rxBytes + txBytes) AS total FROM daily_network_usage " +
            "WHERE date BETWEEN :startDate AND :endDate GROUP BY date ORDER BY date ASC")
    LiveData<List<DateTotal>> getDailyTotals(String startDate, String endDate);

    @Query("DELETE FROM daily_network_usage WHERE date < :cutoffDate")
    void deleteOlderThan(String cutoffDate);

    @Query("DELETE FROM daily_network_usage")
    void deleteAll();
}
