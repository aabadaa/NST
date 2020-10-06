package com.abada.nstnote;

import android.content.Intent;
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
                int position = context.recyclerView.getChildLayoutPosition(v);
                Intent intent = new Intent(context, NoteActivity.class).
                        putExtra(Note.POSITION, position);
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
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void removeItem(int position) {
        try {
            notes.remove(position);
            notifyItemRemoved(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addItem(int notePosition, Note note) {
        if (notePosition == -1) {
            notes.add(note);
            notifyDataSetChanged();
        } else {
            notes.add(notePosition, note);
            notifyItemInserted(notePosition);
        }
    }

    public Note getItem(int position) {
        return notes.get(position);
    }

    public static class NoteHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView header, date;

        public NoteHolder(View v) {
            super(v);
            header = v.findViewById(R.id.list_item_header);
            date = v.findViewById(R.id.list_item_date);
            v.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(getAdapterPosition(), v.getId(), 0, "delete");
            menu.add(getAdapterPosition(), v.getId(), 0, "copy");
            menu.add(getAdapterPosition(), v.getId(), 0, "select all");
        }
    }
}
