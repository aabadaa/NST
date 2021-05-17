package com.abada.nstnote.Repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.abada.nstnote.Note;
import com.abada.nstnote.Utilities.Tools;

import java.util.LinkedList;
import java.util.List;

public class IOManager {
    public final String TAG = this.getClass().getName();
    private static IOManager instance;
    private final MyRoom room;
    private final NoteDao dao;
    public final MutableLiveData<List<Long>> insertedIds;
    private final MutableLiveData<List<Note>> notes;
    private final MutableLiveData<Note> note;

    private IOManager(Application application) {
        room = MyRoom.getInstance(application);
        dao = room.getDao();
        insertedIds = new MutableLiveData<>();
        note = new MutableLiveData<>();
        notes = new MutableLiveData<>();
    }

    public static IOManager getInstance(Application application) {
        if (instance == null)
            instance = new IOManager(application);
        return instance;
    }

    public LiveData<List<Note>> getNotes() {
        dao.getAll().observeForever(notes::setValue);
        return notes;
    }

    public LiveData<List<Long>> insert(Note... notes) {
        Log.i(TAG, "insert: ");
        room.execute(() -> insertedIds.postValue(dao.insert(notes)));
        return insertedIds;
    }

    public void checkAll() {
        Integer x = Tools.getCounter().getValue();
        List<Note> showedNotes = notes.getValue();
        assert showedNotes != null;
        boolean allIsChecked = x == showedNotes.size();
        for (int i = 0; i < showedNotes.size(); i++)
            if (!showedNotes.get(i).isChecked() ^ allIsChecked)
                showedNotes.get(i).check();
    }

    public void delete(Note... notes) {
        room.execute(() -> dao.delete(notes));
        for (Note note : notes) {
            if (note == null) continue;
            if (note.isChecked())
                note.check();
        }
    }

    public void deleteChecked() {
        List<Note> selected = new LinkedList<>();
        for (Note note : notes.getValue())
            if (note.isChecked())
                selected.add(note);
        Note[] notes = new Note[selected.size()];
        selected.toArray(notes);
        delete(notes);
    }

    public void deleteAll() {
        room.execute(dao::deleteAll);
    }

    /**
     * this way is better than returning live data from dao
     * return live data is slow
     */
    public LiveData<Note> getNoteById(long id) {
        room.execute(() -> note.postValue(dao.getNoteById(id)));
        return note;
    }
}
