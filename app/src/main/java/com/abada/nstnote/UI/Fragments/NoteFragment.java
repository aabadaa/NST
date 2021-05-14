package com.abada.nstnote.UI.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.abada.nstnote.Note;
import com.abada.nstnote.Utilities.State;
import com.abada.nstnote.ViewModels.NotesViewModel;
import com.abada.nstnote.databinding.FragmentNoteBinding;

public class NoteFragment extends Fragment {
    final String TAG = this.getClass().getName();
    FragmentNoteBinding binding;
    //others
    Note cur;
    long id = 0;
    NotesViewModel viewModel;
    NoteFragmentArgs args;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        binding = FragmentNoteBinding.inflate(inflater, parent, true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()).create(NotesViewModel.class);
        args = NoteFragmentArgs.fromBundle(requireArguments());
        id = args.getId();
        if (id != 0) {
            viewModel.getNoteById(id).observe(getViewLifecycleOwner(), note -> {
                cur = note;
                binding.noteHeader.setText(note.header);
                binding.noteBody.setText(note.body);
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Note newNote = new Note(binding.noteHeader.getText().toString(), binding.noteBody.getText().toString());
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
        newNote.body = binding.noteBody.getText().toString();
        newNote.header = binding.noteHeader.getText().toString();
        viewModel.edit(newNote, newNote.isEmpty() ? State.DELETE : State.INSERT);
    }
}
