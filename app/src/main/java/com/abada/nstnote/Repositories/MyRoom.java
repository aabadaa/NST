package com.abada.nstnote.Repositories;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.abada.nstnote.Note;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = Note.class, version = 7, exportSchema = false)
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
    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            try {
                database.execSQL("create table Note(id INTEGER primary key ,header TEXT, body TEXT,date TEXT)");
                database.execSQL("insert into Note(id,header,body,date) select id,header,body,date from notes");
                database.execSQL("drop table notes");
            } catch (Exception e) {
            }
        }
    };
    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("create table note2(id INTEGER primary key ,header TEXT, body TEXT,date TEXT,checked TEXT)");
            database.execSQL("insert into note2(id,header,body,date) select id,header,body,date from Note");
            database.execSQL("drop table Note");
            database.execSQL("alter table note2 rename to Note");

        }
    };
    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("create table note2(id INTEGER primary key ,header TEXT, body TEXT,date TEXT,checked INTEGER not null default(0))");
            database.execSQL("insert into note2(id,header,body,date) select id,header,body,date from Note");
            database.execSQL("drop table Note");
            database.execSQL("alter table note2 rename to Note");

        }
    };
    private static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {


        }
    };
    private static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("create table note2(id INTEGER primary key ,header TEXT, body TEXT,date TEXT)");
            database.execSQL("insert into note2(id,header,body,date) select id,header,body,date from Note");
            database.execSQL("drop table Note");
            database.execSQL("alter table note2 rename to Note");
        }
    };

    public static MyRoom getInstance(Application application) {
        if (instance == null) {
            synchronized (MyRoom.class) {
                if (instance == null)
                    instance = Room.databaseBuilder(application, MyRoom.class, "notesDB")
                            .addMigrations(MIGRATION_1_2)
                            .addMigrations(MIGRATION_2_3)
                            .addMigrations(MIGRATION_3_4)
                            .addMigrations(MIGRATION_4_5)
                            .addMigrations(MIGRATION_5_6)
                            .addMigrations(MIGRATION_6_7)
                            .build();
            }
        }
        return instance;
    }

    public abstract NoteDao getDao();

    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }

}
