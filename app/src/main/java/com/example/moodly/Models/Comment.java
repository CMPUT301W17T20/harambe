package com.example.moodly.Models;

import java.util.Date;

import io.searchbox.annotations.JestId;

/**
 * Created by jkc1 on 2017-03-20.
 */

public class Comment {

    private Date date;
    private String owner;
    private String text;
    private String moodId;

    @JestId
    private String id;

    public Comment(String text, String owner, String moodId) {
        this.date = new Date();
        this.owner = owner;
        this.text = text;
        this.moodId = moodId;

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMoodId() {
        return moodId;
    }

    public void setMoodId(String moodId) {
        this.moodId = moodId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toString(){
        return owner + "      "+ text;
    }
}
