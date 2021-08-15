package com.eliasfang.calendify.data;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.eliasfang.calendify.models.Task;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private TaskRepository myRepository;

    private LiveData<List<Task>> myAllTasks;



    public TaskViewModel (Application application) {
        super(application);
        myRepository = new TaskRepository(application);
        myAllTasks = myRepository.getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() { return myAllTasks; }

    public LiveData<List<Task>> getTasksByDate(String date) { return myRepository.getTasksByDate(date); }

    public LiveData<List<Task>> getAllTasksAlpha() { return myRepository.getAllTasksAlpha(); }

    public LiveData<List<Task>> getAllTasksCategory() { return myRepository.getAllCategory(); }

    public LiveData<List<Task>> getAllTasksAlphaCategory() { return myRepository.getAllAlphaCategory(); }

    public void insert(Task task) {myRepository.insert(task); }

    public void delete(Task task) {
        myRepository.delete(task);
    }

    public void update(Task task) { myRepository.update(task); }

    public Task getTask(int alarmId) { return myRepository.getTask(alarmId);}

    public void deleteAllNotes() { myRepository.deleteAll(); }

    //public void refreshTasks() { myAllTasks = myRepository.getAllTasks(); }
}
