package com.eliasfang.calendify;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskRepository {

    private TaskDao myTaskDao;
    private LiveData<List<Task>> myAllTasks;

    public TaskRepository(Application application) {
        TaskRoomDatabase db = TaskRoomDatabase.getDatabase(application);
        myTaskDao = db.taskDao();
        myAllTasks = myTaskDao.getAllTasks();
    }

    LiveData<List<Task>> getAllTasks() {
        return myAllTasks;
    }

    public void insert (Task task) {
        new insertAsyncTask(myTaskDao).execute(task);
    }


    private class insertAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao myAsyncTaskDao;
        public insertAsyncTask(TaskDao myTaskDao) {
            myAsyncTaskDao = myTaskDao;
        }

        @Override
        protected Void doInBackground(final Task... params) {
            myAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
