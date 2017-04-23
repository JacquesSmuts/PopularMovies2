package com.jacquessmuts.popularmovies.Activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

    //TODO: handle empty state
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private Server.SortingOption mSortingOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViews();
        setupRecyclerView();
        mSortingOption = Server.SortingOption.POPULAR;
        onRefresh();
    }

    @Override
    public void onRefresh() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        //TODO: check network state either here or inside Server?
        Server.getMovies(mSortingOption, new GetMoviesListener());
    }

    @Override
    public void onClick(Movie movieObject) {
        startActivity(MovieDetailActivity.getIntent(this, movieObject));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_sort_popular:
                mSortingOption = Server.SortingOption.POPULAR;
                onRefresh();
                return true;
            case R.id.menu_sort_rating:
                mSortingOption = Server.SortingOption.RATING;
                onRefresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void findViews(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_home);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
    }

    private void setupRecyclerView(){
        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 3);
        //todo: implement swiperefreshLayout
        //todo: implement scrollListener with paging API calls
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieListAdapter = new MovieListAdapter(this);
        mRecyclerView.setAdapter(mMovieListAdapter);
    }

    public void handleServerResponse(final String response){

        //runOnUiThread needs to be done because the adapter's notifydatasetchanged only works on UI thread
        HomeActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final ArrayList<Movie> movies = Movie.listFromJson(response);
                handleServerSuccess(movies != null && movies.size() > 0);
                mLoadingIndicator.setVisibility(View.GONE);
                mMovieListAdapter.setData(movies);
            }
        });

    }

    private void handleServerSuccess(boolean success){
        if (success){
            mErrorMessageDisplay.setVisibility(View.GONE);
        } else {
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        }
    }

    private class GetMoviesListener implements Server.ServerListener{

        @Override
        public void serverResponse(String response) {
            handleServerResponse(response);
        }
    }
}
