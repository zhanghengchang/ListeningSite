package com.boll.audiobook.hear.network.response;

import java.io.Serializable;

/**
 * 返回的token参数
 * created by zoro at 2022/11/4
 */
public class TokenResponse implements Serializable {

    private static final long serialVersionUID = 3263911640285635061L;
    private int code;
    private String message;
    private long expireDate;
    private String token;

    public TokenResponse(int code, String message, long expireDate, String token) {
        this.code = code;
        this.message = message;
        this.expireDate = expireDate;
        this.token = token;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(long expireDate) {
        this.expireDate = expireDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
