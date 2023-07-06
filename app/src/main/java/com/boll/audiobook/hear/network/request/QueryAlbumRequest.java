package com.boll.audiobook.hear.network.request;

/**
 * created by zoro at 2023/6/13
 */
public class QueryAlbumRequest {

    private String keyword;
    private Integer levelId;
    private Integer limit;
    private Integer page;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
