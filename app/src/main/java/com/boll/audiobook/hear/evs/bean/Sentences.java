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
public class Sentences {

    private List<Words> words;
    private int word_count;
    private double total_score;
    private double syll_score;
    private double integrity_score;
    private int index;
    private double fluency_score;
    private String content;
    private double accuracy_score;

    public void setWords(List<Words> words) {
        this.words = words;
    }

    public List<Words> getWords() {
        return words;
    }

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

    public void setIntegrity_score(double integrity_score) {
        this.integrity_score = integrity_score;
    }

    public double getIntegrity_score() {
        return integrity_score;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setFluency_score(double fluency_score) {
        this.fluency_score = fluency_score;
    }

    public double getFluency_score() {
        return fluency_score;
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