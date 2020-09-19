package com.abada.nstnote;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Note implements Parcelable {
    private String header="", body="";
    private String date;
    boolean checked=false;
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
    }

    public String getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    public void setDate(String date) {
        this.date=date;
    }
    public void setDate(){
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sm=new SimpleDateFormat("yy-mm-dd hh:mm");
        date= sm.format(currentTime);
    }
    public String getDate(){
        return date;
    }

    public boolean isEmpty(){
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
    }
    @Override
    public String toString(){
        return header+"\t\n"+body;
    }
}
