package com.abada.nstnote;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.abada.nstnote.Utilities.NoteDiffUtil;
import com.abada.nstnote.databinding.ListItemBinding;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {
    private final View.OnClickListener itemClickListener;
    private final Context context;
    private List<Note> showedNotes;
    private final MutableLiveData<String> query;
    private List<Note> allNotes;

    public NoteAdapter(Context context,
                       MutableLiveData<String> query,
                       View.OnClickListener itemClickListener) {
        this.context = context;
        this.query = query;
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

    public void setList(List<Note> notes) {
        allNotes = notes;
        showList();
    }

    private void showList() {
        List<Note> temp = new ArrayList<>(allNotes);
        temp.removeIf(note -> !note.contains(query.getValue()));
        NoteDiffUtil noteDiffUtil = new NoteDiffUtil(showedNotes, temp);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(noteDiffUtil);
        showedNotes = temp;
        diffResult.dispatchUpdatesTo(this);
    }

    public Note getItemByIndex(int position) {
        return showedNotes.get(position);
    }

    public static class NoteHolder extends RecyclerView.ViewHolder {
        public Note note;
        private final ListItemBinding binding;

        public NoteHolder(ListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Note note, Context context) {
            binding.listItemHeader.setText(note.header == null || note.header.isEmpty() ? note.body : note.header);
            binding.listItemDate.setText(note.date);
            this.note = note;
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
