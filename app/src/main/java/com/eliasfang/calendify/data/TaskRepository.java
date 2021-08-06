package com.eliasfang.calendify.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.eliasfang.calendify.models.Task;

import java.util.List;

public class TaskRepository {

    private TaskDao myTaskDao;
    private LiveData<List<Task>> myAllTasks;

    public TaskRepository(Application application) {
        TaskRoomDatabase db = TaskRoomDatabase.getDatabase(application);
        myTaskDao = db.taskDao();
        myAllTasks = myTaskDao.getAllTasks();
    }


    public void insert (Task task) {
        new InsertAsyncTask(myTaskDao).execute(task);
    }

    public void delete(Task task) {
        new DeleteAsyncTask(myTaskDao).execute(task);
    }

    public void update(Task task) {
        new UpdateAsyncTask(myTaskDao).execute(task);
    }

    public void deleteAll(){
        new DeleteAllAsyncTask(myTaskDao).execute();
    }

    public LiveData<List<Task>> getAllTasks() {
        return myAllTasks;
    }


    //Asynchronous call to insert data to be database on a thread other than the main thread
    private static class InsertAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao myAsyncTaskDao;
        public InsertAsyncTask(TaskDao myTaskDao) {
            myAsyncTaskDao = myTaskDao;
        }

        @Override
        protected Void doInBackground(final Task... params) {
            myAsyncTaskDao.insert(params[0]);
            return null;
        }
    }


    private class DeleteAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao myAsyncTaskDao;
        public DeleteAsyncTask(TaskDao myTaskDao) {
            myAsyncTaskDao = myTaskDao;
        }

        @Override
        protected Void doInBackground(final Task... params) {
            myAsyncTaskDao.delete(params[0]);
            return null;
        }
    }


    private class UpdateAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao myAsyncTaskDao;
        public UpdateAsyncTask(TaskDao myTaskDao) {
            myAsyncTaskDao = myTaskDao;
        }

        @Override
        protected Void doInBackground(final Task... params) {
            myAsyncTaskDao.update(params[0]);
            return null;
        }
    }

    private class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private TaskDao myAsyncTaskDao;
        public DeleteAllAsyncTask(TaskDao myTaskDao) {
            myAsyncTaskDao = myTaskDao;
        }

        @Override
        protected Void doInBackground(Void... params) {
            myAsyncTaskDao.deleteAll();
            return null;
        }
    }
}
