package com.abada.nstnote;

import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {
    List<Note> notes;
    MainActivity context;
    public final String TAG = getClass().getName();
    public NoteAdapter(MainActivity context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.list_item, parent, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = context.recyclerView.getChildLayoutPosition(v);
                long id = notes.get(pos).id;
                Intent intent = new Intent(context, NoteActivity.class).
                        putExtra(Note.ID, id);
                Log.i(TAG, "onClick: " + id);
                context.startActivity(intent);
            }
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

    public void addItem(Note note) {
        notes.add(note);
        int position = notes.indexOf(note);
        notifyItemInserted(position);

    }

    public void setItem(int index, Note note) {
        notes.set(index, note);
        notifyItemChanged(index);
    }

    public Note getItem(int position) {
        return notes.get(position);
    }

    public int indexOf(Note note) {
        return notes.indexOf(note);
    }

    public static class NoteHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView header, date;
        long id;

        public NoteHolder(View v) {
            super(v);
            header = v.findViewById(R.id.list_item_header);
            date = v.findViewById(R.id.list_item_date);
            v.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(getAdapterPosition(), Math.toIntExact(id), 0, "delete");
            menu.add(getAdapterPosition(), Math.toIntExact(id), 0, "copy");
            menu.add(getAdapterPosition(), Math.toIntExact(id), 0, "select all");
        }
    }
}
