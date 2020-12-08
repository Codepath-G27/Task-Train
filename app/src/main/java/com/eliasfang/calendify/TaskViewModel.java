package com.eliasfang.calendify;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

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

    public void insert(Task task) {myRepository.insert(task); }

}
