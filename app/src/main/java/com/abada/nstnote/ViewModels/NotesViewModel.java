package com.abada.nstnote.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.abada.nstnote.Note;
import com.abada.nstnote.Repositories.IOManager;
import com.abada.nstnote.Utilities.State;

import java.util.List;

public class NotesViewModel extends AndroidViewModel {
    private final String TAG = getClass().getName();
    private final IOManager iom;
    private final MutableLiveData<String> query;
    private Observer<List<Note>> observer;

    public NotesViewModel(Application application) {
        super(application);
        iom = IOManager.getInstance(application);
        query = new MutableLiveData<>("");
    }

    public void deleteChecked() {
        iom.deleteChecked();
    }

    public void getNotes(String query) {
        iom.getNotes().observeForever(observer);
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

    public MutableLiveData<String> getFilterQuery() {
        return query;
    }

    public void observe(Observer<List<Note>> observer) {
        this.observer = observer;
        query.observeForever(this::getNotes);

    }

}
