package com.boll.audiolib.entity;

import android.text.TextUtils;

import com.boll.audiolib.util.TimeUtil;

/**
 * 解析str字幕资源实体类
 * created by zoro at 2023/5/15
 */
public class SrtResBean {

    private int num;//序号
    private String startTime;//本句开始时间
    private String endTime;//本句结束时间
    private String english;//英文字幕
    private String chinese;//中文字幕

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public long getStartTime() {
        return TimeUtil.timeToMilli(startTime);
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return TimeUtil.timeToMilli(endTime);
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getChinese() {
        return TextUtils.isEmpty(chinese) ? "" : chinese;
    }

    public void setChinese(String chinese) {
        this.chinese = chinese;
    }
}
