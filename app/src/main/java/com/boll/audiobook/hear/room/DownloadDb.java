package com.boll.audiobook.hear.room;

/**
 * created by zoro at 2023/8/5
 */

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DownloadAudio.class}, version = 1, exportSchema = false)
public abstract class DownloadDb extends RoomDatabase {

    public abstract DownloadDao downloadDao();

}
