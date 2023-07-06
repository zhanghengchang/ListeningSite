package com.boll.audiobook.hear.network.request;

/**
 * 鉴权接口请求参数
 * created by zoro at 2023/6/5
 */
public class TokenRequest {

    private String deviceId;

    private String accessKey;

    public TokenRequest() {
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

}
