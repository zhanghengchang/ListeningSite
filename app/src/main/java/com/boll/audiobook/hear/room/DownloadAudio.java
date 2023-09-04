package com.boll.audiobook.hear.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 *
 * created by zoro at 2023/6/24
 */
@Entity(tableName = "download")
public class DownloadAudio {

    @PrimaryKey(autoGenerate = true)
    public int id;

    private String cover;
    private String audioUrl;
    private String title;
    private Integer albumId;
    private Integer audioId;
    private String captionUrl;
    private Integer duration;
    private Boolean isCollect;

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
