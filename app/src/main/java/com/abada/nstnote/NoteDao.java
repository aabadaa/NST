package com.abada.nstnote;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public abstract class NoteDao {
    @Query("select id,header,date from notes")
    public abstract List<Note> getAll();

    @Insert
    public abstract Long insert(Note note);

    @Delete
    public abstract void delete(Note note);

    @Delete
    public abstract void delete(List<Note> notes);

    @Update
    public abstract void update(Note note);

    @Query("select*from notes where id=:id")
    public abstract Note getNoteById(long id);

}
