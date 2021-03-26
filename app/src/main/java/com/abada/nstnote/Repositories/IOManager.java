package com.abada.nstnote.Repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.abada.nstnote.Note;

import java.util.List;
import java.util.concurrent.Future;

public class IOManager {
    public final String TAG = this.getClass().getName();
    private static IOManager instance;
    private final MyRoom room;
    private final NoteDao dao;
    private final MutableLiveData<List<Note>> notes;

    private IOManager(Application application) {
        room = MyRoom.getInstance(application);
        dao = room.getDao();
        notes = new MutableLiveData<>();
        getAll("");
    }

    public static IOManager getInstance(Application application) {
        if (instance == null)
            instance = new IOManager(application);
        return instance;
    }

    public void getAll(String query) {
        Log.i(TAG, "getAll: ");
        room.submit(() -> {
            List<Note> temp = notes.getValue();
            notes.postValue(dao.getAll(query));
            if (temp != null)
                for (Note n : temp)
                    if (n.isChecked())
                        n.check();
            return 0;
        });
    }

    public MutableLiveData<List<Note>> getNotes() {
        return notes;
    }

    public Future<Long> insert(Note note) {
        List<Note> temp = notes.getValue();
        if (!temp.contains(note))
            notes.getValue().add(note.setDate());
        else {
            int index = temp.indexOf(note);
            if (!note.equalsIgnoreDate(temp.get(index)))
                note.setDate();
            if (temp.get(index).isChecked())
                note.check();
            temp.set(temp.indexOf(note), note);
        }
        Log.i(TAG, "insert: ");
        return room.submit(() -> note.id = dao.insert(note));
    }

    public void check(Note note) {
        room.submit(() -> dao.insert(note));
    }

    public void delete(Note... notes) {
        room.submit(() -> {
            dao.delete(notes);
            return 0;
        });
        for (Note note : notes) {
            this.notes.getValue().remove(note);
            if (note.isChecked())
                note.check();
        }
    }

    public void getNoteById(MutableLiveData<Note> note, long id) {
        if (id == 0)
            note.setValue(new Note());
        else
            room.submit(() -> {
                note.postValue(dao.getNoteById(id));
                return id;
            });
    }
}
