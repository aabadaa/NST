package com.abada.nstnote.ViewModels;

import android.app.Application;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.abada.nstnote.Note;
import com.abada.nstnote.NoteAdapter;
import com.abada.nstnote.Repositories.IOManager;
import com.abada.nstnote.Utilities.NotesFilter;
import com.abada.nstnote.Utilities.State;

import java.util.List;

public class NotesViewModel extends AndroidViewModel implements Filterable {
    private final String TAG = getClass().getName();
    private final IOManager iom;
    private Filter filter;


    public NotesViewModel(Application application) {
        super(application);
        iom = IOManager.getInstance(application);
    }

    public void deleteChecked() {
        iom.deleteChecked();
    }

    public LiveData<List<Note>> getNotes(String query) {
        return iom.getNotes(query);
    }

    public void checkAt(int index) {
        iom.checkAt(index);
    }

    public void checkALL() {
        iom.checkAll();
    }

    public LiveData<List<Long>> edit(Note note, State choice) {
        switch (choice) {
            case INSERT:
                return iom.insert(note);
            case DELETE:
                iom.delete(note);
                break;
            default:
                throw new RuntimeException("Wrong choice");
        }
        return null;
    }

    public LiveData<Note> getNoteById(long id) {
        return iom.getNoteById(id);
    }

    public void setNoteAdapter(NoteAdapter noteAdapter) {
        filter = new NotesFilter(iom, noteAdapter);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

}
