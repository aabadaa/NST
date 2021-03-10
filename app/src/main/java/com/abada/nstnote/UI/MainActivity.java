package com.abada.nstnote.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abada.nstnote.Events.FABOnLongClickListener;
import com.abada.nstnote.Events.NoteSimpleCallback;
import com.abada.nstnote.Events.OnCheckListener;
import com.abada.nstnote.Note;
import com.abada.nstnote.NoteAdapter;
import com.abada.nstnote.R;
import com.abada.nstnote.Tools;
import com.abada.nstnote.ViewModels.NotesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getName();
    //Views
    RecyclerView recyclerView;
    NoteAdapter noteAdapter;
    FloatingActionButton button;
    //Listeners
    View.OnClickListener addListener;
    View.OnClickListener delete;
    View.OnClickListener selectAll;
    View.OnClickListener itemClickListener;
    //Others
    NotesViewModel viewModel;

    {
        itemClickListener = v -> {
            int pos = recyclerView.getChildLayoutPosition(v);
            long id = noteAdapter.getItem(pos).id;
            Intent intent = new Intent(MainActivity.this, NoteActivity.class).putExtra(Note.ID, id);
            startActivity(intent);
        };
        addListener = v -> startActivity(new Intent(MainActivity.this, NoteActivity.class));
        delete = v -> Tools.askDialog(this, v1 -> viewModel.deleteSelected(noteAdapter), null);
        selectAll = v -> noteAdapter.checkALL();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        StoragePermissionGranted();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(NotesViewModel.class);
        button = findViewById(R.id.new_note_button);
        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(noteAdapter = new NoteAdapter(this, itemClickListener, new OnCheckListener(viewModel.getSelectedCount())));
        viewModel.getNotes().observe(this, notes -> noteAdapter.setList(notes));
        viewModel.getSelectedCount().observe(this, integer -> enableSelect(integer > 0));
        new ItemTouchHelper(new NoteSimpleCallback(noteAdapter, 0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)).attachToRecyclerView(recyclerView);
    }

    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_action);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                noteAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void enableSelect(boolean enable) {
        if (enable) {
            button.setOnClickListener(delete);
            button.setImageResource(R.drawable.delete_ic);
            button.setOnLongClickListener(new FABOnLongClickListener(this, selectAll, delete));
            button.setTag(true);
        } else {
            button.setOnClickListener(addListener);
            button.setImageResource(R.drawable.add_ic);
            button.setOnLongClickListener(null);
            button.setTag(null);
        }
    }

    public void StoragePermissionGranted() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission is granted");
        } else {
            Log.v(TAG, "Permission is revoked");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1234);
        }
    }
}
