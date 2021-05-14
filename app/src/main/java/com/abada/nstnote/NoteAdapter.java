package com.abada.nstnote;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.abada.nstnote.Utilities.NoteDiffUtil;
import com.abada.nstnote.databinding.ListItemBinding;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {
    private final View.OnClickListener itemClickListener;
    private final Context context;
    private List<Note> showedNotes;

    public NoteAdapter(Context context, LiveData<List<Note>> notes,
                       View.OnClickListener itemClickListener) {
        this.context = context;
        this.showedNotes = notes.getValue();
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ListItemBinding binding = ListItemBinding.inflate(layoutInflater, parent, false);
        binding.getRoot().setOnClickListener(itemClickListener);
        return new NoteHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note note = showedNotes.get(position);
        holder.bind(note, context);
    }

    @Override
    public int getItemCount() {
        return showedNotes == null ? 0 : showedNotes.size();
    }

    public Context getContext() {
        return context;
    }

    public void setList(List<Note> notes) {
        NoteDiffUtil noteDiffUtil = new NoteDiffUtil(showedNotes, notes);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(noteDiffUtil);
        this.showedNotes = notes;
        diffResult.dispatchUpdatesTo(this);
    }

    public Note getItem(int position) {
        return showedNotes.get(position);
    }

    public static class NoteHolder extends RecyclerView.ViewHolder {
        long id;
        private final ListItemBinding binding;

        public NoteHolder(ListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Note note, Context context) {
            binding.listItemHeader.setText(note.header == null || note.header.isEmpty() ? note.body : note.header);
            binding.listItemDate.setText(note.date);
            id = note.id;
            Drawable background;
            if (note.isChecked())
                background = context.getDrawable(R.drawable.item_background_checked);
            else
                background = context.getDrawable(R.drawable.item_background_unchecked);
            itemView.setTag(note.isChecked());
            itemView.setBackground(background);
        }
    }
}
