package com.jacquessmuts.popularmovies.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jacquessmuts.popularmovies.Activities.HomeActivity;
import com.jacquessmuts.popularmovies.Models.Movie;
import com.jacquessmuts.popularmovies.R;
import com.jacquessmuts.popularmovies.Utils.Server;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Jacques Smuts on 2017/04/20.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieListViewHolder> {

    private ArrayList<Movie> movieList;
    private Cursor cursor;

    private final MovieListOnClickHandler clickHandler;

    public interface MovieListOnClickHandler {
        void onClick(Movie movieObject);
    }


    public MovieListAdapter(MovieListOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    public class MovieListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView imageMoviePoster;

        public MovieListViewHolder(View view) {
            super(view);
            imageMoviePoster = (ImageView) view.findViewById(R.id.image_movie_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Movie movie;

            if (cursor != null){
                int adapterPosition = getAdapterPosition();
                cursor.moveToPosition(adapterPosition);

                movie = new Movie();
                movie.setId(cursor.getInt(HomeActivity.INDEX_MOVIE_ID));
                movie.setOriginal_title(cursor.getString(HomeActivity.INDEX_ORIGINAL_TITLE));
                movie.setOverview(cursor.getString(HomeActivity.INDEX_OVERVIEW));
                movie.setPoster_path(cursor.getString(HomeActivity.INDEX_POSTER_PATH));
                movie.setFavorite(cursor.getInt(HomeActivity.INDEX_IS_FAVORITE) > 0);
                movie.setVote_average(cursor.getDouble(HomeActivity.INDEX_VOTE_AVERAGE));

            } else {
                movie = movieList.get(getAdapterPosition());
            }
            clickHandler.onClick(movie);
        }
    }

    @Override
    public MovieListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_movie, viewGroup, false);
        return new MovieListViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param movieListViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(MovieListViewHolder movieListViewHolder, int position) {
        String posterPath = "";
        if (cursor != null){
            cursor.moveToPosition(position);
            posterPath = cursor.getString(HomeActivity.INDEX_POSTER_PATH);
        } else {
            Movie movieItem = movieList.get(position);
            posterPath = movieItem.getPoster_path();
        }


        Context context = movieListViewHolder.imageMoviePoster.getContext();
        Picasso.with(context)
                .load(Server.buildImageUrl(context, posterPath))
                .placeholder(android.R.drawable.stat_sys_download)
                .into(movieListViewHolder.imageMoviePoster);
    }


    @Override
    public int getItemCount() {
        int size = 0;
        if (cursor != null) {
            size = cursor.getCount();
        } else if (movieList != null){
            size = movieList.size();
        }
        return size;
    }

    public void setData(ArrayList<Movie> movieList) {
        this.movieList = movieList;
        cursor = null;
        notifyDataSetChanged();
    }

    public void swapCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public void addData(ArrayList<Movie> movieList){
        this.movieList.addAll(movieList);
        notifyDataSetChanged();
    }
}
