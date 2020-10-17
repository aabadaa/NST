package com.abada.nstnote;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class IOManager {
    public final String TAG = this.getClass().getName();
    private static IOManager instance = null;
    private final MyRoom room;
    private final NoteDao dao;
    private final Application application;
    private final MutableLiveData<Long> temp = new MutableLiveData<>();//used in update
    private final MutableLiveData<Note> note = new MutableLiveData<>();
    private NoteAdapter noteAdapter;
    private List<Note> list;

    private IOManager(Application application) {
        this.application = application;
        room = MyRoom.getInstance(application);
        dao = room.getDao();
        getAll();
    }

    public static IOManager getInstance(Application application) {
        if (instance == null)
            instance = new IOManager(application);
        return instance;
    }

    public NoteAdapter getNoteAdapter(MainActivity mainActivity) {
        getAll();
        return noteAdapter = new NoteAdapter(mainActivity, list);
    }

    void insert(Note note) {
        Log.i(TAG, "writeNote: ");
        if (note.getDate().isEmpty())
            note.setDate();

        room.execute(() -> temp.postValue(dao.insert(note)));
        temp.observeForever(new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                note.id = aLong;
                noteAdapter.addItem(note);
                //  list.add(note);
            }
        });
    }

    public void deleteNote(Note note) {
        room.execute(() -> dao.delete(note));
        noteAdapter.removeItem(note);
    }

    public void update(Note note) {
        room.execute(() -> dao.update(note));
        int index = noteAdapter.indexOf(note);
        noteAdapter.setItem(index, note);
    }

    public void getNoteById(long id) {
        room.execute(() -> note.postValue(dao.getNoteById(id)));
    }

    public void getAll() {
        room.execute(() -> {
            list = dao.getAll();
            for (int i = 0; i < list.size(); i++)
                if (list.get(i).getDate().startsWith("[0-9]") == false) {
                    list.get(i).setDate();
                    dao.update(list.get(i));
                }
        });
    }

    public MutableLiveData<Note> getNote() {
        return note;
    }

    Note readNote(String filename) throws FileNotFoundException {
        Log.i(TAG, "readNote: ");
        String header = null;
        FileInputStream fis = application.openFileInput(filename);
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            int index = line.indexOf('\t');
            if (index >= 0) {
                header = line.substring(0, index);
                line = line.substring(index + 1);
            }
            else {
                header=filename;
            }
            while (line != null) {
                body.append(line);
                body.append("\n");
                line = reader.readLine();
            }
        } finally {
            return new Note(header, body.toString(), filename);
        }
    }

    public ArrayList<Note> getNotesFiles() {
        Log.i(TAG, "getNotes: ");
        File directory = application.getFilesDir();
        File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files.length);
        String[] dates = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            Log.d("Files", "FileName:" + files[i].getName());
            dates[i] = files[i].getName();
        }
        ArrayList<Note> out = new ArrayList<>();
        for (int i = 0; i < dates.length; i++) {
            try {
                out.add(readNote(dates[i]));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < files.length; i++)
            files[i].delete();
        return out;
    }


    void fix() {
        ArrayList<Note> notes = getNotesFiles();
        Log.i(TAG, "fix: " + notes.size());
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            room.execute(() -> {
                dao.insert(note);
            });
        }
    }
}
