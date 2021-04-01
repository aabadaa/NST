package com.abada.nstnote.Utilities;

import android.widget.Filter;

import com.abada.nstnote.Repositories.IOManager;

public class NotesFilter extends Filter {
    private final IOManager iom;

    public NotesFilter(IOManager iom) {
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

