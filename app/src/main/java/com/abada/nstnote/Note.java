package com.abada.nstnote;

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
    public String body = "";
    public String date;
    public boolean checked = false;

    @Ignore
    public Note() {
        setDate();
    }

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

    public Note(String header, String body, String date, long id) {
        this(header, body, id);
        this.date = date;
    }

    public Note setDate() {
        date = Tools.getIns().getCurrentDate();
        return this;
    }

    public boolean isChecked() {
        return checked;
    }

    public void check() {
        checked = !checked;
        Tools.getIns().checkOne(isChecked());
    }

    public boolean isEmpty() {
        return header.isEmpty() && body.isEmpty();
    }

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
        return id != null && o.id != null && id.equals(o.id);
    }

    public boolean equalsIgnoreDate(Note other) {
        if (other == null)
            return false;
        return header.equals(other.header) && body.equals(other.body);
    }
}
