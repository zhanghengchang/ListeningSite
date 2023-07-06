package com.boll.audiobook.hear.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.boll.audiobook.hear.service.PlayService;
import com.boll.audiobook.hear.utils.Const;

/**
 * 外部控制后台播放/暂停 上一曲/下一曲
 * created by zoro at 2023/6/24
 */
public class ControlPlayProvider extends ContentProvider {

    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH); //用于匹配URI，并返回对应的操作编码
    private static final String AUTHORITES = "com.boll.audiobook.hear.contentprovider.controlplayprovider";
    private static final int PLAY_OR_PAUSE = 1; //播放或暂停
    private static final int LEFT_OR_RIGHT = 2; //上一曲或下一曲
    private static final int GET_PLAY_STATE = 3; //获取当前播放状态
    private static final int GET_TITLE = 4; //获取当前播放标题
    private static final int GET_COVER_URL = 5; //获取当前播放封面url

    private static final String[] SUCCESS_COLUMN_NAME = {"success_state"};
    private static final String[] PLAY_COLUMN_NAME = {"play_state"};
    private static final String[] TITLE_COLUMN_NAME = {"title"};
    private static final String[] COVER_COLUMN_NAME = {"cover_url"};

    private static MatrixCursor matrixCursor;

    static { //添加有效的URI及其编码
        sUriMatcher.addURI(AUTHORITES, "/play_or_pause", PLAY_OR_PAUSE);
        sUriMatcher.addURI(AUTHORITES, "/left_or_right", LEFT_OR_RIGHT);
        sUriMatcher.addURI(AUTHORITES, "/get_play_state", GET_PLAY_STATE);
        sUriMatcher.addURI(AUTHORITES, "/get_title", GET_TITLE);
        sUriMatcher.addURI(AUTHORITES, "/get_coverUrl", GET_COVER_URL);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int code = sUriMatcher.match(uri);
        if (code == PLAY_OR_PAUSE && Const.isStartPlayService) {
            matrixCursor = new MatrixCursor(SUCCESS_COLUMN_NAME);
            int success_state = 0;
            if (sortOrder.equals("play")) {
                if (!PlayService.getInstance().isPlaying()) {
                    PlayService.getInstance().start();
                    success_state = 1;
                }
            } else if (sortOrder.equals("pause")) {
                if (PlayService.getInstance().isPlaying()) {
                    PlayService.getInstance().pause();
                    success_state = 1;
                }
            }
            matrixCursor.addRow(new Object[]{success_state});
            return matrixCursor;
        } else if (code == LEFT_OR_RIGHT && Const.isStartPlayService) {
            matrixCursor = new MatrixCursor(SUCCESS_COLUMN_NAME);
            int success_state = 0;
            if (sortOrder.equals("left")) {
                PlayService.getInstance().playLeft();
                success_state = 1;
            } else if (sortOrder.equals("right")) {
                PlayService.getInstance().playRight();
                success_state = 1;
            }
            matrixCursor.addRow(new Object[]{success_state});
            return matrixCursor;
        } else if (code == GET_PLAY_STATE && Const.isStartPlayService) {
            matrixCursor = new MatrixCursor(PLAY_COLUMN_NAME);
            int play_state = 0;
            if (PlayService.getInstance().isPlaying()) {
                play_state = 1;
            } else {
                play_state = 0;
            }
            matrixCursor.addRow(new Object[]{play_state});
            return matrixCursor;
        } else if (code == GET_TITLE && Const.isStartPlayService){
            matrixCursor = new MatrixCursor(TITLE_COLUMN_NAME);
            matrixCursor.addRow(new Object[]{Const.CURRENT_TITLE});
            return matrixCursor;
        } else if (code == GET_COVER_URL && Const.isStartPlayService){
            matrixCursor = new MatrixCursor(COVER_COLUMN_NAME);
            matrixCursor.addRow(new Object[]{Const.CURRENT_COVER_URL});
            return matrixCursor;
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
