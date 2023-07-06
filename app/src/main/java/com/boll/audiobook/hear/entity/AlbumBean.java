package com.boll.audiobook.hear.entity;

/**
 * 主页专辑listView的实体类
 * created by zoro at 2023/5/11
 */
public class AlbumBean {

    private int albumId;
    private String imgUrl;
    private String resCount;
    private String name;

    public AlbumBean() {
    }

    public AlbumBean(int albumId, String imgUrl, String resCount, String name) {
        this.albumId = albumId;
        this.imgUrl = imgUrl;
        this.resCount = resCount;
        this.name = name;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getResCount() {
        return resCount;
    }

    public void setResCount(String resCount) {
        this.resCount = resCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
