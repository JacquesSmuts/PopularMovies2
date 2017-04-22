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
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    //original title
//    movie poster image thumbnail
//    A plot synopsis (called overview in the api)
//    user rating (called vote_average in the api)
//    release date
    public static final String EXTRA_MOVIE = "extra_movie";

    private Movie mMovie;

    private TextView mTextViewTitle, mTextViewRating, mTextViewDate, mTextViewSynopsis;
    private ImageView mImageViewPoster;

    public static Intent getIntent(Context context, Movie movie){
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra(EXTRA_MOVIE, movie);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleExtras();
        setContentView(R.layout.activity_movie_detail);
        findViews();
        populateContents();
    }

    private void findViews(){
        mImageViewPoster = (ImageView) findViewById(R.id.imageview_poster);
        mTextViewTitle = (TextView) findViewById(R.id.textview_title);
        mTextViewRating = (TextView) findViewById(R.id.textview_rating);
        mTextViewDate = (TextView) findViewById(R.id.textview_date);
        mTextViewSynopsis = (TextView) findViewById(R.id.textview_synopsis);
    }

    private void populateContents(){
        Picasso.with(this)
                .load(Server.buildImageUrl(this, mMovie.getPoster_path()))
                .into(mImageViewPoster);

        mTextViewTitle.setText(mMovie.getOriginal_title());
        mTextViewRating.setText(String.valueOf(mMovie.getVote_average()));
        mTextViewDate.setText(mMovie.getRelease_date());
        mTextViewSynopsis.setText(mMovie.getOverview());
    }

    private void handleExtras(){
        Bundle extras = getIntent().getExtras();
        if (extras.containsKey(EXTRA_MOVIE)){
            mMovie = extras.getParcelable(EXTRA_MOVIE);
        }
    }
}
