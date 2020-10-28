package com.abada.nstnote;

import android.annotation.SuppressLint;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "notes")
public class Note {
    public static final String ID = "id";
    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo(name = "header")
    private String header = "";
    @ColumnInfo(name = "body")
    private String body = "";
    @ColumnInfo(name = "date")
    private String date = "";
    @Ignore
    private boolean checked = false;

    public Note() {
    }

    public Note(String body) {
        this.body = body;
        setDate();
    }

    public Note(String header, String body) {
        this.header = header;
        this.body = body;
        setDate();
    }
    public Note(String header ,String body,String date){
        this(header,body);
        setDate(date);
    }
    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isChecked() {
        return checked;
    }

    public void check() {
        checked = !checked;
    }

    public void setDate() {
        Date currentTime = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        date = sm.format(currentTime);
    }

    public String getDate() {
        return date;
    }

    public boolean isEmpty() {
        return header.isEmpty() && body.isEmpty();
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", header='" + header + '\'' +
                ", body='" + body + '\'' +
                ", date='" + date + '\'' +
                '}';
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
