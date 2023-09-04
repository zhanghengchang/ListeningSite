package com.boll.audiobook.hear.utils;

import android.os.Environment;

import java.io.File;

/**
 * @author Created by zoro on 2022/03/25
 */
public class Const {

    //博尔BaseUrl
    public static final String LISTENER_BASEURL = "http://audiobookapi.bolledu.net/audiobook/";

    //心喜阅BaseUrl
//    public static final String LISTENER_BASEURL = "https://audiobookapi.dolphinmedia.cn/";

    //博尔词典url
    public static final String DICTIONARY_BASEURL = "http://scanpen.bolledu.net/";

    //词典调鉴权接口的key
    public static String ACCESS_KEY = "xingxiyue202306011707ogm9aixr18470m";

    //默认token
    public static final String TOKEN_TEST = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6Im" +
            "FiM2UwZjI4LWY2NjctNDI1ZS05MzUzLTU1ZmRjNjg1NDg0YSJ9.DVHIigwtESrYmfPdwrRLalWFo25jNgavEs" +
            "-U7irtr6FTcc1mw0Bk_AirLk6XXYwhPalkFdU-J_VzB3K6FsceOg";

    //请求头ua
    public static String UA;

    //mp3资源文件存放路径
    public static String MP3PATH = "data" + Environment.getDataDirectory().getAbsolutePath() + File.separator
                + "com.boll.audiobook.hear" + File.separator + "ListeningSite" + File.separator + "mp3" + File.separator;

    //lrc字幕文件存放路径
    public static String LRCPATH = "data" + Environment.getDataDirectory().getAbsolutePath() + File.separator
            + "com.boll.audiobook.hear" + File.separator + "ListeningSite" + File.separator + "lrc" + File.separator;

    //录音文件存放路径
    public static String RECORDPATH = "data" + Environment.getDataDirectory().getAbsolutePath() + File.separator
            + "com.boll.audiobook.hear" + File.separator + "ListeningSite" + File.separator + "record" + File.separator;

    //录音合并文件存放路径
    public static String MERGEPATH = "data" + Environment.getDataDirectory().getAbsolutePath() + File.separator
            + "com.boll.audiobook.hear" + File.separator + "ListeningSite" + File.separator + "merge" + File.separator;

    //当前播放的标题
    public static String CURRENT_TITLE = "";
    //当前播放的封面url
    public static String CURRENT_COVER_URL = "";
    //是否开启了后台播放服务
    public static boolean isStartPlayService = false;

}
