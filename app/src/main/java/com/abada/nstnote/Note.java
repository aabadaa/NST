package com.abada.nstnote;

import android.annotation.SuppressLint;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.abada.nstnote.Utilities.Checkable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "notes")
public class Note extends Checkable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;
    @ColumnInfo(name = "header")
    public String header = "";
    @ColumnInfo(name = "body")
    public String body = "";
    @ColumnInfo(name = "date")
    public String date;

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
        Date currentTime = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        date = sm.format(currentTime);
        return this;
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
        return id == o.id;
    }

    public boolean equalsIgnoreDate(Note other) {
        if (other == null)
            return false;
        return header.equals(other.header) && body.equals(other.body);
    }
}
