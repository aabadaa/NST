package com.abada.nstnote;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class IOManager {
    public final String TAG = this.getClass().getName();
    private static IOManager instance = null;
    private final MyRoom room;
    private final NoteDao dao;
    public final MutableLiveData<Note> note = new MutableLiveData<>();
    private final MutableLiveData<List<Note>> notes;
    private NoteAdapter noteAdapter;
    private List<Note> list;

    private IOManager(Application application) {
        room = MyRoom.getInstance(application);
        dao = room.getDao();
        getAll();
        notes = new MutableLiveData<>();
        notes.observeForever(notes -> {
            list = notes;
            if (noteAdapter != null)
                noteAdapter.setList(list);
        });
    }

    public static IOManager getInstance(Application application) {
        if (instance == null)
            instance = new IOManager(application);
        return instance;
    }

    public NoteAdapter getNoteAdapter(MainActivity mainActivity) {
        if (list == null)
            getAll();
        return noteAdapter = new NoteAdapter(mainActivity, list);
    }

    void insert(Note note) {
        Log.i(TAG, "writeNote: ");
        if (note.getDate().isEmpty())
            note.setDate();

        room.execute(() -> {
            note.id = dao.insert(note);
            list.add(note);
            notes.postValue(list);
        });
    }

    public void deleteNote(Note note) {
        room.execute(() -> dao.delete(note));
        noteAdapter.removeItem(note);
    }

    public void deleteSelected() {
        room.execute(() -> {
            List<Note> t = new ArrayList<>(noteAdapter.selected);
            dao.delete(t);
        });
        noteAdapter.deleteSelected();
    }

    public void update(Note note) {
        room.execute(() -> dao.update(note));
        int index = noteAdapter.indexOf(note);
        noteAdapter.setItem(index, note);
    }

    public void getNoteById(long id) {
        room.execute(() -> note.postValue(dao.getNoteById(id)));
    }

    public void getAll() {
        room.execute(() -> notes.postValue(dao.getAll()));
    }

}
