package com.abada.nstnote.Repositories;

import android.app.Application;
import android.util.Log;

import androidx.core.util.Pair;
import androidx.lifecycle.MutableLiveData;

import com.abada.nstnote.Note;
import com.abada.nstnote.Utilities.State;

import java.util.List;

public class IOManager {
    public final String TAG = this.getClass().getName();
    private static IOManager instance;
    private final MyRoom room;
    private final NoteDao dao;
    private final MutableLiveData<List<Note>> notes;
    private final MutableLiveData<Pair<State, Note>> saveChanges;
    private IOManager(Application application) {
        room = MyRoom.getInstance(application);
        dao = room.getDao();
        saveChanges = new MutableLiveData<>();
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
        return notes;
    }

    public void insert(Note note) {
        Log.i(TAG, "writeNote: ");
        saveChanges.setValue(new Pair(State.INSERT, note));
        note.setDate();
        room.execute(() -> {
            note.id = dao.insert(note);
        });

    }

    public void delete(Note... notes) {
        if (notes.length == 1)
            saveChanges.setValue(new Pair(State.DELETE, notes[0]));
        else {
            this.notes.getValue().remove(notes);
            this.notes.postValue(this.notes.getValue());
        }
        room.execute(() -> dao.delete(notes));
    }


    public void update(Note note) {
        saveChanges.setValue(new Pair(State.UPDATE, note));
        room.execute(() -> {
            dao.update(note);
        });
    }

    public MutableLiveData<Pair<State, Note>> getSaveChanges() {
        return saveChanges;
    }

    public void getNoteById(MutableLiveData<Note> note, long id) {
        if (id == -1)
            note.setValue(new Note());
        else
            room.execute(() -> note.postValue(dao.getNoteById(id)));
    }
}
