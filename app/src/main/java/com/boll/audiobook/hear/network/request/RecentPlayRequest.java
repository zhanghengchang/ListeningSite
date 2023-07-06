package com.boll.audiobook.hear.network.request;

/**
 * created by zoro at 2023/6/17
 */
public class RecentPlayRequest {

    private Integer albumId;
    private Boolean directory;
    private String fileName;
    private Integer type;

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public Boolean getDirectory() {
        return directory;
    }

    public void setDirectory(Boolean directory) {
        this.directory = directory;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}
