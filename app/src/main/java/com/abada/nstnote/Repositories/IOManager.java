package com.abada.nstnote.Repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.abada.nstnote.Note;

import java.util.List;

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
        getAll();
    }

    public static IOManager getInstance(Application application) {
        if (instance == null)
            instance = new IOManager(application);
        return instance;
    }

    private void getAll() {
        room.execute(() -> notes.postValue(dao.getAll()));
    }

    public MutableLiveData<List<Note>> getNotes() {
        getAll();
        return notes;
    }

    public void insert(Note note) {
        Log.i(TAG, "writeNote: ");
        note.setDate();
        room.execute(() -> {
            note.id = dao.insert(note);
            notes.getValue().add(note);
            notes.postValue(notes.getValue());
        });

    }

    public void delete(Note... notes) {
        room.execute(() -> {
            dao.delete(notes);
            this.notes.getValue().remove(notes);
            this.notes.postValue(this.notes.getValue());
        });
    }


    public void update(Note note) {
        room.execute(() -> {
            dao.update(note);
            int index = notes.getValue().indexOf(note);
            notes.getValue().set(index, note);
            notes.postValue(notes.getValue());
        });
    }

    public void getNoteById(MutableLiveData<Note> note, long id) {
        if (id == -1)
            note.setValue(new Note());
        else
            room.execute(() -> note.postValue(dao.getNoteById(id)));
    }
}
