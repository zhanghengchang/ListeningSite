package com.boll.audiobook.hear.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.litepal.LitePal;

/**
 * 下载内容保存
 * created by zoro at 2023/6/24
 */
public class DownloadProvider extends ContentProvider {

    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH); //用于匹配URI，并返回对应的操作编码
    private static final String AUTHORITES = "com.boll.audiobook.hear.contentprovider.downloadprovider";
    private static final int QUERY = 1; //查询

    static { //添加有效的URI及其编码
        sUriMatcher.addURI(AUTHORITES, "/query", QUERY);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int code = sUriMatcher.match(uri);
        if (code == QUERY) {
            Cursor cursor = LitePal.findBySQL("select * from downloadaudio");
            return cursor;
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

}
