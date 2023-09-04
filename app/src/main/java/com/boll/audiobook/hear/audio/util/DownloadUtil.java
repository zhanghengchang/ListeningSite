package com.boll.audiobook.hear.audio.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 下载字幕或音频文件
 * created by zoro at 2023/5/12
 */
public class DownloadUtil {

    /**
     * 判断本地是否存在文件
     *
     * @param path
     * @return
     */
    public static boolean existsFile(String path) {
        File file = new File(path);
        if (file.exists()) return true;
        return false;
    }

    /**
     * 下载str字幕或mp3资源
     *
     * @param url
     * @param localPath 保存的本地路径
     * @param fileName 文件名
     */
    public static boolean downloadRes(String url, String localPath, String fileName) {
        File folderFile = new File(localPath);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }
        File file = new File(localPath + fileName);
        //获取输入流下载
        InputStream inputStream = getInputStream(url);
        byte[] data = new byte[1024];
        FileOutputStream fileOutputStream = null;
        try {
            int length = inputStream.read(data);
            fileOutputStream = new FileOutputStream(file);
            while (length != -1) {
                fileOutputStream.write(data, 0, length);
                length = inputStream.read(data);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取url输入流
     *
     * @param url
     * @return
     */
    public static InputStream getInputStream(String url) {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL mUrl = new URL(url);
            httpURLConnection = (HttpURLConnection) mUrl.openConnection();
            // 设置网络连接超时时间
            httpURLConnection.setConnectTimeout(1000 * 60);
            // 设置应用程序要从网络连接读取数据
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200) {
                // 从服务器返回一个输入流
                inputStream = httpURLConnection.getInputStream();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputStream;
    }

}
