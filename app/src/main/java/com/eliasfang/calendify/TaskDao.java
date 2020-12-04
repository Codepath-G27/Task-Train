package com.eliasfang.calendify;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Query("DELETE FROM task_table")
    void deleteAll();

    //TODO create delete completed tasks query

    //TODO be able to organize tasks by earliest due date first
    @Query("SELECT * from task_table")
    LiveData<List<Task>> getAllTasks();
}
