package com.boll.audiobook.hear.room;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface DownloadDao {

    @Insert
    void insertAll(DownloadAudio... downloadAudios);

    @Insert
    void insert(DownloadAudio downloadAudio);

    @Delete
    void delete(DownloadAudio downloadAudio);

    @Query("SELECT * FROM download")
    Cursor getAll();

}
