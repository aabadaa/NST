package com.abada.nstnote;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getName();
    ListView listView;
    ArrayList<Note> noteList;
    NoteAdapter noteAdapter;
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LinearLayout ll = (LinearLayout) view;
            TextView tv = (TextView) ll.getChildAt(1);
            if(tv.getText().toString().isEmpty())
                tv=(TextView)ll.getChildAt(0);
            Intent noteIntent = new Intent(MainActivity.this, NoteActivity.class)
                    .putExtra("date",noteList.get(position).getDate())
                    .putExtra("header",noteList.get(position).getHeader());
            MainActivity.this.startActivityForResult(noteIntent, 1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        StoragePermissionGranted();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((Button) findViewById(R.id.new_note_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, NoteActivity.class), 1);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");

        listView = findViewById(R.id.list_view);
        try {
            noteList = new IOManager(this).getNotes();
            noteAdapter = new NoteAdapter(this, noteList);
            listView.setAdapter(noteAdapter);
            listView.setOnItemClickListener(onItemClickListener);
            registerForContextMenu(listView);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.add((int)info.targetView.getTag(), v.getId(), 0, "delete");
        menu.add((int)info.targetView.getTag(), v.getId(), 0, "copy");
        menu.add((int)info.targetView.getTag(), v.getId(), 0,"select all");
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
                Toast.makeText(this,"select all",Toast.LENGTH_SHORT).show();
                for(int i=0;i<listView.getChildCount();i++)
                    listView.setItemChecked(i,true);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: ");
        if (resultCode != Activity.RESULT_OK)
            return;
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 1) {
            if (data.hasExtra("deleted")) ;
            noteList.remove(data.getParcelableArrayExtra("deleted"));
            if (data.hasExtra("new"))
                noteList.add((Note) data.getParcelableExtra("new"));
            listView.setAdapter(new NoteAdapter(this, noteList));
        }
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