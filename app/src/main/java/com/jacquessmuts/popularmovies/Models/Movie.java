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

public class Movie implements Parcelable {

    private String overview;
    private String original_language;
    private String original_title;
    private boolean video;
    private String title;
    private int[] genre_ids;
    private String poster_path;
    private String backdrop_path;
    private String release_date;
    private double popularity;
    private double vote_average;
    private int id;
    private boolean adult;
    private int vote_count;
    private ArrayList<Trailer> trailers;
    private ArrayList<Review> reviews;
    private boolean isFavorite;

    //GETTERS AND SETTERS
    public String getOverview() {
        return this.overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getOriginal_language() {
        return this.original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getOriginal_title() {
        return this.original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public boolean getVideo() {
        return this.video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int[] getGenre_ids() {
        return this.genre_ids;
    }

    public void setGenre_ids(int[] genre_ids) {
        this.genre_ids = genre_ids;
    }

    public String getPoster_path() {
        return this.poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getBackdrop_path() {
        return this.backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getRelease_date() {
        return this.release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public double getPopularity() {
        return this.popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public double getVote_average() {
        return this.vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getAdult() {
        return this.adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public int getVote_count() {
        return this.vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }


    //JSON handler

    /**
     * Return a list of Movies from a given json String
     * @param jsonString any json String
     * @return arraylist of movies
     */
    public static ArrayList<Movie> listFromJson(String jsonString){
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        Type listType = new TypeToken<List<Movie>>(){}.getType();
        ArrayList<Movie> movies = null;
        try {
            movies = gson.fromJson(jsonObject.get(Server.JSON_RESULTS), listType);
        } catch (IllegalStateException e){
            e.printStackTrace(); // probably not necessary?
        }
        return movies;
    }


    //PARCELABLE
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.overview);
        dest.writeString(this.original_language);
        dest.writeString(this.original_title);
        dest.writeByte(this.video ? (byte) 1 : (byte) 0);
        dest.writeString(this.title);
        dest.writeIntArray(this.genre_ids);
        dest.writeString(this.poster_path);
        dest.writeString(this.backdrop_path);
        dest.writeString(this.release_date);
        dest.writeDouble(this.popularity);
        dest.writeDouble(this.vote_average);
        dest.writeInt(this.id);
        dest.writeByte(this.adult ? (byte) 1 : (byte) 0);
        dest.writeInt(this.vote_count);
        dest.writeTypedList(this.trailers);
        dest.writeTypedList(this.reviews);
        dest.writeByte(this.isFavorite ? (byte) 1 : (byte) 0);
    }

    public Movie() {
    }

    protected Movie(Parcel in) {
        this.overview = in.readString();
        this.original_language = in.readString();
        this.original_title = in.readString();
        this.video = in.readByte() != 0;
        this.title = in.readString();
        this.genre_ids = in.createIntArray();
        this.poster_path = in.readString();
        this.backdrop_path = in.readString();
        this.release_date = in.readString();
        this.popularity = in.readDouble();
        this.vote_average = in.readDouble();
        this.id = in.readInt();
        this.adult = in.readByte() != 0;
        this.vote_count = in.readInt();
        this.trailers = in.createTypedArrayList(Trailer.CREATOR);
        this.reviews = in.createTypedArrayList(Review.CREATOR);
        this.isFavorite = in.readByte() != 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
