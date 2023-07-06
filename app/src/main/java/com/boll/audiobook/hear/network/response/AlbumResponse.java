package com.boll.audiobook.hear.network.response;

import java.io.Serializable;
import java.util.List;

/**
 * created by zoro at 2023/5/11
 */
public class AlbumResponse implements Serializable {

    private List<AudioResponse> audioList;
    private Integer id;
    private Boolean isCollect;
    private Integer levelId;
    private String name;
    private String picUrl;
    private Integer resCount;

    public List<AudioResponse> getAudioList() {
        return audioList;
    }

    public void setAudioList(List<AudioResponse> audioList) {
        this.audioList = audioList;
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

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Integer getResCount() {
        return resCount;
    }

    public void setResCount(Integer resCount) {
        this.resCount = resCount;
    }
}
