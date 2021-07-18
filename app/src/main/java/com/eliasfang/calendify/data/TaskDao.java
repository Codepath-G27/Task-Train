package com.eliasfang.calendify.data;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.eliasfang.calendify.models.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Query("DELETE FROM task_table")
    void deleteAll();

    @Query("DELETE FROM task_table WHERE id = :taskId")
    void deleteTask(int taskId);

    //TODO be able to organize tasks by earliest due date first
    @Query("SELECT * from task_table where isCompleted = 0")
    LiveData<List<Task>> getAllTasks();

    //TODO create edit task function, which would query for a given task id, delete it
    //then reinsert with same id, updated values, update query

    @Query("UPDATE task_table SET isCompleted = 1 WHERE id = :taskId")
    void updateIsComplete(int taskId);

    @Query("SELECT * from task_table where isCompleted = 0")
    LiveData<List<Task>> getTasksInProgress();

    @Query("SELECT * from task_table where hasAlarm = 'true'")
    List<Task> getTasksWithAlarm();
}
