package com.jacquessmuts.popularmovies.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jacquessmuts.popularmovies.Utils.Server;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Review implements Parcelable {

    private String author;
    private String id;
    private String content;
    private String url;

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    //JSON handler

    /**
     * Return a list of Movies from a given json String
     * @param jsonString any json String
     * @return arraylist of movies
     */
    public static ArrayList<Review> listFromJson(String jsonString){
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        Type listType = new TypeToken<List<Review>>(){}.getType();
        ArrayList<Review> reviews = null;
        try {
            reviews = gson.fromJson(jsonObject.get(Server.JSON_RESULTS), listType);
        } catch (IllegalStateException e){
            e.printStackTrace(); // probably not necessary?
        }
        return reviews;
    }

    //region PARCELABLE
    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            Review var = new Review();
            var.author = source.readString();
            var.id = source.readString();
            var.content = source.readString();
            var.url = source.readString();
            return var;
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.author);
        dest.writeString(this.id);
        dest.writeString(this.content);
        dest.writeString(this.url);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
