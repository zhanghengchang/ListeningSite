package com.boll.audiobook.hear.network.response;

import java.io.Serializable;

/**
 * created by zoro at 2023/5/11
 */
public class AudioResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer albumId;
    private String audioUrl;
    private String captionUrl;
    private String cover;
    private Integer duration;
    private Integer id;
    private Boolean isCollect;
    private String title;

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getCaptionUrl() {
        return captionUrl;
    }

    public void setCaptionUrl(String captionUrl) {
        this.captionUrl = captionUrl;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(Boolean isCollect) {
        this.isCollect = isCollect;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
