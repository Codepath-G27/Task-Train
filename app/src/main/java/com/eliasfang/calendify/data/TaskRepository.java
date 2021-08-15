package com.eliasfang.calendify.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.eliasfang.calendify.models.Task;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TaskRepository {

    private TaskDao myTaskDao;
    private LiveData<List<Task>> myAllTasks;

    public TaskRepository(Application application) {
        TaskRoomDatabase db = TaskRoomDatabase.getDatabase(application);
        myTaskDao = db.taskDao();
        myAllTasks = myTaskDao.getAllTasks();
    }

    //Use the executor to write the data Async
    public void insert (Task task) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> {
            myTaskDao.insert(task);
        });
    }

    public void delete(Task task) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> {
            myTaskDao.delete(task);
        });
    }

    public void update(Task task) {
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> {
            myTaskDao.update(task);
        });
    }

    public void deleteAll(){
        TaskRoomDatabase.databaseWriteExecutor.execute(() -> {
            myTaskDao.deleteAll();
        });
    }


    public Task getTask(int alarmId) {
        Future<Task> callback =  TaskRoomDatabase.databaseWriteExecutor.submit(() -> myTaskDao.getTask(alarmId));
        try{
            return callback.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    return null;
    }

    public LiveData<List<Task>> getAllTasks() {
        return myAllTasks;
    }

    public LiveData<List<Task>> getTasksByDate(String date) {
        Future<LiveData<List<Task>>> callback =  TaskRoomDatabase.databaseWriteExecutor.submit(() -> myTaskDao.getTasksByDate(date));
        try{
            return callback.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;

    }

    public LiveData<List<Task>> getAllTasksAlpha() {
        Future<LiveData<List<Task>>> callback =  TaskRoomDatabase.databaseWriteExecutor.submit(() -> myTaskDao.getAllAlphabetical());
        try{
            return callback.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LiveData<List<Task>> getAllCategory() {
        Future<LiveData<List<Task>>> callback =  TaskRoomDatabase.databaseWriteExecutor.submit(() -> myTaskDao.getAllCategory());
        try{
            return callback.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LiveData<List<Task>> getAllAlphaCategory() {
        Future<LiveData<List<Task>>> callback =  TaskRoomDatabase.databaseWriteExecutor.submit(() -> myTaskDao.getAllAlphaCategory());
        try{
            return callback.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


}
