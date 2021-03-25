package com.abada.nstnote.Repositories;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.abada.nstnote.Note;

import java.util.List;

@Dao
public abstract class NoteDao {
    @Query("select * from notes where header like '%'||:query||'%' or body like '%'||:query||'%'")
    public abstract List<Note> getAll(String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Long insert(Note note);

    @Delete
    public abstract void delete(Note... note);

    @Query("select*from notes where id=:id")
    public abstract Note getNoteById(long id);

}
