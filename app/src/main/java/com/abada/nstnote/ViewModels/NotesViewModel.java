package com.abada.nstnote.ViewModels;

import android.app.Application;
import android.util.Log;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.abada.nstnote.Note;
import com.abada.nstnote.NoteAdapter;
import com.abada.nstnote.Repositories.IOManager;
import com.abada.nstnote.Utilities.NotesFilter;
import com.abada.nstnote.Utilities.Tools;

import java.util.List;

public class NotesViewModel extends AndroidViewModel implements Filterable {
    private final String TAG = getClass().getName();
    private final LiveData<List<Note>> notes;
    private final IOManager iom;
    private final Filter filter;


    public NotesViewModel(Application application) {
        super(application);
        iom = IOManager.getInstance(application);
        notes = iom.getNotes();
        filter = new NotesFilter(iom);
    }

    public void deleteSelected(NoteAdapter noteAdapter) {
        final List<Note> selectedNotes = noteAdapter.deleteSelected();
        Note[] temp = new Note[selectedNotes.size()];
        selectedNotes.toArray(temp);
        iom.delete(temp);
    }

    public void update() {
        Log.i(TAG, "update: ");
        iom.getAll("");
    }

    public LiveData<List<Note>> getNotes() {
        return notes;
    }

    public void checkAt(int index) {
        iom.check(notes.getValue().get(index));
    }

    public void checkALL() {
        Integer x = Tools.getIns().getCounter().getValue();
        List<Note> showedNotes = notes.getValue();
        assert showedNotes != null;
        boolean allIsChecked = x == showedNotes.size();
        for (int i = 0; i < showedNotes.size(); i++)
            if (!showedNotes.get(i).isChecked() ^ allIsChecked)
                checkAt(i);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}
