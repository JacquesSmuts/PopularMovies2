package com.jacquessmuts.popularmovies.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacquessmuts.popularmovies.Movie;
import com.jacquessmuts.popularmovies.R;
import com.jacquessmuts.popularmovies.Utils.Server;
import com.jacquessmuts.popularmovies.Utils.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "extra_movie";

    private Movie mMovie;
    @BindView(R.id.textview_title) TextView textview_title;
    @BindView(R.id.textview_rating) TextView textview_rating;
    @BindView(R.id.textview_date) TextView textview_date;
    @BindView(R.id.textview_synopsis) TextView textview_synopsis;
    @BindView(R.id.imageview_poster) ImageView imageview_poster;

    public static Intent getIntent(Context context, Movie movie){
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra(EXTRA_MOVIE, movie);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        handleExtras();
        populateContents();
        //downloadAdditionalData();
    }

    private void handleExtras(){
        Bundle extras = getIntent().getExtras();
        if (extras.containsKey(EXTRA_MOVIE)){
            mMovie = extras.getParcelable(EXTRA_MOVIE);
        }
    }

    private void populateContents(){
        if (mMovie == null){
            finish();
            return;
        }
        Picasso.with(this)
                .load(Server.buildImageUrl(this, mMovie.getPoster_path()))
                .into(imageview_poster);

        textview_title.setText(mMovie.getOriginal_title());
        textview_rating.setText(String.valueOf(mMovie.getVote_average()));
        textview_date.setText(mMovie.getRelease_date());
        textview_synopsis.setText(mMovie.getOverview());
    }

//    private void downloadAdditionalData(){
//        if (Util.getConnected(this)) {
//            Server.getTrailers(mMovie.getId(), new GetTrailersListener());
//            Server.getReviews(mMovie.getId(), new GetMoviesListener());
//        } else {
//            handleServerSuccess(false);
//        }
//    }
//
//    private class GetTrailersListener implements Server.ServerListener{
//
//        @Override
//        public void serverResponse(String response) {
//            handleServerResponse(response);
//        }
//    }
//
//
//    public void handleTrailerResponse(final String response){
//
//        //runOnUiThread needs to be done because the adapter's notifydatasetchanged only works on UI thread
//        MovieDetailActivity.this.runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                final ArrayList<Trailer> trailers = Trailer.listFromJson(response);
//                handleServerSuccess(movies != null && movies.size() > 0);
//                if (swiperefresh_home.isRefreshing()){
//                    mMovieListAdapter.addData(movies);
//                    swiperefresh_home.setRefreshing(false);
//                } else {
//                    mMovieListAdapter.setData(movies);
//                }
//            }
//        });
//
//    }

}
