package com.abada.nstnote.UI;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.abada.nstnote.IOManager;
import com.abada.nstnote.Note;
import com.abada.nstnote.R;
import com.abada.nstnote.TileService;

public class OnFLyActivity extends AppCompatActivity {
    Button save, cancel;
    EditText body;
    Note newNote;
    String toastText = "saved";
    IOManager iom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        setFinishOnTouchOutside(false);
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
        body = findViewById(R.id.note);
        iom = IOManager.getInstance(getApplication());
        iom.getNote().observe(this, new Observer<Note>() {
            @Override
            public void onChanged(Note note) {
                if (note == null)
                    return;
                body.setText(note.getBody());
            }
        });
        save.setOnClickListener(v -> {
            save();
            finish();
        });
        cancel.setOnClickListener(v -> {
            toastText = "canceled";
            iom.getNote().setValue(new Note());
            finish();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        TileService.clicked = false;
    }

    private void save() {
        String body = this.body.getText().toString();
        String header = body;
        if (header.contains("\n"))
            header = header.substring(0, header.indexOf("\n"));
        if (header.length() > 30)
            header = header.substring(30);
        newNote = new Note(header, body);
        if (newNote.getBody().isEmpty()) {
            toastText = "canceled";
        } else {
            iom.insert(newNote);
        }
        iom.getNote().setValue(new Note());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        iom.getNote().setValue(new Note(body.getText().toString()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();

    }
}