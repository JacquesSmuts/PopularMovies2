package com.jacquessmuts.popularmovies.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacquessmuts.popularmovies.Fragments.ReviewFragment;
import com.jacquessmuts.popularmovies.Fragments.TrailerFragment;
import com.jacquessmuts.popularmovies.Models.Movie;
import com.jacquessmuts.popularmovies.Models.Review;
import com.jacquessmuts.popularmovies.Models.Trailer;
import com.jacquessmuts.popularmovies.R;
import com.jacquessmuts.popularmovies.Utils.Server;
import com.jacquessmuts.popularmovies.Utils.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import icepick.Icepick;
import icepick.State;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "extra_movie";

    @State Movie movie;
    private int mSuccessfulApiCount = 0;
    private static final int TOTAL_API_CALLS = 2;

    @BindView(R.id.swiperefresh_detail) SwipeRefreshLayout swiperefresh_detail;
    @BindView(R.id.textview_title) TextView textview_title;
    @BindView(R.id.textview_rating) TextView textview_rating;
    @BindView(R.id.textview_date) TextView textview_date;
    @BindView(R.id.textview_synopsis) TextView textview_synopsis;
    @BindView(R.id.imageview_poster) ImageView imageview_poster;
    @BindView(R.id.tv_error_message_display) TextView tv_error_message_display;
    @BindView(R.id.checkbox_movie_favorite) CheckBox checkbox_movie_favorite;
    @BindView(R.id.view_pager) ViewPager view_pager;

    TrailersReviewsPagerAdapter adapterViewPager;

    public static Intent getIntent(Context context, Movie movie){
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra(EXTRA_MOVIE, movie);
        return intent;
    }

    /**
     * OVERRIDES
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        handleExtras();
        Icepick.restoreInstanceState(this, savedInstanceState);
        populateContents();
        downloadAdditionalData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    /**
     * METHODS
     */

    private void handleExtras(){
        Bundle extras = getIntent().getExtras();
        if (extras.containsKey(EXTRA_MOVIE)){
            movie = extras.getParcelable(EXTRA_MOVIE);
        }
    }

    private void populateContents(){
        if (movie == null){
            finish();
            return;
        }
        Picasso.with(this)
                .load(Server.buildImageUrl(this, movie.getPoster_path()))
                .placeholder(android.R.drawable.stat_sys_download)
                .into(imageview_poster);

        textview_title.setText(movie.getOriginal_title());
        textview_rating.setText(String.valueOf(movie.getVote_average()));
        textview_date.setText(movie.getRelease_date());
        textview_synopsis.setText(movie.getOverview());
        swiperefresh_detail.setEnabled(false);

        checkbox_movie_favorite.setChecked(movie.isFavorite());

        adapterViewPager = new TrailersReviewsPagerAdapter(getSupportFragmentManager(), movie, new TrailerFragmentListener());
        view_pager.setAdapter(adapterViewPager);
    }

    private void downloadAdditionalData(){
        if (Util.getConnected(this)) {
            mSuccessfulApiCount = 0;
            Server.getTrailers(movie.getId(), new GetTrailersListener());
            Server.getReviews(movie.getId(), new GetReviewsListener());
            swiperefresh_detail.setRefreshing(true);
        } else {
            handleServerSuccess(false);
        }
    }

    private void handleServerSuccess(boolean success){
        if (success){
            tv_error_message_display.setVisibility(View.GONE);
        } else {
            tv_error_message_display.setVisibility(View.VISIBLE);
        }
        mSuccessfulApiCount++;
        if (mSuccessfulApiCount >= TOTAL_API_CALLS){
            swiperefresh_detail.setRefreshing(false);
        }
        adapterViewPager.setMovie(movie);
    }

    /**
     * CLASSES
     */

    @OnCheckedChanged(R.id.checkbox_movie_favorite)
    void checkChanged(CompoundButton button, boolean checked){
        movie.setFavorite(checked);
        //TODO: database operation
    }

    private static class TrailersReviewsPagerAdapter extends FragmentPagerAdapter {
        private static final int NUM_ITEMS = 2;
        private static final int POS_TRAILER = 0;
        private static final int POS_REVIEW = 1;

        private Movie movie;
        private TrailerFragment trailerFragment;
        private ReviewFragment reviewFragment;
        private TrailerFragmentListener listener;

        public TrailersReviewsPagerAdapter(FragmentManager fragmentManager, Movie movie, TrailerFragmentListener trailerFragmentListener) {
            super(fragmentManager);
            this.movie = movie;
            this.listener = trailerFragmentListener;
            trailerFragment = TrailerFragment.newInstance(1, trailerFragmentListener, movie.getTrailers());
            reviewFragment = ReviewFragment.newInstance(1, null, movie.getReviews());
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {

            if (trailerFragment == null || reviewFragment == null){
                trailerFragment = TrailerFragment.newInstance(1, listener, movie.getTrailers());
                reviewFragment = ReviewFragment.newInstance(1, null, movie.getReviews());
            }

            switch (position) {
                case POS_TRAILER:
                    return trailerFragment;
                case POS_REVIEW:
                    return reviewFragment;//return reviewFragment;
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case POS_TRAILER:
                    return "Trailers";//getItem(position).getContext().getString(R.string.trailers_title);
                case POS_REVIEW:
                    return "Reviews";//getItem(position).getContext().getString(R.string.reviews_title);
                default:
                    return null;
            }
        }

        public void setMovie(Movie movie) {
            this.movie = movie;
            trailerFragment.setTrailers(this.movie.getTrailers());
            reviewFragment.setReviews(this.movie.getReviews());
        }
    }

    private class TrailerFragmentListener implements TrailerFragment.OnListFragmentInteractionListener{

        @Override
        public void onTrailerClick(Trailer item) {
            if (item.getSite().toLowerCase().contains("youtube")){
                String url = "http://www.youtube.com/watch?v=" + item.getKey();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } else {
                Log.e("MovieDetailActivity", "Figure out site other than YouTube. Key=" + item.getKey());
            }
        }
    }

    private class GetTrailersListener implements Server.ServerListener{

        @Override
        public void serverResponse(String response) {
            handleTrailersResponse(response);
        }
    }

    private class GetReviewsListener implements Server.ServerListener{

        @Override
        public void serverResponse(String response) {
            handleReviewsResponse(response);
        }
    }

    public void handleTrailersResponse(final String response){

        //runOnUiThread needs to be done because the adapter's notifydatasetchanged only works on UI thread
        MovieDetailActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final ArrayList<Trailer> trailers = Trailer.listFromJson(response);
                //mTrailerListAdapter.addData(trailers);
                movie.setTrailers(trailers);
                handleServerSuccess(trailers != null && trailers.size() > 0);
            }
        });
    }

    public void handleReviewsResponse(final String response){

        //runOnUiThread needs to be done because the adapter's notifydatasetchanged only works on UI thread
        MovieDetailActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final ArrayList<Review> reviews = Review.listFromJson(response);
                movie.setReviews(reviews);
                //mTrailerListAdapter.addData(trailers);
                handleServerSuccess(reviews != null && reviews.size() > 0);
            }
        });
    }

}
