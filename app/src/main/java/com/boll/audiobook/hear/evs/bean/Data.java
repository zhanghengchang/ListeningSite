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
public class Data {

    private String version;
    private String type;
    private Read_chapter read_chapter;
    private String language;

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setRead_chapter(Read_chapter read_chapter) {
        this.read_chapter = read_chapter;
    }

    public Read_chapter getRead_chapter() {
        return read_chapter;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

}