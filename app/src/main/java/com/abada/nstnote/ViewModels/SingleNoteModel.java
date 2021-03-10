package com.abada.nstnote.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.abada.nstnote.Note;
import com.abada.nstnote.Repositories.IOManager;

public class SingleNoteModel extends AndroidViewModel {
    public static final int UPDATE = 0, INSERT = 1, DELETE = 2;
    private final MutableLiveData<Note> note;
    IOManager iom;

    public SingleNoteModel(Application application) {
        super(application);
        note = new MutableLiveData<>();
        iom = IOManager.getInstance(application);
    }

    public MutableLiveData<Note> getNote() {
        return note;
    }

    public void edit(int choice) {
        switch (choice) {
            case UPDATE:
                iom.update(note.getValue());
                break;
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
