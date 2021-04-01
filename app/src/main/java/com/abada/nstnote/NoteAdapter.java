package com.abada.nstnote;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.abada.nstnote.Utilities.Checkable;
import com.abada.nstnote.Utilities.NoteDiffUtil;

import java.util.LinkedList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {
    private final View.OnClickListener itemClickListener;
    private final Context context;
    private List<Note> showedNotes;
    private final LiveData<List<Note>> notesLiveData;

    public NoteAdapter(Context context, LiveData<List<Note>> notes,
                       View.OnClickListener itemClickListener) {
        this.context = context;
        this.notesLiveData = notes;
        this.showedNotes = notes.getValue();
        this.itemClickListener = itemClickListener;
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
        TextView header = holder.header;
        TextView date = holder.date;
        header.setText(note.header == null || note.header.isEmpty() ? note.body : note.header);
        date.setText(note.date);
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
        return showedNotes == null ? 0 : showedNotes.size();
    }

    public Context getContext() {
        return context;
    }

    public void remove(Note... notes) {
        try {
            for (Note note : notes) {
                int position = showedNotes.indexOf(note);
                showedNotes.remove(position);
                notifyItemRemoved(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setList() {
        List<Note> notes = notesLiveData.getValue();
        NoteDiffUtil noteDiffUtil = new NoteDiffUtil(showedNotes, notes);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(noteDiffUtil);
        this.showedNotes = notes;
        diffResult.dispatchUpdatesTo(this);
    }

    public Note getItem(int position) {
        return showedNotes.get(position);
    }

    public void checkAt(int pos) {
        Note note = showedNotes.get(pos);
        notifyItemRemoved(pos);
        note.check();
        notifyItemInserted(pos);
    }

    public void checkALL() {
        int x = Checkable.getCheckCounter();
        boolean allIsChecked = x == showedNotes.size();
        for (int i = 0; i < showedNotes.size(); i++)
            if (!showedNotes.get(i).isChecked() ^ allIsChecked)
                checkAt(i);
    }

    public List<Note> deleteSelected() {
        List<Note> selected = new LinkedList<>();
        for (Note note : showedNotes) {
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
