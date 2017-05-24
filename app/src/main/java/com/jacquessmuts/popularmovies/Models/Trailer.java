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

public class Trailer implements Parcelable {

    private String site;
    private int size;
    private String iso_3166_1;
    private String name;
    private String id;
    private String type;
    private String iso_639_1;
    private String key; //the Youtube key

    public String getSite() {
        return this.site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getIso_3166_1() {
        return this.iso_3166_1;
    }

    public void setIso_3166_1(String iso_3166_1) {
        this.iso_3166_1 = iso_3166_1;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIso_639_1() {
        return this.iso_639_1;
    }

    public void setIso_639_1(String iso_639_1) {
        this.iso_639_1 = iso_639_1;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    //JSON handler

    /**
     * Return a list of Movies from a given json String
     * @param jsonString any json String
     * @return arraylist of movies
     */
    public static ArrayList<Trailer> listFromJson(String jsonString){
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        Type listType = new TypeToken<List<Trailer>>(){}.getType();
        ArrayList<Trailer> trailers = null;
        try {
            trailers = gson.fromJson(jsonObject.get(Server.JSON_RESULTS), listType);
        } catch (IllegalStateException e){
            e.printStackTrace(); // probably not necessary?
        }
        return trailers;
    }

    //region PARCELABLE
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.site);
        dest.writeInt(this.size);
        dest.writeString(this.iso_3166_1);
        dest.writeString(this.name);
        dest.writeString(this.id);
        dest.writeString(this.type);
        dest.writeString(this.iso_639_1);
        dest.writeString(this.key);
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel source) {
            Trailer var = new Trailer();
            var.site = source.readString();
            var.size = source.readInt();
            var.iso_3166_1 = source.readString();
            var.name = source.readString();
            var.id = source.readString();
            var.type = source.readString();
            var.iso_639_1 = source.readString();
            var.key = source.readString();
            return var;
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
