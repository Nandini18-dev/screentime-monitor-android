package com.nandini.screentime.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.nandini.screentime.data.PackageTotal;
import com.nandini.screentime.data.entity.NotificationEvent;

import java.util.List;

@Dao
public interface NotificationEventDao {

    @Insert
    void insert(NotificationEvent event);

    @Query("SELECT COUNT(*) FROM notification_event WHERE dateKey = :date")
    LiveData<Integer> countForDate(String date);

    @Query("SELECT packageName, COUNT(*) AS total FROM notification_event " +
            "WHERE dateKey = :date GROUP BY packageName ORDER BY total DESC LIMIT :limit")
    LiveData<List<PackageTotal>> getTopPackagesForDate(String date, int limit);

    @Query("DELETE FROM notification_event WHERE dateKey < :cutoffDate")
    void deleteOlderThan(String cutoffDate);

    @Query("DELETE FROM notification_event")
    void deleteAll();
}
