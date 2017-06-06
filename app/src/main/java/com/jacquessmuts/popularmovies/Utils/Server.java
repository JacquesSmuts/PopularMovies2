package com.jacquessmuts.popularmovies.Utils;

import android.content.Context;
import android.util.Log;

import com.jacquessmuts.popularmovies.BuildConfig;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Jacques Smuts on 2017/04/17.
 *
 */

public class Server {

//    The base URL will look like: http://image.tmdb.org/t/p/.
//    Then you will need a ‘size’, which will be one of the following: "w92", "w154", "w185", "w342"
// , "w500", "w780", or "original". For most phones we recommend using “w185”.
//    And finally the poster path returned by the query, in this case “/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg”
//
//    Once you obtain your key, you append it to your HTTP request as a URL parameter like so:
//    http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]

    private static final String TAG = "Network Operation";

    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String POPULAR = BASE_URL + "popular";
    private static final String TRAILERS = BASE_URL + "%s/videos";
    private static final String REVIEWS = BASE_URL + "%s/reviews";

    private static final String TOP_RATED = BASE_URL + "top_rated";
    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String YOUTUBE_IMAGE_URL = "https://img.youtube.com/vi/%s/mqdefault.jpg";
    private static final String API_KEY = BuildConfig.API_KEY; //Replace API Key here, or define in gradle.properties
    private static final String API_KEY_APPEND ="?api_key=" + API_KEY;
    private static final String PAGE_NUMBER = "&page=";

    public static final String JSON_RESULTS = "results";

    private static OkHttpClient mClient = new OkHttpClient();

    public interface ServerListener{
        void serverResponse(String response);
    }

    public enum SortingOption{
        POPULAR, RATING
    }

    private enum ImageSize {
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

    public static String buildImageUrl(Context context, String relativePath){
        String url = BASE_IMAGE_URL;

        int dpi = Util.getDPI(context);
        String size = ImageSize.THREE.getSize();
        if (dpi >= 420){
            //for higher resolution screens, use a higher resolution image
            size = ImageSize.FOUR.getSize();
        }
        url += size;
        url += relativePath;

        return url;
    }


    public static String buildYouTubeImageUrl(String key){
        return String.format(YOUTUBE_IMAGE_URL, key);
    }

    public static void getMovies(SortingOption option, ServerListener listener){
        getMovies(option, 1, listener);
    }

    public static void getMovies(SortingOption option, int pageNumber, ServerListener listener){

        switch (option){
            case POPULAR:
                getPopularMovies(pageNumber, listener);
                break;
            case RATING:
                getTopRatedMovies(pageNumber, listener);
                break;
        }
    }


    private static void getTopRatedMovies(int pageNumber, ServerListener listener){
        doRequest(TOP_RATED, pageNumber, listener);
    }

    private static void getPopularMovies(int pageNumber, ServerListener listener){
        doRequest(POPULAR, pageNumber, listener);
    }

    public static void getTrailers(int movieId, ServerListener listener){
        doRequest(String.format(TRAILERS, movieId), listener);
    }

    public static void getReviews(int movieId, ServerListener listener){
        doRequest(String.format(REVIEWS, movieId), listener);
    }

    private static void doRequest(String url, final ServerListener listener) {
        doRequest(url, -1, listener);
    }


    /**
     * Asynchronously do a getRequest with the given URL, and return a string through the given listener
     * @param url url from statics declared above
     * @param listener listener must be implemented in returning class
     */
    private static void doRequest(String url, int pageNumber, final ServerListener listener) {
        Log.i(TAG, "url=" + url);
        String finalUrl = url+API_KEY_APPEND;
        if (pageNumber > 1){
            finalUrl += PAGE_NUMBER + pageNumber;
        }
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
//                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
//                    //System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
//                }
                String responseString = response.body().string();
                Log.i(TAG, "response=" + responseString);
                listener.serverResponse(responseString);
            }
        });
    }
}
