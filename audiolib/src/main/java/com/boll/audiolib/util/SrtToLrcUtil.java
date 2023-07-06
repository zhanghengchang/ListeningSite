package com.boll.audiolib.util;

import android.text.TextUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * str格式字幕转换为lrc格式字幕
 * created by zoro at 2023/5/17
 */
public class SrtToLrcUtil {

    /**
     * 保存转换完的lrc文件
     *
     * @param str
     * @param localPath  本地保存文件夹路径
     * @param fileName  子路径文件名
     * @param enFilePath 英文字幕保存路径
     * @param chFilePath 中文字幕保存路径
     * @return
     */
//    public static boolean saveLrcFile(String str, String localPath, String fileName, String enFilePath, String chFilePath) {
//        File folderFile = new File(localPath + File.separator + fileName);
//        if (!folderFile.exists()) {
//            folderFile.mkdirs();
//        }
//        File enFile = new File(enFilePath);//英文字幕
//        File chFile = new File(chFilePath);//中文字幕
//        if (enFile.exists()){
//            return true;
//        }
//        FileWriter enFw = null;
//        FileWriter chFw = null;
//        try {
//            enFw = new FileWriter(enFile);
//            chFw = new FileWriter(chFile);
//            BufferedWriter enBw = new BufferedWriter(enFw);
//            BufferedWriter chBw = new BufferedWriter(chFw);
//            //以空行为分割符 拆分每句的字幕
//            String[] sentences = str.split("\n\n");
//            for (int i = 0; i < sentences.length; i++) {
//                String sentence = sentences[i];
//                //再以换行为分隔符 拆分每句字幕的每行内容
//                String[] lines = sentence.split("\n");
//                if (!TextUtils.equals("\n", sentences[i])) {
//                    //将每行内容转换成lrc格式并写入
//                    for (int j = 0; j < lines.length; j++) {
//                        StringBuilder enLine = new StringBuilder();
//                        StringBuilder chLine = new StringBuilder();
//                        if (j == 1) {
//                            String lineTime = lines[j];
//                            //拆分开始、结束时间
//                            String[] times = lineTime.split("-->");
//                            if (times != null && times.length == 2) {
//                                //去掉中间空格
//                                String startTime = times[0].replace(" ", "");
//                                String[] split = startTime.split(":");
//                                if (split != null && split.length == 3) {
//                                    startTime = split[1] + ":" + split[2];
//                                } else if (split != null && split.length == 2) {
//                                    startTime = split[0] + ":" + split[1];
//                                }
//                                startTime = "[" + startTime.replace(",", ".") + "]";
//                                enLine.append(startTime);
//                                chLine.append(startTime);
//                            }
//                        } else if (j == 2) {
//                            enLine.append(lines[j] + "\n");
//                        } else if (j == 3) {
//                            chLine.append(lines[j] + "\n");
//                        }
//                        String enStr = String.valueOf(enLine);
//                        String chStr = String.valueOf(chLine);
//                        //写入英文字幕
//                        enBw.write(enStr);
//                        enBw.flush();
//                        //写入中文字幕
//                        chBw.write(chStr);
//                        chBw.flush();
//                    }
//                }
//            }
//            enFw.close();
//            chFw.close();
//            enBw.close();
//            chBw.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }

    /**
     * 保存转换完的lrc文件
     *
     * @param str
     * @param localPath  本地保存文件夹路径
     * @param fileName   子路径文件名
     * @param enFilePath 英文字幕保存路径
     * @param chFilePath 中文字幕保存路径
     * @return
     */
    public static boolean saveLrcFile(String str, String localPath, String fileName, String enFilePath, String chFilePath) {
        File folderFile = new File(localPath + File.separator + fileName);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }
        File enFile = new File(enFilePath);//英文字幕
        File chFile = new File(chFilePath);//中文字幕
        if (enFile.exists()) {
            return true;
        }
        FileWriter enFw = null;
        FileWriter chFw = null;
        try {
            enFw = new FileWriter(enFile);
            chFw = new FileWriter(chFile);
            BufferedWriter enBw = new BufferedWriter(enFw);
            BufferedWriter chBw = new BufferedWriter(chFw);
            //以空行为分割符 拆分每句的字幕
            String[] sentences = str.split("\n\n");
            for (int i = 0; i < sentences.length; i++) {
                String sentence = sentences[i];
                //再以换行为分隔符 拆分每句字幕的每行内容
                String[] lines = sentence.split("\n");
                String endTime = "";
                if (!TextUtils.equals("\n", sentences[i])) {
                    //将每行内容转换成lrc格式并写入
                    for (int j = 0; j < lines.length; j++) {
                        StringBuilder enLine = new StringBuilder();
                        StringBuilder chLine = new StringBuilder();
                        if (j == 1) {
                            String lineTime = lines[j];
                            //拆分开始、结束时间
                            String[] times = lineTime.split("-->");
                            if (times != null && times.length == 2) {
                                //去掉中间空格
                                String startTime = times[0].replace(" ", "");
                                String[] spStartTime = startTime.split(":");
                                if (spStartTime != null && spStartTime.length == 3) {
                                    startTime = spStartTime[1] + ":" + spStartTime[2];
                                } else if (spStartTime != null && spStartTime.length == 2) {
                                    startTime = spStartTime[0] + ":" + spStartTime[1];
                                }
                                startTime = "[" + startTime.replace(",", ".") + "]";
                                if (i == 0) {
                                    enLine.append(startTime);
                                    chLine.append(startTime);
                                }
                                if (i != sentences.length - 1){
                                    endTime = times[1].replace(" ", "");
                                    String[] spEndTime = endTime.split(":");
                                    if (spEndTime != null && spEndTime.length == 3) {
                                        endTime = spEndTime[1] + ":" + spEndTime[2];
                                    } else if (spEndTime != null && spEndTime.length == 2) {
                                        endTime = spEndTime[0] + ":" + spEndTime[1];
                                    }
                                    endTime = "[" + endTime.replace(",", ".") + "]";
                                }
                            }
                        } else if (j == 2) {
                            enLine.append(lines[j] + "\n" + endTime);
                        } else if (j == 3) {
                            chLine.append(lines[j] + "\n" + endTime);
                        }
                        String enStr = String.valueOf(enLine);
                        String chStr = String.valueOf(chLine);
                        //写入英文字幕
                        enBw.write(enStr);
                        enBw.flush();
                        //写入中文字幕
                        chBw.write(chStr);
                        chBw.flush();
                    }
                }
            }
            enFw.close();
            chFw.close();
            enBw.close();
            chBw.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
