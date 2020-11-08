package com.abada.nstnote.UI;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.abada.nstnote.IOManager;
import com.abada.nstnote.Note;
import com.abada.nstnote.R;
import com.abada.nstnote.TileService;

public class NoteActivity extends AppCompatActivity {
    final String TAG = this.getClass().getName();
    EditText header, body;
    Note curNote;
    IOManager iom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Log.i(TAG, "onCreate: ");

        header = findViewById(R.id.note_header);
        body = findViewById(R.id.note_text);
        iom = IOManager.getInstance(getApplication());
        iom.getNote().observe(this, note -> {
            if (note == null) {
                note = new Note();
                Log.i(TAG, "onCreate: null note reference observed");
            }
            header.setText(note.getHeader());
            body.setText(note.getBody());
            curNote = note;
        });

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
        TileService.clicked = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void save() {
        String header = this.header.getText().toString();
        String body = this.body.getText().toString();
        Note note = new Note(header, body);
        if (curNote != null)
            note.id = curNote.id;
        if (getIntent().getBooleanExtra(Note.NEW_NOTE, false)) {
            if (!note.equalsIgnoreDate(curNote) || curNote.isEmpty())
                if (note.isEmpty())
                    iom.deleteNote(note);
                else
                    iom.update(note);
        } else if (!note.isEmpty())
            iom.insert(note);
        iom.getNote().setValue(new Note());
    }
}