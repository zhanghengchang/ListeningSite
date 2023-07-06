package com.boll.audiobook.hear.utils;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 多个音频合并
 * created by zoro at 2023/6/20
 */
public class Mp3MergeUtil {

    private static final String TAG = "Mp3MergeUtil";

    public static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    /**
     * @param mp3ListFile 多个Mp3音频片段的路径拼接字符串  拼接规则 a.mp3|b.mp3|c.mp3
     * @param resultFile  生成文件路径
     */
    public static String mergeMp3Cmd(String mp3ListFile, String resultFile) {
        String cmd = "ffmpeg -i concat:%s -acodec copy %s";
        cmd = String.format(cmd, mp3ListFile, resultFile);
        return cmd;
    }

    public static void mergeMP3Files(List<File> files) {
        FileOutputStream fos = null;
        File mergeFile = new File(Const.MERGEPATH + "merge.mp3");
        if (mergeFile.exists()) mergeFile.delete();
//        List<File> destFiles = new ArrayList<>();
//        for (int i = 0; i < files.size(); i++) {
//            String absolutePath = files.get(i).getAbsolutePath();
//            String destPath = absolutePath.replace(".mp3", ".temp");
//            File destFile = new File(destPath);
//            try {
//                copyFileUsingStream(files.get(i),destFile);
//                destFiles.add(destFile);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        try {
            fos = new FileOutputStream(mergeFile);
            int count = files.size();
            boolean firstFile = true;
            for (int i = 0; i < count; i++) {
                File file = files.get(i);
                Log.e(TAG, "mergeMP3Files: " + file.getAbsolutePath());
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    if (fis.available() < 6) {
                        continue;
                    }
                    byte[] buffer = new byte[1024 * 4];
                    int len = -1;
                    if (firstFile) {// 读取第一个文件的全部内容，其它文件跳过文件头的6个字节
//                        firstFile = false;
                        while ((len = fis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                    } else {
                        fis.skip(6);
                        while ((len = fis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, Log.getStackTraceString(e));
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, Log.getStackTraceString(e));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        }
    }

    /**
     * 合并音频文件
     *
     * @param mp3Path    音频片段路径集合
     * @param resultPath 生成文件路径
     * @param isAdd      追加or覆盖
     */
    public static void mergeMP3Files(List<String> mp3Path, String resultPath, boolean isAdd) {
        File file = new File(resultPath);
        if (file.exists()) {
            file.delete();
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(resultPath, isAdd), 1024 * 200);
            for (int i = 0; i < mp3Path.size(); i++) {
                writeVideoFile(mp3Path.get(i), outputStream);
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param videoPath    音频片段路径
     * @param outputStream 音频流
     */
    public static void writeVideoFile(String videoPath, BufferedOutputStream outputStream) {
        try {
            MediaExtractor extractor = new MediaExtractor();
            extractor.setDataSource(videoPath);
            int videoTrackIndex = getAudioTrack(extractor);
            if (videoTrackIndex < 0) {
                return;
            }
            //选择音频轨道
            extractor.selectTrack(videoTrackIndex);
            MediaFormat mediaFormat = extractor.getTrackFormat(videoTrackIndex);
            //获取音频时间
            float time = mediaFormat.getLong(MediaFormat.KEY_DURATION) / 1000;//返回的是微秒，转换成毫秒
            //合并音频
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 200);
            while (true) {
                int sampleSize = extractor.readSampleData(buffer, 0);
                if (sampleSize <= 0) {
                    break;
                }
                byte[] buf = new byte[sampleSize];
                buffer.get(buf, 0, sampleSize);
                //写入文件
                outputStream.write(buf);
                //音轨数据往前读
                extractor.advance();
            }
            extractor.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取音频数据轨道
     */
    private static int getAudioTrack(MediaExtractor extractor) {
        for (int i = 0; i < extractor.getTrackCount(); i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio")) {//audio为音频，video为视频
                return i;
            }
        }
        return -1;
    }

}
