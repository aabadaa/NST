package com.abada.nstnote.Repositories;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.abada.nstnote.Note;

import java.util.List;

@Dao
public abstract class NoteDao {
    @Query("select * from Note where header like '%'||:query||'%' or body like '%'||:query||'%'")
    public abstract LiveData<List<Note>> getAll(String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract List<Long> insert(Note... notes);

    @Delete
    public abstract void delete(Note... note);

    @Query("select*from Note where id=:id")
    public abstract Note getNoteById(long id);

    @Query("delete from Note")
    public abstract void deleteAll();
}
