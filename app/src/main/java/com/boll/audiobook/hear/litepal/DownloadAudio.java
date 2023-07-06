package com.boll.audiobook.hear.litepal;

import org.litepal.crud.LitePalSupport;

/**
 *
 * created by zoro at 2023/6/24
 */
public class DownloadAudio extends LitePalSupport {

    private Integer albumId;
    private String audioUrl;
    private String captionUrl;
    private String cover;
    private Integer duration;
    private Integer audioId;
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

    public Integer getAudioId() {
        return audioId;
    }

    public void setAudioId(Integer audioId) {
        this.audioId = audioId;
    }

    public Boolean getCollect() {
        return isCollect;
    }

    public void setCollect(Boolean collect) {
        isCollect = collect;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
