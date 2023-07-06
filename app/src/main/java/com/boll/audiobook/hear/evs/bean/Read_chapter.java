/**
 * Copyright 2023 json.cn
 */
package com.boll.audiobook.hear.evs.bean;

import java.util.List;

/**
 * Auto-generated: 2023-06-07 14:33:19
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
public class Read_chapter {

    private int word_count;
    private double total_score;
    private double syll_score;
    private List<Sentences> sentences;
    private String reject_type;
    private boolean is_rejected;
    private double integrity_score;
    private double fluency_score;
    private String except_info;
    private String content;
    private double accuracy_score;

    public void setWord_count(int word_count) {
        this.word_count = word_count;
    }

    public int getWord_count() {
        return word_count;
    }

    public void setTotal_score(double total_score) {
        this.total_score = total_score;
    }

    public double getTotal_score() {
        return total_score;
    }

    public void setSyll_score(double syll_score) {
        this.syll_score = syll_score;
    }

    public double getSyll_score() {
        return syll_score;
    }

    public void setSentences(List<Sentences> sentences) {
        this.sentences = sentences;
    }

    public List<Sentences> getSentences() {
        return sentences;
    }

    public void setReject_type(String reject_type) {
        this.reject_type = reject_type;
    }

    public String getReject_type() {
        return reject_type;
    }

    public void setIs_rejected(boolean is_rejected) {
        this.is_rejected = is_rejected;
    }

    public boolean getIs_rejected() {
        return is_rejected;
    }

    public void setIntegrity_score(double integrity_score) {
        this.integrity_score = integrity_score;
    }

    public double getIntegrity_score() {
        return integrity_score;
    }

    public void setFluency_score(double fluency_score) {
        this.fluency_score = fluency_score;
    }

    public double getFluency_score() {
        return fluency_score;
    }

    public void setExcept_info(String except_info) {
        this.except_info = except_info;
    }

    public String getExcept_info() {
        return except_info;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setAccuracy_score(double accuracy_score) {
        this.accuracy_score = accuracy_score;
    }

    public double getAccuracy_score() {
        return accuracy_score;
    }

}