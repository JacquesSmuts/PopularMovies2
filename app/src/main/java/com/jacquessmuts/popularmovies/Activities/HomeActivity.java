package com.jacquessmuts.popularmovies.Activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jacquessmuts.popularmovies.Adapters.MovieListAdapter;
import com.jacquessmuts.popularmovies.Movie;
import com.jacquessmuts.popularmovies.R;
import com.jacquessmuts.popularmovies.Utils.Server;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements MovieListAdapter.MovieListOnClickHandler, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private MovieListAdapter mMovieListAdapter;
    private ArrayList<Movie> mMovies;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViews();
        setupRecyclerView();
        onRefresh();
    }

    private void findViews(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_home);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
    }

    private void setupRecyclerView(){
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieListAdapter = new MovieListAdapter(this);
        mRecyclerView.setAdapter(mMovieListAdapter);
    }

    @Override
    public void onRefresh() {
        Server.getPopularMovies(new PopularMoviesListener());
    }

    @Override
    public void onClick(String weatherForDay) {

    }

    public class PopularMoviesListener implements Server.ServerListener{

        @Override
        public void serverResponse(String response) {
            Log.d("Server Response", response);
            ArrayList<Movie> movies = new ArrayList<>();

            mMovies = Movie.listFromJson(response);

            onRefresh();
        }
    }
}
