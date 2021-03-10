package com.abada.nstnote;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abada.nstnote.Events.OnCheckListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> implements Filterable {
    private final Filter filter;
    private final OnCheckListener onCheckListener;
    private final View.OnClickListener itemClickListener;
    private final Context context;
    private List<Note> showedNotes = new LinkedList<>();
    private List<Note> allNotes;

    {
        filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Note> notesFilter = new ArrayList<>();
                if (constraint.toString().isEmpty())
                    notesFilter.addAll(allNotes);
                else
                    for (Note note : allNotes)
                        if (note.contains(constraint))
                            notesFilter.add(note);

                FilterResults filterResults = new FilterResults();
                filterResults.values = notesFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                showedNotes.clear();
                showedNotes.addAll((Collection<? extends Note>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public NoteAdapter(Context context, View.OnClickListener itemClickListener, OnCheckListener onCheckListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
        this.onCheckListener = onCheckListener;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.list_item, parent, false);
        v.setOnClickListener(itemClickListener);
        return new NoteHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note note = showedNotes.get(position);
        note.setOnCheckListener(onCheckListener);
        TextView header = holder.header;
        TextView date = holder.date;
        header.setText(note.getHeader());
        date.setText(note.getDate());
        holder.id = note.id;
        Drawable background;
        if (note.isChecked())
            background = context.getDrawable(R.drawable.item_background_checked);
        else
            background = context.getDrawable(R.drawable.item_background_unchecked);
        holder.itemView.setTag(note.isChecked());
        holder.itemView.setBackground(background);
    }

    @Override
    public int getItemCount() {
        return showedNotes.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public Context getContext() {
        return context;
    }

    public void remove(Note... notes) {
        try {
            for (Note note : notes) {
                int position = showedNotes.indexOf(note);
                showedNotes.remove(position);
                allNotes.remove(position);
                notifyItemRemoved(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateItem(Note note) {
        //todo: try to declare a sender intent to use this method

        int pos = showedNotes.indexOf(note);
        allNotes.set(pos, note);
        showedNotes.set(pos, note);
        notifyItemChanged(pos);
    }

    public void setList(List<Note> notes) {
        this.allNotes = notes;
        this.showedNotes = new ArrayList<>(notes);
        notifyDataSetChanged();
    }

    public Note getItem(int position) {
        return showedNotes.get(position);
    }

    public boolean checkAt(int pos) {
        Note note = showedNotes.get(pos);
        notifyItemRemoved(pos);
        note.check();
        notifyItemInserted(pos);
        return note.isChecked();
    }

    public void checkALL() {
        int x = onCheckListener.getSelectedCount();
        boolean allIsChecked = x == showedNotes.size();
        for (int i = 0; i < showedNotes.size(); i++)
            if (!showedNotes.get(i).isChecked() ^ allIsChecked)
                checkAt(i);
    }

    public List<Note> deleteSelected() {
        List<Note> selected = new LinkedList<>();
        Iterator<Note> it = showedNotes.iterator();
        while (it.hasNext()) {
            Note note = it.next();
            if (note.isChecked()) {
                selected.add(note);
            }
        }
        Note[] notes = new Note[selected.size()];
        selected.toArray(notes);
        remove(notes);
        return selected;
    }

    public static class NoteHolder extends RecyclerView.ViewHolder {
        public TextView header, date;
        long id;

        public NoteHolder(View v) {
            super(v);
            header = v.findViewById(R.id.list_item_header);
            date = v.findViewById(R.id.list_item_date);
        }
    }

}
