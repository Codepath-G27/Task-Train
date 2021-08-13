package com.eliasfang.calendify.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.eliasfang.calendify.models.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Task.class}, version = 1, exportSchema = false)
public abstract class TaskRoomDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();

    private static volatile TaskRoomDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static synchronized TaskRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    TaskRoomDatabase.class, "task_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

}