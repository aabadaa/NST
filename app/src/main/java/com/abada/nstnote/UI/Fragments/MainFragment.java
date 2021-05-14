package com.abada.nstnote.UI.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.abada.nstnote.Events.FABOnLongClickListener;
import com.abada.nstnote.Events.FABScrollListener;
import com.abada.nstnote.Events.NoteSimpleCallback;
import com.abada.nstnote.NoteAdapter;
import com.abada.nstnote.R;
import com.abada.nstnote.Repositories.IOManager;
import com.abada.nstnote.Utilities.BackupManager;
import com.abada.nstnote.Utilities.Tools;
import com.abada.nstnote.ViewModels.NotesViewModel;
import com.abada.nstnote.databinding.FragmentMainBinding;

public class MainFragment extends Fragment {
    public final String TAG = this.getClass().getName();
    private FragmentMainBinding binding;
    //Others
    NotesViewModel viewModel;
    NoteAdapter noteAdapter;
    BackupManager b;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        binding = FragmentMainBinding.inflate(inflater, parent, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
                .create(NotesViewModel.class);
        binding.FAB.setOnClickListener(getAddListener());

        noteAdapter = new NoteAdapter(getActivity(), viewModel.getNotes(""), getItemClickListener());
        viewModel.setNoteAdapter(noteAdapter);
        binding.rv.setAdapter(noteAdapter);
        binding.rv.addOnScrollListener(new FABScrollListener(binding.FAB));
        NoteSimpleCallback.setNoteCallback(viewModel, binding.rv);
        binding.refresh.setOnRefreshListener(this::update);
        update();
        b = new BackupManager(requireContext(), IOManager.getInstance(getActivity().getApplication()));
        Tools.createIns(getContext());
        Tools.getIns().getCounter().observe(requireActivity(), i -> {
            enableSelect(i > 0);
            Log.i(TAG, "onViewCreated: " + i);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_action);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.getFilter().filter(newText);
                return true;
            }
        });
        MenuItem backup = menu.findItem(R.id.menu_backup);
        MenuItem restore = menu.findItem(R.id.menu_restore);
        backup.setOnMenuItemClickListener(item -> {
            b.backUp();
            return true;
        });
        restore.setOnMenuItemClickListener(item -> {
            b.restore();
            return true;
        });
    }

    public void enableSelect(boolean enable) {
        binding.FAB.hide();
        if (enable) {
            binding.FAB.setOnClickListener(getDeleteListener());
            binding.FAB.setImageDrawable(getResources().getDrawable(R.drawable.delete_ic, null));
            binding.FAB.setOnLongClickListener(new FABOnLongClickListener(getContext(), getSelectAllListener(), getDeleteListener()));
            binding.FAB.setTag(true);
        } else {
            binding.FAB.setOnClickListener(getAddListener());
            binding.FAB.setImageDrawable(getResources().getDrawable(R.drawable.add_ic, null));
            binding.FAB.setOnLongClickListener(null);
            binding.FAB.setTag(null);
        }
        binding.FAB.show();
    }

    private void toNoteFragment(long id) {
        NavDirections action = MainFragmentDirections.mainToNote().setId(id);
        Navigation.findNavController(binding.getRoot()).navigate(action);
    }

    //listeners
    private View.OnClickListener getAddListener() {
        return v1 -> toNoteFragment(0);
    }

    private View.OnClickListener getDeleteListener() {
        return v -> Tools.getIns().askDialog(v1 -> viewModel.deleteChecked(), null);
    }

    private View.OnClickListener getSelectAllListener() {
        return v -> viewModel.checkALL();
    }

    private View.OnClickListener getItemClickListener() {
        return v1 -> {
            int pos = binding.rv.getChildLayoutPosition(v1);
            long id = noteAdapter.getItem(pos).id;
            toNoteFragment(id);
        };
    }

    private void update() {
        viewModel.getNotes("").observe(requireActivity(), notes -> {
            noteAdapter.setList(notes);
            binding.refresh.setRefreshing(false);
        });
    }
}
