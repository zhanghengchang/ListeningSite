package com.boll.audiolib.util;

import android.text.TextUtils;

import com.boll.audiolib.entity.SrtResBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析字幕资源
 * created by zoro at 2023/5/15
 */
public class SrtResParseUtil {

    /**
     * 解析字幕文件每一行内容
     *
     * @param str
     * @return
     */
    public static List<SrtResBean> parseSrt(String str) {
        List<SrtResBean> srtResBeans = new ArrayList<>();
        //以空行为分割符 拆分每句的字幕
        String[] sentences = str.split("\n\n");
        for (int i = 0; i < sentences.length; i++) {
            String sentence = sentences[i];
            //再以换行为分隔符 拆分每句字幕的单独行
            String[] lines = sentence.split("\n");
            SrtResBean srtResBean = new SrtResBean();
            if (!TextUtils.equals("\n",sentences[i])) {
                //添加每一句的字幕数据到list
                for (int j = 0; j < lines.length; j++) {
                    if (j == 0) {
                        srtResBean.setNum(Integer.parseInt(lines[j]));
                    } else if (j == 1) {
                        String line = lines[j];
                        //拆分开始 结束时间
                        String[] times = line.split("-->");
                        if (times != null && times.length == 2) {
                            String startTime = times[0].replace(" ", "");
                            String endTime = times[1].replace(" ", "");
                            srtResBean.setStartTime(startTime);
                            srtResBean.setEndTime(endTime);
                        } else {
                            //不规范数据
                            continue;
                        }
                    } else if (j == 2) {
                        srtResBean.setEnglish(lines[j]);
                    } else {
                        srtResBean.setChinese(lines[j]);
                    }
                }
                srtResBeans.add(srtResBean);
            }
        }
        return srtResBeans;
    }

}
