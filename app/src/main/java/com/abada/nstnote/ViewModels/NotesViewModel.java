package com.abada.nstnote.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.abada.nstnote.Events.OnCheckListener;
import com.abada.nstnote.Note;
import com.abada.nstnote.NoteAdapter;
import com.abada.nstnote.Repositories.IOManager;

import java.util.List;

public class NotesViewModel extends AndroidViewModel {
    public static OnCheckListener onCheckListener;
    private final MutableLiveData<List<Note>> notes;
    private final MutableLiveData<Integer> selectedCount;
    IOManager iom;

    {
        selectedCount = new MutableLiveData<>();
        onCheckListener = new OnCheckListener(selectedCount);
    }

    public NotesViewModel(Application application) {
        super(application);
        iom = IOManager.getInstance(application);
        selectedCount.setValue(0);
        notes = iom.getNotes();
    }

    public MutableLiveData<Integer> getSelectedCount() {
        return selectedCount;
    }

    public void deleteSelected(NoteAdapter noteAdapter) {
        final List<Note> selectedNotes = noteAdapter.deleteSelected();
        Note[] temp = new Note[selectedNotes.size()];
        selectedNotes.toArray(temp);
        iom.delete(temp);
        selectedCount.setValue(0);
    }

    public MutableLiveData<List<Note>> getNotes() {
        return notes;
    }
}
