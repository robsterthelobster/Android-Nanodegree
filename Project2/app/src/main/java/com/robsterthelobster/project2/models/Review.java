package com.robsterthelobster.project2.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by robin on 4/2/2016.
 */
public class Review {
    @SerializedName("id")
    String id;

    @SerializedName("author")
    String author;

    @SerializedName("content")
    String content;

    @SerializedName("url")
    String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
