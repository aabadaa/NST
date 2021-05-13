package com.abada.nstnote.Repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.abada.nstnote.Note;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;

public class IOManager {
    public final String TAG = this.getClass().getName();
    private static IOManager instance;
    private final MyRoom room;
    private final NoteDao dao;
    private String query = "";
    private LiveData<List<Note>> notes;

    private IOManager(Application application) {
        room = MyRoom.getInstance(application);
        dao = room.getDao();
    }

    public static IOManager getInstance(Application application) {
        if (instance == null)
            instance = new IOManager(application);
        return instance;
    }

    public void getAll(String query) {
        this.query = query;
        notes = dao.getAll(query);
    }

    public LiveData<List<Note>> getNotes() {
        notes = dao.getAll(query);
        return notes;
    }

    public Future<List<Long>> insert(Note... notes) {
        Log.i(TAG, "insert: ");
        return (Future<List<Long>>) room.submit(() -> dao.insert(notes));
    }

    public void check(Note note) {
        note.check();
        room.submit(() -> dao.insert(note));
    }

    public void delete(Note... notes) {
        room.submit(() -> {
            dao.delete(notes);
            return null;
        });
        for (Note note : notes) {
            if (note == null) continue;
            Objects.requireNonNull(this.notes.getValue()).remove(note);
            if (note.isChecked())
                note.check();
        }
    }

    public void deleteAll() {
        room.submit(() -> {
            dao.deleteAll();
            return 0L;
        });
    }

    public LiveData<Note> getNoteById(long id) {
        return dao.getNoteById(id);
    }
}
