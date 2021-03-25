package com.abada.nstnote.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.abada.nstnote.Note;
import com.abada.nstnote.Repositories.IOManager;
import com.abada.nstnote.Utilities.State;

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

    public void edit(State choice) {
        switch (choice) {
            case INSERT:
                iom.insert(note.getValue());
                break;
            case DELETE:
                iom.delete(note.getValue());
                break;
            default:
                throw new RuntimeException("Wrong choice");
        }
    }

    public void getNote(long id) {
        iom.getNoteById(note, id);
    }

}
