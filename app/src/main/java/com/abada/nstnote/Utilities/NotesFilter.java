package com.abada.nstnote.Utilities;

import android.widget.Filter;

import com.abada.nstnote.NoteAdapter;
import com.abada.nstnote.Repositories.IOManager;

public class NotesFilter extends Filter {
    private final IOManager iom;
    private final NoteAdapter noteAdapter;

    public NotesFilter(IOManager iom, NoteAdapter noteAdapter) {
        this.iom = iom;
        this.noteAdapter = noteAdapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        iom.getNotes(constraint.toString()).observeForever(noteAdapter::setList);
        return null;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
    }
}

