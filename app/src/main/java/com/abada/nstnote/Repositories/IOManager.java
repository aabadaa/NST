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
        getAll("");
    }

    public static IOManager getInstance(Application application) {
        if (instance == null)
            instance = new IOManager(application);
        return instance;
    }

    public void getAll(String query) {
        Log.i(TAG, "getAll: ");
        room.execute(() -> {
            List<Note> temp = notes.getValue();
            notes.postValue(dao.getAll(query));
            if (temp != null)
                for (Note n : temp)
                    if (n.isChecked())
                        n.check();
        });
    }

    public MutableLiveData<List<Note>> getNotes() {
        return notes;
    }

    public void insert(Note note) {
        note.setDate();
        room.execute(() -> note.id = dao.insert(note));
        List<Note> temp = notes.getValue();
        if (!temp.contains(note))
            notes.getValue().add(note);
        else {
            int index = temp.indexOf(note);
            if (temp.get(index).isChecked())
                note.check();
            temp.set(temp.indexOf(note), note);
        }
        Log.i(TAG, "insert: ");
    }

    public void check(Note note) {
        room.execute(() -> dao.insert(note));
    }

    public void delete(Note... notes) {
        room.execute(() -> dao.delete(notes));
        for (Note note : notes)
            this.notes.getValue().remove(note);
    }

    public void getNoteById(MutableLiveData<Note> note, long id) {
        if (id == 0)
            note.setValue(new Note());
        else
            room.execute(() -> note.postValue(dao.getNoteById(id)));
    }
}
