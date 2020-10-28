package com.abada.nstnote;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getName();
    public static boolean isOpend = false;
    NoteAdapter noteAdapter;
    FloatingActionButton button;
    RecyclerView recyclerView;
    IOManager iom;
    View.OnClickListener addListener = v -> startActivity(new Intent(MainActivity.this, NoteActivity.class));
    View.OnClickListener delete = v -> Tools.AskOption(MainActivity.this).show();
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isOpend = true;
        Log.i(TAG, "onCreate: ");
        StoragePermissionGranted();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.new_note_button);
        iom = IOManager.getInstance(getApplication());
        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = iom.getNoteAdapter(this);
        recyclerView.setAdapter(noteAdapter);
        registerForContextMenu(recyclerView);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        button.setOnClickListener(addListener);
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
        isOpend = false;
    }

    public void enableSelect(boolean enable) {
        if (enable) {
            button.setOnClickListener(delete);
            button.setImageResource(R.drawable.icondelete);
        } else {
            button.setOnClickListener(addListener);
            button.setImageResource(R.drawable.iconadd);
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
    }

}
