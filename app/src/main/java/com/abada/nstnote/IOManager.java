package com.abada.nstnote;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

public class IOManager {
    Context context;
    public final String TAG = this.getClass().getName();

    public IOManager(Context context) {
        this.context = context;
    }

    void writeNote(Note note) {
        Log.i(TAG, "writeNote: ");
        if(note.getDate()==null || note.getDate().isEmpty())
            note.setDate();
        String filename = note.getDate();
        String fileContents = note.getHeader() + "\t" + note.getBody();
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Note readNote(String filename) throws FileNotFoundException {
        Log.i(TAG, "readNote: ");
        String header = null;
        FileInputStream fis = context.openFileInput(filename);
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            int index = line.indexOf('\t');
            if(index>=0) {
                header = line.substring(0, index);
                line = line.substring(index + 1);
            }
            else {
                header=filename;
            }
            while (line != null) {
                body.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
        } finally {
            return new Note(header, body.toString(), filename);
        }
    }

    public ArrayList<Note> getNotes() throws FileNotFoundException {
        Log.i(TAG, "getNotes: ");
        File directory = context.getFilesDir();
        File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files.length);
        String[] dates = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            Log.d("Files", "FileName:" + files[i].getName());
            dates[i] = files[i].getName();
        }
        ArrayList<Note> out = new ArrayList<>();
        for (int i = 0; i < dates.length; i++)
            out.add(readNote(dates[i]));
        return out;
    }

    public boolean deleteNote(Note note) {
        String dir = context.getFilesDir().getAbsolutePath();
        if(note==null || note.getHeader()==null || note.getDate()==null)
            return false;
        File f = new File(dir, note.getDate());
        if(f==null)
            f=new File(dir,note.getHeader());
        if(f==null)
            return false;
        boolean d0 = f.delete();
        Log.w("Delete Check", "File deleted: " + dir + "/myFile " + d0);
        return d0;
    }
    void fix(){
        File directory = context.getFilesDir();
        File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files.length);
        for (int i = 0; i < files.length; i++) {
            String filename = files[i].getName();
            if(filename.startsWith("T")){
                filename= new SimpleDateFormat("yy-mm-dd hh:mm").format(new Date(filename));
            File to=new File(directory,filename);
            files[i].renameTo(to);
            Log.i(TAG, "fix: "+files[i].getName()+"\n"+filename);}
        }
    }
}
