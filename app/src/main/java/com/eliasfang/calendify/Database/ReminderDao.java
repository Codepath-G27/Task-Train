package com.eliasfang.calendify.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReminderDao {

    @Insert
    void insertAll(ReminderEntity reminderEntity);

    @Query("SELECT * FROM myTable")
    List<ReminderEntity> getAllData();

    @Query("DELETE FROM myTable WHERE id = :alarmId")
    void deleteTask(int alarmId);

}
