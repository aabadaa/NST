package com.abada.nstnote.UI;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.abada.nstnote.IOManager;
import com.abada.nstnote.Note;
import com.abada.nstnote.R;
import com.abada.nstnote.TileService;

public class OnFLyActivity extends AppCompatActivity {
    Button save, cancel;
    EditText body;
    static Note newNote;
    static String toastText = "saved";
    IOManager iom;
    View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_popup);
        v = getLayoutInflater().inflate(R.layout.activity_popup, null);
        setFinishOnTouchOutside(false);
        save = v.findViewById(R.id.save);
        cancel = v.findViewById(R.id.cancel);
        body = v.findViewById(R.id.note);
        iom = IOManager.getInstance(getApplication());
        iom.getNote().observe(this, note -> body.setText(note.getBody()));
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

    }/*
    public static void init(View v, Application application){
        Button  save = v.findViewById(R.id.save);
        Button cancel = v.findViewById(R.id.cancel);
        EditText body = v.findViewById(R.id.note);
        IOManager iom = IOManager.getInstance(application);
        iom.getNote().observeForever( note -> body.setText(note.getBody()));
        save.setOnClickListener(v1 -> {
            String b = body.getText().toString();
            String header = b;
            if (header.contains("\n"))
                header = header.substring(0, header.indexOf("\n"));
            if (header.length() > 30)
                header = header.substring(30);
            newNote = new Note(header, b);
            if (newNote.getBody().isEmpty()) {
                toastText = "canceled";
            } else {
                iom.insert(newNote);
            }
            iom.getNote().setValue(new Note());
        });
        cancel.setOnClickListener(v1 -> {
            toastText = "canceled";
            iom.getNote().setValue(new Note());

        });
    }*/
}