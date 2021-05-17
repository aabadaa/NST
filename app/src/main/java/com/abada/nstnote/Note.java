package com.abada.nstnote;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.abada.nstnote.Utilities.Tools;

import java.io.Serializable;

@Entity
public class Note implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public Long id;
    public String header = "";
    public String body;
    public String date;
    @Ignore
    public Note(String body) {
        this.body = body;
        setDate();
    }

    @Ignore
    public Note(String header, String body) {
        this.header = header;
        this.body = body;
        setDate();
    }

    @Ignore
    public Note(String header, String body, long id) {
        this(header, body);
        setDate();
        this.id = id;
    }

    @Ignore
    public Note(@NonNull Note note) {
        header = note.header;
        body = note.body;
        date = note.date;
        id = note.id;
    }

    public Note(String header, String body, String date, long id) {
        this(header, body, id);
        this.date = date;
    }

    public void setDate() {
        date = Tools.getCurrentDate();
    }

    public boolean isChecked() {
        return Tools.isChecked(id);
    }

    public void check() {
        Tools.checkNote(id);
    }

    public boolean isEmpty() {
        return header.isEmpty() && body.isEmpty();
    }

    public boolean contains(String str) {
        return header.contains(str) || body.contains(str);
    }

    @NonNull
    @Override
    public String toString() {
        return "Note{" +
                ",[" + header + ']' +
                "{" + body + '}' +
                date;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Note))
            return false;
        Note o = (Note) other;
        return id != null && id.equals(o.id);
    }

    public boolean equalsIgnoreDate(Note other) {
        if (other == null)
            return false;
        return header.equals(other.header) && body.equals(other.body);
    }
}
