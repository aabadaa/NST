package com.abada.nstnote.Events;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.abada.nstnote.Note;
import com.abada.nstnote.NoteAdapter;
import com.abada.nstnote.Utilities.Tools;
import com.abada.nstnote.ViewModels.NotesViewModel;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class NoteSimpleCallback extends ItemTouchHelper.SimpleCallback {
    private final NoteAdapter noteAdapter;
    private final NotesViewModel notesViewModel;

    public NoteSimpleCallback(NotesViewModel notesViewModel, NoteAdapter noteAdapter, int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
        this.noteAdapter = noteAdapter;
        this.notesViewModel = notesViewModel;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder
            viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    public static void setNoteCallback(NotesViewModel notesViewModel, RecyclerView recyclerView) {

        new ItemTouchHelper(
                new NoteSimpleCallback(notesViewModel,
                        (NoteAdapter) recyclerView.getAdapter(),
                        0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT))
                .attachToRecyclerView(recyclerView);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {


        boolean checked = (Boolean) viewHolder.itemView.getTag();
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftLabel(checked ? "Un check" : "Check")
                .addSwipeRightLabel("Copy")
                .create()
                .decorate();

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int pos = viewHolder.getAdapterPosition();
        switch (direction) {
            case ItemTouchHelper.LEFT:
                noteAdapter.checkAt(pos);
                notesViewModel.checkAt(pos);
                break;
            case ItemTouchHelper.RIGHT:
                Note note = noteAdapter.getItem(pos);
                Tools.copy(noteAdapter.getContext(), note);
                noteAdapter.notifyItemChanged(pos);
                break;
        }
    }
}

