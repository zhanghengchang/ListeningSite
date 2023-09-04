package com.boll.audiobook.hear.audio.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 获取字幕内容
 * created by zoro at 2023/5/17
 */
public class GetContentUtil {

    /**
     * 获取网络文本，需要在工作线程中执行
     *
     * @param url
     * @return
     */
    public static String getContentFromNetwork(String url) {
        String str = null;
        try {
            URL _url = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(1000 * 30);
            conn.setReadTimeout(1000 * 30);
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                is.close();
                bos.close();
                str = bos.toString("UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return str;
    }

    /**
     * 获取本地字幕文件内容
     *
     * @param filePath
     */
    public static String getContentFromLocal(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                InputStream in = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                String str = "";
                StringBuilder content = new StringBuilder();
                while ((len = in.read(bytes)) != -1) {
                    str = new String(bytes, 0, len, "UTF-8");
                    content.append(str);
                }
                return content.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }

}
