package com.boll.audiobook.hear.entity;

import java.io.Serializable;

/**
 * 专辑页、播放页选集 音频listView的实体类
 * created by zoro at 2023/5/12
 */
public class AudioBean implements Serializable {

    private int num;
    private String audioName;
    private String duration;
    private boolean isPlaying;
    private int downloadState;//下载状态 0：未下载，1：下载完成，2：下载中

    public AudioBean() {
    }

    public AudioBean(int num, String audioName, String duration, boolean isPlaying, int downloadState) {
        this.num = num;
        this.audioName = audioName;
        this.duration = duration;
        this.isPlaying = isPlaying;
        this.downloadState = downloadState;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public int getDownloadState() {
        return downloadState;
    }

    public void setDownloadState(int downloadState) {
        this.downloadState = downloadState;
    }
}
