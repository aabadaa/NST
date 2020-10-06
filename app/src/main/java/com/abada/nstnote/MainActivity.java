package com.abada.nstnote;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getName();
    public static NoteAdapter noteAdapter;
    RecyclerView recyclerView;
    ArrayList<Note> noteList;
    RecyclerView.LayoutManager layoutManager;
    IOManager iom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        StoragePermissionGranted();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((Button) findViewById(R.id.new_note_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
            }
        });
        iom = new IOManager(this);
        recyclerView = findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(this);
        noteList = iom.getNotes();
        noteAdapter = new NoteAdapter(this, noteList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(noteAdapter);
        registerForContextMenu(recyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.notifyDataSetChanged();
        Log.i(TAG, "onStart: ");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        //return super.onContextItemSelected(item);
        switch (item.getTitle().toString()) {
            case "delete":
               Tools.AskOption(this,noteAdapter,item.getGroupId()).show();
                break;
            case "copy":
                Tools.copy(this,noteList.get(item.getGroupId()));
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
       // new IOManager(this).fix();
    }

}