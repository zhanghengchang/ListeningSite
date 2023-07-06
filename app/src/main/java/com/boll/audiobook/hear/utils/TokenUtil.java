package com.boll.audiobook.hear.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * created by zoro at 2023/6/5
 */
public class TokenUtil {

    public static final String STR_REGISTER_CONTENTPROVIDER_DONAME = "provider.login_provider";

    public static String getUserLoginToken(Context ctx) {
        String strUri = String.format("content://%s/%s", STR_REGISTER_CONTENTPROVIDER_DONAME, "userToken");
        Uri uri = Uri.parse(strUri);

        ContentResolver resolver = ctx.getContentResolver();
//        String token = "token";  // note : 缺省token
        String token = Const.TOKEN_TEST;
        try {
            Cursor cursor = resolver.query(uri, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnNo = cursor.getColumnIndex("user_token");
                    if (columnNo >= 0) {
                        token = cursor.getString(columnNo);
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("apCnst", e.toString());
        }
        return token;
    }

}
