package com.abada.nstnote;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {
    List<Note> notes;
    MainActivity mainActivity;
    List<Note> selected = new ArrayList<>();
    public final String TAG = getClass().getName();

    public NoteAdapter(MainActivity context, List<Note> notes) {
        this.mainActivity = context;
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.list_item, parent, false);
        v.setOnClickListener(v1 -> {
            int pos = mainActivity.recyclerView.getChildLayoutPosition(v1);
            long id = notes.get(pos).id;
            Intent intent = new Intent(mainActivity, NoteActivity.class).
                    putExtra(Note.ID, id);
            Log.i(TAG, "onClick: " + id);
            mainActivity.startActivity(intent);
        });
        return new NoteHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note note = notes.get(position);
        TextView header = holder.header;
        TextView date = holder.date;
        header.setText(note.getHeader());
        date.setText(note.getDate());
        holder.id = note.id;
        if (note.isChecked()) {
            holder.itemView.setTag(true);
            holder.itemView.setBackground(mainActivity.getDrawable(R.drawable.item_background_checked));
        } else {
            holder.itemView.setTag(false);
            holder.itemView.setBackground(mainActivity.getDrawable(R.drawable.item_background_unchecked));
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void removeItem(Note note) {
        try {
            int position = notes.indexOf(note);
            notes.remove(position);

            notifyItemRemoved(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setItem(int index, Note note) {
        notes.set(index, note);
        notifyItemChanged(index);
    }

    public void setList(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    public Note getItem(int position) {
        return notes.get(position);
    }

    public int indexOf(Note note) {
        return notes.indexOf(note);
    }

    public void checkAt(int pos) {
        Note note = notes.get(pos);
        notifyItemRemoved(pos);
        note.check();
        if (note.isChecked())
            selected.add(note);
        else
            selected.remove(note);
        notes.set(pos, note);
        notifyItemInserted(pos);

        mainActivity.enableSelect(selected.size() > 0);
    }

    public void deleteSelected() {
        for (Note note : selected) removeItem(note);

        selected = new ArrayList<>();
        mainActivity.enableSelect(false);
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
