package com.robsterthelobster.project2.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by robin on 4/2/2016.
 */
public class ReviewModel {

    @SerializedName("id")
    int id;

    @SerializedName("page")
    int page;

    @SerializedName("results")
    List<Review> results;

    @SerializedName("total_pages")
    int total_pages;

    @SerializedName("total_results")
    int total_results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Review> getResults() {
        return results;
    }

    public void setResults(List<Review> results) {
        this.results = results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }
}
