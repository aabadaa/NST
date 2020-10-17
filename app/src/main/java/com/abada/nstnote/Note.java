package com.abada.nstnote;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "notes")
public class Note implements Parcelable {
    public static final String ID = "id";
    @Ignore
    public final String TAG = getClass().getName();
    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo(name = "header")
    private String header = "";
    @ColumnInfo(name = "body")
    private String body = "";
    @ColumnInfo(name = "date")
    private String date = "";
    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public Note() {}

    public Note(String body){
        this.body=body;
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
    protected Note(Parcel in) {
        header = in.readString();
        body = in.readString();
        date = in.readString();
        id = in.readLong();
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

    public void setDate() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-mm-dd hh:mm");
        date = sm.format(currentTime);
    }

    public String getDate() {
        return date;
    }

    public boolean isEmpty() {
        return header.isEmpty() && body.isEmpty();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(header);
        dest.writeString(body);
        dest.writeString(date);
        dest.writeLong(id);
    }

    @Override
    public String toString() {
        return "Note{" +
                " \"header\":" + header +
                ",\n\"body\":" + body + "\n" +
                "}";
    }

    @Override
    public boolean equals(Object other) {
        Note o = (Note) other;
        return id == o.id;
    }

    public boolean equalsIgnoreDate(Note other) {
        if (other == null)
            return false;
        return header.equals(other.getHeader()) && body.equals(other.getBody());
    }
}
