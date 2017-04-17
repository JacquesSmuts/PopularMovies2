package com.jacquessmuts.popularmovies.Utils;

import com.jacquessmuts.popularmovies.BuildConfig;

/**
 * Created by Jacques Smuts on 2017/04/17.
 *
 */

public class Server {

//    The base URL will look like: http://image.tmdb.org/t/p/.
//    Then you will need a ‘size’, which will be one of the following: "w92", "w154", "w185", "w342"
// , "w500", "w780", or "original". For most phones we recommend using “w185”.
//    And finally the poster path returned by the query, in this case “/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg”

    public static final String BASE_URL = "http://tmdb.org";
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    public static final String API_KEY = BuildConfig.API_KEY; //Replace API Key here, or define in gradle.properties

    public enum ImageSize {
        ONE("w92"),
        TWO("w154"),
        THREE("w185"), //default?
        FOUR("w342"),
        FIVE("w500"),
        SIX("w780"),
        ORIGINAL("original");

        private final String size;

        ImageSize(String size){
            this.size = size;
        }

        public String getSize() {
            return size;
        }
    }

    public static String buildImageUrl(String relativePath){
        String url = BASE_IMAGE_URL;

        url += ImageSize.THREE.getSize(); //TODO: get the DPI and determine the imagesize based on that
        url += relativePath;

        return url;
    }

}
