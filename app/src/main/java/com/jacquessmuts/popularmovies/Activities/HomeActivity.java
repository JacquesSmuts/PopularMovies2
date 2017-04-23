package com.jacquessmuts.popularmovies.Activities;

import android.support.v4.widget.NestedScrollView;
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
import com.jacquessmuts.popularmovies.Utils.Util;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements MovieListAdapter.MovieListOnClickHandler, SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private MovieListAdapter mMovieListAdapter;
    private ScrollPagingListener mScrollListener;

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
        setLoading(true);
        getData(1);
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
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh_home);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_home);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
    }

    private void setupRecyclerView(){
        mLayoutManager = new GridLayoutManager(this, 3);
        mSwipeRefresh.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieListAdapter = new MovieListAdapter(this);
        mRecyclerView.setAdapter(mMovieListAdapter);

        mScrollListener = new ScrollPagingListener(mLayoutManager);
        mRecyclerView.addOnScrollListener(mScrollListener);
    }

    private void getData(int pageNumber){
        if (pageNumber > 1) {
            mSwipeRefresh.setRefreshing(true);
        } else {
            mRecyclerView.removeOnScrollListener(mScrollListener);
            mScrollListener = new ScrollPagingListener(mLayoutManager);
            mRecyclerView.addOnScrollListener(mScrollListener);
        }
        if (Util.getConnected(this)) {
            Server.getMovies(mSortingOption, pageNumber, new GetMoviesListener());
        } else {
            handleServerSuccess(false);
        }
    }

    private void setLoading(boolean isLoading){
        if (isLoading){
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mLoadingIndicator.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void handleServerResponse(final String response){

        //runOnUiThread needs to be done because the adapter's notifydatasetchanged only works on UI thread
        HomeActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final ArrayList<Movie> movies = Movie.listFromJson(response);
                setLoading(false);
                handleServerSuccess(movies != null && movies.size() > 0);
                if (mSwipeRefresh.isRefreshing()){
                    mMovieListAdapter.addData(movies);
                    mSwipeRefresh.setRefreshing(false);
                } else {
                    mMovieListAdapter.setData(movies);
                }
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

    private class ScrollPagingListener extends RecyclerView.OnScrollListener {

        private int previousTotal = 0; // The total number of items in the dataset after the last load
        private boolean loading = true; // True if we are still waiting for the last set of data to load.
        private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
        int firstVisibleItem, visibleItemCount, totalItemCount;

        private int current_page = 1;

        private GridLayoutManager mLayoutManager;

        public ScrollPagingListener(GridLayoutManager linearLayoutManager) {
            this.mLayoutManager = linearLayoutManager;
        }

        public void reset(){
            previousTotal = 0;
            loading = false;
            current_page = 1;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = mLayoutManager.getItemCount();
            firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold)) {
                // End has been reached
                current_page++;

                getData(current_page);

                loading = true;
            }
        }
    }
}
