package com.boll.audiobook.hear.network.response;

/**
 * created by zoro at 2023/5/12
 */
public class AlbumDetailResponse extends BaseResponse {

    private AlbumResponse data;

    public AlbumResponse getData() {
        return data;
    }

    public void setData(AlbumResponse data) {
        this.data = data;
    }
}
