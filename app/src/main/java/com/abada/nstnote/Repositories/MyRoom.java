package com.abada.nstnote.Repositories;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.abada.nstnote.Note;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Database(entities = Note.class, version = 2, exportSchema = false)
public abstract class MyRoom extends RoomDatabase {
    private static MyRoom instance;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE notes "
                    + " ADD COLUMN checker TEXT");
        }
    };

    public static MyRoom getInstance(Application application) {
        if (instance == null) {
            synchronized (MyRoom.class) {
                if (instance == null)
                    instance = Room.databaseBuilder(application, MyRoom.class, "notesDB")
                            .addMigrations(MIGRATION_1_2)
                            .build();
            }
        }
        return instance;
    }

    public abstract NoteDao getDao();

    public Future<Long> submit(Callable<Long> callable) {
        return executor.submit(callable);
    }

}
