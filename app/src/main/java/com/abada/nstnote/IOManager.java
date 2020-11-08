package com.abada.nstnote;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.abada.nstnote.UI.MainActivity;

import java.util.List;

public class IOManager {
    public final String TAG = this.getClass().getName();
    private static IOManager instance;
    private final MyRoom room;
    private final NoteDao dao;
    private final MutableLiveData<Note> note;
    private final MutableLiveData<List<Note>> notes;
    private final MutableLiveData<Integer> selectedCount;
    private final Note.OnCheckListener onCheckListener;
    private NoteAdapter noteAdapter;
    private List<Note> list;

    {
        note = new MutableLiveData<>();
        notes = new MutableLiveData<>();
        selectedCount = new MutableLiveData<>();
        onCheckListener = new Note.OnCheckListener() {
            @Override
            public void onCheck(boolean isChecked) {
                int x = selectedCount.getValue() + (isChecked ? 1 : -1);
                selectedCount.setValue(x);
            }
        };
    }

    public static IOManager getInstance(Application application) {
        if (instance == null)
            instance = new IOManager(application);
        return instance;
    }

    private IOManager(Application application) {
        room = MyRoom.getInstance(application);
        dao = room.getDao();
        room.execute(() -> notes.postValue(dao.getAll()));
        selectedCount.setValue(0);
        notes.observeForever(notes -> {
            list = notes;
            if (noteAdapter != null)
                noteAdapter.setList(list);
        });
    }

    public NoteAdapter getNoteAdapter(MainActivity mainActivity) {
        if (noteAdapter == null || list.size() == 0) {
            room.execute(() -> notes.postValue(dao.getAll()));
        }
        if (noteAdapter == null) {
            noteAdapter = new NoteAdapter(mainActivity, onCheckListener);
        }
        return noteAdapter;
    }

    public MutableLiveData<Note> getNote() {
        return note;
    }

    public MutableLiveData<Integer> getSelectedCount() {
        return selectedCount;
    }

    public void insert(Note note) {
        Log.i(TAG, "writeNote: ");
        note.setDate();
        room.execute(() -> {
            note.id = dao.insert(note);
            list.add(note);
            notes.postValue(list);
        });
    }

    public void deleteNote(Note note) {
        room.execute(() -> dao.delete(note));
        noteAdapter.remove(note);
    }

    public void deleteSelected() {
        final List<Note> selectedNotes = noteAdapter.deleteSelected();
        room.execute(() -> dao.delete(selectedNotes));
        selectedCount.setValue(0);
    }

    public void update(Note note) {
        room.execute(() -> dao.update(note));
        noteAdapter.updateItem(note);
    }

    public void getNoteById(long id) {
        if (id == -1)
            note.setValue(new Note());
        else
            room.execute(() -> note.postValue(dao.getNoteById(id)));
    }
}
