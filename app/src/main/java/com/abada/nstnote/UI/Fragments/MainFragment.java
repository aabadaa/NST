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
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.abada.nstnote.Events.FABOnLongClickListener;
import com.abada.nstnote.Events.FABScrollListener;
import com.abada.nstnote.Events.NoteSimpleCallback;
import com.abada.nstnote.NoteAdapter;
import com.abada.nstnote.R;
import com.abada.nstnote.Repositories.IOManager;
import com.abada.nstnote.Utilities.BackupManager;
import com.abada.nstnote.Utilities.Tools;
import com.abada.nstnote.ViewModels.NotesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainFragment extends Fragment {
    public final String TAG = this.getClass().getName();
    //Views
    View view;
    RecyclerView recyclerView;
    FloatingActionButton button;
    SwipeRefreshLayout swipeRefreshLayout;
    //Others
    NotesViewModel viewModel;
    NoteAdapter noteAdapter;
    BackupManager b;
    public MainFragment() {
        super(R.layout.fragment_main);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        view = super.onCreateView(inflater, parent, savedInstanceState);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
                .create(NotesViewModel.class);
        button = view.findViewById(R.id.new_note_button);
        button.setOnClickListener(getAddListener());

        recyclerView = view.findViewById(R.id.rv);
        noteAdapter = new NoteAdapter(getActivity(), viewModel.getNotes(), getItemClickListener());
        recyclerView.setAdapter(noteAdapter);
        recyclerView.addOnScrollListener(new FABScrollListener(button));
        NoteSimpleCallback.setNoteCallback(viewModel, recyclerView);

        swipeRefreshLayout = view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.update());

        viewModel.getNotes().observe(requireActivity(), notes -> {
            noteAdapter.setList();
            swipeRefreshLayout.setRefreshing(false);
        });
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
        button.hide();
        if (enable) {
            button.setOnClickListener(getDeleteListener());
            button.setImageDrawable(getResources().getDrawable(R.drawable.delete_ic, null));
            button.setOnLongClickListener(new FABOnLongClickListener(getContext(), getSelectAllListener(), getDeleteListener()));
            button.setTag(true);
        } else {
            button.setOnClickListener(getAddListener());
            button.setImageDrawable(getResources().getDrawable(R.drawable.add_ic, null));
            button.setOnLongClickListener(null);
            button.setTag(null);
        }
        button.show();
    }

    private void toNoteFragment(long id) {
        NavDirections action = MainFragmentDirections.mainToNote().setId(id);
        Navigation.findNavController(view).navigate(action);
    }

    //listeners
    private View.OnClickListener getAddListener() {
        return v1 -> toNoteFragment(0);
    }

    private View.OnClickListener getDeleteListener() {
        return v -> Tools.getIns().askDialog(v1 -> viewModel.deleteSelected(noteAdapter), null);
    }

    private View.OnClickListener getSelectAllListener() {
        return v -> viewModel.checkALL();
    }

    private View.OnClickListener getItemClickListener() {
        return v1 -> {
            int pos = recyclerView.getChildLayoutPosition(v1);
            long id = noteAdapter.getItem(pos).id;
            toNoteFragment(id);
        };
    }
}
