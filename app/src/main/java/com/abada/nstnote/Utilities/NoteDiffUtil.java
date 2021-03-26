package com.abada.nstnote.Utilities;

import androidx.recyclerview.widget.DiffUtil;

import com.abada.nstnote.Note;

import java.util.List;

public class NoteDiffUtil extends DiffUtil.Callback {
    final private List<Note> old, cur;

    public NoteDiffUtil(List<Note> old, List<Note> cur) {
        this.old = old;
        this.cur = cur;
    }

    @Override
    public int getOldListSize() {
        return old == null ? 0 : old.size();
    }

    @Override
    public int getNewListSize() {
        return cur == null ? 0 : cur.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return old.get(oldItemPosition).equals(cur.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return old.get(oldItemPosition).equalsIgnoreDate(cur.get(newItemPosition));
    }
}
