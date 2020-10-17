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
    abstract List<Note> getAll();

    @Insert
    abstract Long insert(Note note);

    @Delete
    abstract void delete(Note note);

    @Update
    abstract void update(Note note);

    @Query("select*from notes where id=:id")
    abstract Note getNoteById(long id);


}
