package com.boll.audiobook.hear.network.response;

import java.util.List;

/**
 * created by zoro at 2023/5/11
 */
public class LevelResponse {

    private List<AlbumResponse> albumList;
    private String iconUrl;
    private Integer id;
    private String name;
    private Integer parentId;
    private String picUrl;

    public List<AlbumResponse> getAlbumList() {
        return albumList;
    }

    public void setAlbumList(List<AlbumResponse> albumList) {
        this.albumList = albumList;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

}
