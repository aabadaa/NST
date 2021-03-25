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
    private String header = "";
    @ColumnInfo(name = "body")
    private String body = "";
    @ColumnInfo(name = "date")
    private String date;

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

    public Note(String header, String body, long id) {
        this(header, body);
        setDate();
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    public String getDate() {
        return date;
    }


    public void setHeader(String header) {
        this.header = header;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDate() {
        Date currentTime = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        date = sm.format(currentTime);
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
        return header.equals(other.getHeader()) && body.equals(other.getBody());
    }
}
