package com.robsterthelobster.project2.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robin on 4/2/2016.
 */
public class TrailerModel {

    @SerializedName("id")
    String id;

    @SerializedName("results")
    List<Trailer> trailers;

    @Override
    public String toString(){
        return id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }
}
