package com.abada.nstnote.UI.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.abada.nstnote.Note;
import com.abada.nstnote.R;
import com.abada.nstnote.Utilities.State;
import com.abada.nstnote.ViewModels.SingleNoteViewModel;

public class NoteFragment extends Fragment {
    final String TAG = this.getClass().getName();
    //views
    View v;
    EditText header, body;
    //others
    Note cur;
    long id = 0;
    SingleNoteViewModel viewModel;
    NoteFragmentArgs args;

    public NoteFragment() {
        super(R.layout.fragment_note);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        return v = super.onCreateView(inflater, parent, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        header = view.findViewById(R.id.note_header);
        body = view.findViewById(R.id.note_text);
        viewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()).create(SingleNoteViewModel.class);
        args = NoteFragmentArgs.fromBundle(requireArguments());
        id = args.getId();
        if (id != 0) {
            viewModel.getNote(id).observe(getViewLifecycleOwner(), note -> {
                cur = note;
                header.setText(note.header);
                body.setText(note.body);
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Note newNote = new Note(header.getText().toString(), body.getText().toString());
        if (cur != null) {
            newNote.id = cur.id;
            newNote.date = cur.date;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        save();
    }

    void save() {
        Note newNote = new Note(cur);
        newNote.body = this.body.getText().toString();
        newNote.header = this.header.getText().toString();
        viewModel.edit(newNote, newNote.isEmpty() ? State.DELETE : State.INSERT);
    }
}
