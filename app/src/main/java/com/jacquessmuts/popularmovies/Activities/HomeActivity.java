package com.jacquessmuts.popularmovies.Activities;

import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import com.jacquessmuts.popularmovies.Adapters.MovieListAdapter;
import com.jacquessmuts.popularmovies.Data.MovieContract;
import com.jacquessmuts.popularmovies.Models.Movie;
import com.jacquessmuts.popularmovies.R;
import com.jacquessmuts.popularmovies.Utils.Server;
import com.jacquessmuts.popularmovies.Utils.Util;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;

public class HomeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, MovieListAdapter.MovieListOnClickHandler, SwipeRefreshLayout.OnRefreshListener {

    public static final String KEY_LAYOUT_INSTANCE_STATE = "key_layout_instance_state";

    @BindView(R.id.swiperefresh_home) SwipeRefreshLayout swiperefresh_home;
    @BindView(R.id.recyclerview_home) RecyclerView recyclerview_home;
    private GridLayoutManager layoutManager;
    private MovieListAdapter movieListAdapter;
    private ScrollPagingListener scrollListener;

    @BindView(R.id.pb_loading_indicator) ProgressBar pb_loading_indicator;

    @State Server.SortingOption sortingOption;
    @State ArrayList<Movie> movieList;
    @State int scrollPosition;
    private Cursor cursor;

    /*
    * The columns of data that we are interested in displaying within the list
    */
    public static final String[] MAIN_MOVIES_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_IS_FAVORITE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE
    };

    /*
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_ORIGINAL_TITLE = 1;
    public static final int INDEX_OVERVIEW = 2;
    public static final int INDEX_POSTER_PATH = 3;
    public static final int INDEX_IS_FAVORITE = 4;
    public static final int INDEX_VOTE_AVERAGE = 5;

    private static final int ID_MOVIE_LOADER = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sortingOption = Server.SortingOption.POPULAR;
        Icepick.restoreInstanceState(this, savedInstanceState);
        ButterKnife.bind(this);
        setupRecyclerView();
        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);

        if (savedInstanceState == null){
            onRefresh();
        } else {
            movieListAdapter.setData(movieList);
            layoutManager.scrollToPosition(scrollPosition);
        }
    }

    @Override
    public void onRefresh() {
        setLoading(true);
        getData(1);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_LAYOUT_INSTANCE_STATE, layoutManager.onSaveInstanceState());
        movieList = movieListAdapter.getMovieList();
        scrollPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        Icepick.saveInstanceState(this, outState);
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
                sortingOption = Server.SortingOption.POPULAR;
                onRefresh();
                return true;
            case R.id.menu_sort_rating:
                sortingOption = Server.SortingOption.RATING;
                onRefresh();
                return true;
            case R.id.menu_sort_favorite:
                sortingOption = Server.SortingOption.FAVORITE;
                onRefresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupRecyclerView(){
        int columns = 3;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            columns = 5;
        }
        if (Util.isTablet(this)){
            columns = columns+1;
        }

        layoutManager = new GridLayoutManager(this, columns);
        recyclerview_home.setLayoutManager(layoutManager);

        int marginInPixels = (int) getResources().getDimension(R.dimen.grid_layout_margin);
        recyclerview_home.addItemDecoration(new GridSpacingItemDecoration(columns, marginInPixels, true));
        recyclerview_home.setHasFixedSize(true);
        swiperefresh_home.setOnRefreshListener(this);

        movieListAdapter = new MovieListAdapter(this);
        recyclerview_home.setAdapter(movieListAdapter);

        scrollListener = new ScrollPagingListener(layoutManager);
        recyclerview_home.addOnScrollListener(scrollListener);
    }

    private void getData(int pageNumber){

        if (sortingOption == Server.SortingOption.FAVORITE && cursor != null){

            //Data is loaded from database and handled through cursor
            movieListAdapter.swapCursor(cursor);
            recyclerview_home.removeOnScrollListener(scrollListener);
            recyclerview_home.smoothScrollToPosition(0);
            setLoading(false);
            handleServerSuccess(cursor.getCount() > 0);
        } else {

            //Data is loaded from server and handled through ArrayList
            if (pageNumber > 1) {
                swiperefresh_home.setRefreshing(true);
            } else {
                recyclerview_home.removeOnScrollListener(scrollListener);
                scrollListener = new ScrollPagingListener(layoutManager);
                recyclerview_home.addOnScrollListener(scrollListener);
            }
            movieListAdapter.swapCursor(null);
            if (Util.getConnected(this)) {
                Server.getMovies(sortingOption, pageNumber, new GetMoviesListener());
            } else {
                handleServerSuccess(false);
            }
        }
    }

    private void setLoading(boolean isLoading){
        if (isLoading){
            pb_loading_indicator.setVisibility(View.VISIBLE);
            recyclerview_home.setVisibility(View.GONE);
        } else {
            pb_loading_indicator.setVisibility(View.GONE);
            recyclerview_home.setVisibility(View.VISIBLE);
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
                if (swiperefresh_home.isRefreshing()){
                    movieListAdapter.addData(movies);
                    swiperefresh_home.setRefreshing(false);
                } else {
                    movieListAdapter.setData(movies);
                }
            }
        });

    }

    private void handleServerSuccess(boolean success){
        if (!success){
            Util.errorMessageInternet(this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {

            case ID_MOVIE_LOADER:
                /* URI for all rows of weather data in our weather table */
                Uri forecastQueryUri = MovieContract.MovieEntry.CONTENT_URI;
                /* Sort order: Ascending by date */
                //String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
                /*
                 * A SELECTION in SQL declares which rows you'd like to return. In our case, we
                 * want all weather data from today onwards that is stored in our weather table.
                 * We created a handy method to do that in our WeatherEntry class.
                 */
                String selection = MovieContract.MovieEntry.getAllFavorites();

                return new CursorLoader(this,
                        forecastQueryUri,
                        MAIN_MOVIES_PROJECTION,
                        selection,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    /**
     * Called when a Loader has finished loading its data.
     *
     * NOTE: There is one small bug in this code. If no data is present in the cursor do to an
     * initial load being performed with no access to internet, the loading indicator will show
     * indefinitely, until data is present from the ContentProvider. This will be fixed in a
     * future version of the course.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursor = data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieListAdapter.swapCursor(null);
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

    /**
     * https://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing/30701422
     * Adds even spacing betwen items in listview
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}
