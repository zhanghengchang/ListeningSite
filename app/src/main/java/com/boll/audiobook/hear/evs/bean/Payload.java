/**
 * Copyright 2023 json.cn
 */
package com.boll.audiobook.hear.evs.bean;

/**
 * Auto-generated: 2023-06-07 14:33:19
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
public class Payload {

    private String sid;
    private String description;
    private Data data;
    private int code;

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSid() {
        return sid;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}