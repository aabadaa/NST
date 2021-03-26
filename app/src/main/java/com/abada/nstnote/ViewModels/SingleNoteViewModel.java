package com.abada.nstnote.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.abada.nstnote.Note;
import com.abada.nstnote.Repositories.IOManager;
import com.abada.nstnote.Utilities.State;

import java.util.concurrent.Future;

public class SingleNoteViewModel extends AndroidViewModel {
    private final MutableLiveData<Note> note;
    private final IOManager iom;

    public SingleNoteViewModel(Application application) {
        super(application);
        iom = IOManager.getInstance(application);
        note = new MutableLiveData<>();
    }

    public MutableLiveData<Note> getNoteLiveData() {
        return note;
    }

    public Future<Long> edit(State choice) {
        switch (choice) {
            case INSERT:
                return iom.insert(note.getValue());
            case DELETE:
                iom.delete(note.getValue());
                break;
            default:
                throw new RuntimeException("Wrong choice");
        }
        return null;
    }

    public void getNote(long id) {
        iom.getNoteById(note, id);
    }
}
