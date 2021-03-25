package com.abada.nstnote.Utilities;

import android.widget.Filter;

import androidx.lifecycle.MutableLiveData;

import com.abada.nstnote.Note;
import com.abada.nstnote.Repositories.IOManager;

import java.util.List;

public class NotesFilter extends Filter {
    private final MutableLiveData<List<Note>> notes;
    private final IOManager iom;

    public NotesFilter(MutableLiveData<List<Note>> notes, IOManager iom) {
        this.notes = notes;
        this.iom = iom;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        iom.getAll(constraint.toString());
        return null;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
    }
}

