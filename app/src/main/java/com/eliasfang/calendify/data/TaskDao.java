package com.eliasfang.calendify.data;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.eliasfang.calendify.models.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Delete
    void delete(Task task);

    @Update
    void update(Task task);

    @Query("DELETE FROM task_table")
    void deleteAll();

//    @Query("DELETE FROM task_table WHERE id = :taskId")
//    void deleteTask(int taskId);


    @Query("SELECT * from task_table")
    LiveData<List<Task>> getAllTasks();

//    @Query("UPDATE task_table SET isCompleted = 1 WHERE id = :taskId")
//    void updateIsComplete(int taskId);
//
//    @Query("SELECT * from task_table where isCompleted = 0")
//    LiveData<List<Task>> getTasksInProgress();
//
//    @Query("SELECT * from task_table where hasAlarm = 'true'")
//    List<Task> getTasksWithAlarm();
}
