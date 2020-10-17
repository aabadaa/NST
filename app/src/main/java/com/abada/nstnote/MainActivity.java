package com.abada.nstnote;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getName();
    public static boolean isOpend = false;
    NoteAdapter noteAdapter;
    RecyclerView recyclerView;
    IOManager iom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isOpend = true;
        Log.i(TAG, "onCreate: ");
        StoragePermissionGranted();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.new_note_button).setOnClickListener((v) -> {
            startActivity(new Intent(MainActivity.this, NoteActivity.class));
        });
        iom = IOManager.getInstance(getApplication());
        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = iom.getNoteAdapter(this);
        recyclerView.setAdapter(noteAdapter);
        registerForContextMenu(recyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Log.i(TAG, "onContextItemSelected: " + item.getGroupId());
        switch (item.getTitle().toString()) {
            case "delete":
               Tools.AskOption(this,noteAdapter,item.getGroupId()).show();
                break;
            case "copy":
                Tools.copy(this, noteAdapter.getItem(item.getGroupId()));
                break;
            case "select all":
                Toast.makeText(this, "No select all haha", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
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

    public void StoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else  //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
        IOManager.getInstance(getApplication()).fix();
    }

}