package com.nandini.screentime.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.nandini.screentime.data.entity.LockUnlockEvent;

@Dao
public interface LockEventDao {

    @Insert
    void insert(LockUnlockEvent event);

    @Query("SELECT COUNT(*) FROM lock_unlock_event WHERE dateKey = :date AND type = :type")
    LiveData<Integer> countForDateAndType(String date, String type);

    @Query("DELETE FROM lock_unlock_event WHERE dateKey < :cutoffDate")
    void deleteOlderThan(String cutoffDate);

    @Query("DELETE FROM lock_unlock_event")
    void deleteAll();
}
