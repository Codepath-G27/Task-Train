package com.eliasfang.calendify.data;

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

    public LiveData<List<Task>> getAllTasks() {
        return myAllTasks;
    }

    public void insert (Task task) {
        new insertAsyncTask(myTaskDao).execute(task);
    }

    public void deleteTask(Task task) {
        new deleteAsyncTask(myTaskDao).execute(task);
    }

    public void updateCompleted(Task task) {
        new updateAsyncTask(myTaskDao).execute(task);
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
    private class deleteAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao myAsyncTaskDao;
        public deleteAsyncTask(TaskDao myTaskDao) {
            myAsyncTaskDao = myTaskDao;
        }

        @Override
        protected Void doInBackground(final Task... params) {
            myAsyncTaskDao.deleteTask(params[0].getId());
            return null;
        }
    }
    private class updateAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao myAsyncTaskDao;
        public updateAsyncTask(TaskDao myTaskDao) {
            myAsyncTaskDao = myTaskDao;
        }

        @Override
        protected Void doInBackground(final Task... params) {
            myAsyncTaskDao.updateIsComplete(params[0].getId());
            return null;
        }
    }
}
