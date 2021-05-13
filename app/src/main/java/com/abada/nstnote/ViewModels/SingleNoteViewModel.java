package com.abada.nstnote.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.abada.nstnote.Note;
import com.abada.nstnote.Repositories.IOManager;
import com.abada.nstnote.Utilities.State;

import java.util.List;
import java.util.concurrent.Future;

public class SingleNoteViewModel extends AndroidViewModel {
    private final IOManager iom;

    public SingleNoteViewModel(Application application) {
        super(application);
        iom = IOManager.getInstance(application);
    }

    public Future<List<Long>> edit(Note note, State choice) {
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

    public LiveData<Note> getNote(long id) {
        return iom.getNoteById(id);
    }
}
