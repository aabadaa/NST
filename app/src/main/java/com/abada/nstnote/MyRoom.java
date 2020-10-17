package com.abada.nstnote;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = Note.class, version = 1, exportSchema = false)
public abstract class MyRoom extends RoomDatabase {
    private static MyRoom instance;
    private final ExecutorService excutor = Executors.newFixedThreadPool(4);

    public static MyRoom getInstance(Application application) {
        if (instance == null) {
            synchronized (MyRoom.class) {
                if (instance == null)
                    instance = Room.databaseBuilder(application, MyRoom.class, "notesDB").build();
            }
        }
        return instance;
    }

    public abstract NoteDao getDao();

    public void execute(Runnable runnable) {
        excutor.execute(runnable);
    }

}
