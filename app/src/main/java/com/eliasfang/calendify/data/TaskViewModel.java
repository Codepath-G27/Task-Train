package com.eliasfang.calendify.data;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.eliasfang.calendify.data.Task;
import com.eliasfang.calendify.data.TaskRepository;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private TaskRepository myRepository;

    private LiveData<List<Task>> myAllTasks;

    private boolean isSelected;




    public TaskViewModel (Application application) {
        super(application);
        myRepository = new TaskRepository(application);
        myAllTasks = myRepository.getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() { return myAllTasks; }

    public void insert(Task task) {myRepository.insert(task); }

    public void deleteTask(Task task) {
        myRepository.deleteTask(task);
    }


    public void updateCompleted(Task task) {
        myRepository.updateCompleted(task);
    }
    public void refreshTasks() {
        myAllTasks = myRepository.getAllTasks();
    }
}
