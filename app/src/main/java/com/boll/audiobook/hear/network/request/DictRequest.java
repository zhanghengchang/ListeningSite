package com.boll.audiobook.hear.network.request;

/**
 * created by zoro at 2023/6/6
 */
public class DictRequest {

    private String keywordStr;
    private String token;
    private String langType;// 中文:CHN,英文:ENG

    public String getKeywordStr() {
        return keywordStr;
    }

    public void setKeywordStr(String keywordStr) {
        this.keywordStr = keywordStr;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLangType() {
        return langType;
    }

    public void setLangType(String langType) {
        this.langType = langType;
    }
}
