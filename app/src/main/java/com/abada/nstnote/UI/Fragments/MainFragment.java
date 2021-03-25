package com.abada.nstnote.UI.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.abada.nstnote.Events.FABOnLongClickListener;
import com.abada.nstnote.Events.NoteSimpleCallback;
import com.abada.nstnote.NoteAdapter;
import com.abada.nstnote.R;
import com.abada.nstnote.Utilities.Checkable;
import com.abada.nstnote.Utilities.Tools;
import com.abada.nstnote.ViewModels.NotesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainFragment extends Fragment {
    public final String TAG = this.getClass().getName();
    //Views
    View vvv;
    RecyclerView recyclerView;
    FloatingActionButton button;
    SwipeRefreshLayout swipeRefreshLayout;
    //Others
    NotesViewModel viewModel;
    NoteAdapter noteAdapter;


    public MainFragment() {
        super(R.layout.fragment_main);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        vvv = super.onCreateView(inflater, parent, savedInstanceState);
        setHasOptionsMenu(true);
        return vvv;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())
                .create(NotesViewModel.class);
        button = view.findViewById(R.id.new_note_button);
        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout = view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.update();
        });
        noteAdapter = new NoteAdapter(getActivity(), viewModel.getNotes(), getItemClickListener());
        viewModel.getNotes().observeForever(notes -> {
            setNoteAdapter();
            swipeRefreshLayout.setRefreshing(false);
        });
        Checkable.counter.observe(getActivity(), integer -> enableSelect(integer > 0));
        Checkable.counter.observeForever(i -> Log.i(TAG, "onViewCreated: " + i));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
    }

    public void enableSelect(boolean enable) {
        if (enable) {
            button.setOnClickListener(getDeleteListener());
            button.setImageResource(R.drawable.delete_ic);
            button.setOnLongClickListener(new FABOnLongClickListener(getContext(), getSelectAllListener(), getDeleteListener()));
            button.setTag(true);
        } else {
            button.setOnClickListener(getAddListener());
            button.setImageResource(R.drawable.add_ic);
            button.setOnLongClickListener(null);
            button.setTag(null);
        }
    }

    private void toNoteFragment(long id) {
        NavDirections action = MainFragmentDirections.mainToNote().setId(id);
        Navigation.findNavController(vvv).navigate(action);
    }

    private void setNoteAdapter() {
        recyclerView.setAdapter(noteAdapter);
        noteAdapter.setList(viewModel.getNotes().getValue());
        NoteSimpleCallback.setNoteCallback(viewModel, recyclerView);

    }

    //listeners
    private View.OnClickListener getAddListener() {
        return v1 -> toNoteFragment(0);
    }

    private View.OnClickListener getDeleteListener() {
        return v -> Tools.askDialog(getContext(), v1 -> viewModel.deleteSelected(noteAdapter), null);
    }

    private View.OnClickListener getSelectAllListener() {
        return v -> viewModel.checkALL(noteAdapter);
    }

    private View.OnClickListener getItemClickListener() {
        return v1 -> {
            int pos = recyclerView.getChildLayoutPosition(v1);
            long id = noteAdapter.getItem(pos).id;
            toNoteFragment(id);
        };
    }
}
