package com.abada.nstnote.ViewModels;

import android.app.Application;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.abada.nstnote.Events.OnCheckListener;
import com.abada.nstnote.Note;
import com.abada.nstnote.NoteAdapter;
import com.abada.nstnote.NotesFilter;
import com.abada.nstnote.Repositories.IOManager;

import java.util.List;

public class NotesViewModel extends AndroidViewModel implements Filterable {
    public static OnCheckListener onCheckListener;
    private static NoteAdapter noteAdapter;
    private final MutableLiveData<List<Note>> notes;
    private final MutableLiveData<Integer> selectedCount;
    private final IOManager iom;
    private Filter filter;

    {
        selectedCount = new MutableLiveData<>();
        onCheckListener = new OnCheckListener(selectedCount);
    }

    public NotesViewModel(Application application) {
        super(application);
        iom = IOManager.getInstance(application);
        selectedCount.setValue(0);
        notes = iom.getNotes();
        notes.observeForever(notes -> {
            if (noteAdapter != null) noteAdapter.setList(notes);
        });
        iom.getSaveChanges().observeForever(pair -> {
            switch (pair.first) {
                case INSERT:
                    noteAdapter.insertItem(pair.second);
                    break;
                case UPDATE:
                    noteAdapter.updateItem(pair.second);
                    break;
                case DELETE:
                    noteAdapter.remove(pair.second);
            }
        });
    }

    public MutableLiveData<Integer> getSelectedCount() {
        return selectedCount;
    }

    public NoteAdapter getNoteAdapter() {
        return noteAdapter;
    }

    public NotesViewModel setItemClickListener(View.OnClickListener clickListener) {
        if (noteAdapter == null)
            noteAdapter = new NoteAdapter(getApplication(), notes.getValue(), clickListener, new OnCheckListener(selectedCount));
        return this;
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

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new NotesFilter(notes.getValue(), notes);
        }
        return filter;
    }
}
