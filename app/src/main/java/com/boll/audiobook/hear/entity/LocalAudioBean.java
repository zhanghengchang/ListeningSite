package com.boll.audiobook.hear.entity;

/**
 * created by zoro at 2023/6/30
 */
public class LocalAudioBean {

    private int num;
    private String title;
    private boolean isPlaying;

    public LocalAudioBean() {
    }

    public LocalAudioBean(int num, String title, boolean isPlaying) {
        this.num = num;
        this.title = title;
        this.isPlaying = isPlaying;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
