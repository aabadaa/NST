package com.abada.nstnote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;

public class NoteActivity extends AppCompatActivity {
    final String TAG = this.getClass().getName();
    EditText header, body;
    Note note = new Note();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Log.i(TAG, "onCreate: ");
        Intent intent = getIntent();
        header = findViewById(R.id.note_header);
        body = findViewById(R.id.note_text);

        if (intent.hasExtra("date")) {
            IOManager iom = new IOManager(this);
            try {
                note = iom.readNote(intent.getStringExtra("date"));
                if(note.getHeader()==null)
                    note=iom.readNote(intent.getStringExtra("header"));
                header.setText(note.getHeader());
                body.setText(note.getBody());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        save();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop: ");
        super.onStop();
    }

    void save() {
        Log.i(TAG, "save: ");
        IOManager iom = new IOManager(this);
        Intent res = new Intent();
        setResult(Activity.RESULT_OK, res);
        Note newNote=new Note(header.getText().toString(),body.getText().toString());
        if (!note.isEmpty() && (!newNote.getHeader().equals(note.getHeader()) || !newNote.getBody().equals(note.getBody()))) {

            iom.deleteNote(note);
            res.putExtra("deleted",  note);
        }
        if (newNote.getHeader().isEmpty() && newNote.getBody().isEmpty()) {
            setResult(Activity.RESULT_OK, res);
            finish();
            return;
        }
        if(note.getHeader().equals(newNote.getHeader()) && note.getBody().equals(newNote.getBody()))
        {
            setResult(Activity.RESULT_FIRST_USER, res);
            finish();
            return;
        }
        iom.writeNote(newNote);
        res.putExtra("new",newNote);
        setResult(Activity.RESULT_FIRST_USER, res);
        finish();
        Log.i(TAG, "save: finish");
    }
}