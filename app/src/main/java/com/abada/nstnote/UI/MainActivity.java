package com.abada.nstnote.UI;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abada.nstnote.IOManager;
import com.abada.nstnote.Note;
import com.abada.nstnote.NoteAdapter;
import com.abada.nstnote.R;
import com.abada.nstnote.Tools;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {
    public static boolean isOpened = false;
    public final String TAG = this.getClass().getName();
    NoteAdapter noteAdapter;
    FloatingActionButton button;
    RecyclerView recyclerView;
    IOManager iom;
    View.OnClickListener addListener;
    View.OnClickListener delete;
    View.OnClickListener selectAll;
    View.OnClickListener itemClickListener;
    View.OnLongClickListener longClickListener;
    ItemTouchHelper itemTouchHelper;

    {
        addListener = v -> {
            startActivity(new Intent(MainActivity.this, NoteActivity.class).putExtra(Note.NEW_NOTE, false));
            iom.getNoteById(-1);
        };
        delete = v -> startActivity(new Intent(this, AskDeleteActivity.class));// Tools.AskOption(MainActivity.this).show();
        selectAll = v -> noteAdapter.checkALL();
        itemClickListener = v -> {
            int pos = recyclerView.getChildLayoutPosition(v);
            long id = noteAdapter.getItem(pos).id;
            Intent intent = new Intent(MainActivity.this, NoteActivity.class).putExtra(Note.NEW_NOTE, true);
            iom.getNoteById(id);
            startActivity(intent);
        };

        longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if ((boolean) button.getTag()) {
                    button.setOnClickListener(selectAll);
                    button.setImageResource(R.drawable.select_all_ic);
                    Toast.makeText(MainActivity.this, "Select all", Toast.LENGTH_SHORT).show();
                    button.setTag(false);
                } else {
                    button.setOnClickListener(delete);
                    button.setImageResource(R.drawable.delete_ic);
                    Toast.makeText(MainActivity.this, "Delete", Toast.LENGTH_SHORT).show();
                    button.setTag(true);
                }
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                return true;
            }
        };
        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        noteAdapter.checkAt(pos);
                        break;
                    case ItemTouchHelper.RIGHT:
                        Note note = noteAdapter.getItem(pos);
                        Tools.copy(MainActivity.this, note);
                        noteAdapter.notifyItemChanged(pos);
                        break;
                }
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
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isOpened = true;
        Log.i(TAG, "onCreate: ");
        StoragePermissionGranted();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iom = IOManager.getInstance(getApplication());

        button = findViewById(R.id.new_note_button);
        recyclerView = findViewById(R.id.rv);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = iom.getNoteAdapter(this);
        recyclerView.setAdapter(noteAdapter);
        registerForContextMenu(recyclerView);

        itemTouchHelper.attachToRecyclerView(recyclerView);

        iom.getSelectedCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                enableSelect(integer > 0);
            }
        });
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

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        isOpened = false;
    }

    public void enableSelect(boolean enable) {
        if (enable) {
            button.setOnClickListener(delete);
            button.setImageResource(R.drawable.delete_ic);
            button.setOnLongClickListener(longClickListener);
            button.setTag(true);
        } else {
            button.setOnClickListener(addListener);
            button.setImageResource(R.drawable.add_ic);
            button.setOnLongClickListener(null);
            button.setTag(null);
        }
    }

    public View.OnClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void StoragePermissionGranted() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission is granted");
        } else {
            Log.v(TAG, "Permission is revoked");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
}
