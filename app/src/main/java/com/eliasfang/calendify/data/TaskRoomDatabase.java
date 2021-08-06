package com.eliasfang.calendify.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.eliasfang.calendify.models.Task;

@Database(entities = {Task.class}, version = 2, exportSchema = false)
public abstract class TaskRoomDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();

    private static TaskRoomDatabase INSTANCE;

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