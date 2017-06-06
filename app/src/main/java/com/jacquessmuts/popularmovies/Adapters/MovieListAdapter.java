package com.jacquessmuts.popularmovies.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jacquessmuts.popularmovies.Models.Movie;
import com.jacquessmuts.popularmovies.R;
import com.jacquessmuts.popularmovies.Utils.Server;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Jacques Smuts on 2017/04/20.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieListViewHolder> {

    private ArrayList<Movie> mMovieList;

    private final MovieListOnClickHandler mClickHandler;

    public interface MovieListOnClickHandler {
        void onClick(Movie movieObject);
    }


    public MovieListAdapter(MovieListOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class MovieListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mImageMoviePoster;

        public MovieListViewHolder(View view) {
            super(view);
            mImageMoviePoster = (ImageView) view.findViewById(R.id.image_movie_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Movie movie = mMovieList.get(getAdapterPosition());
            mClickHandler.onClick(movie);
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
        Movie movieItem = mMovieList.get(position);

        Context context = movieListViewHolder.mImageMoviePoster.getContext();
        Picasso.with(context)
                .load(Server.buildImageUrl(context, movieItem.getPoster_path()))
                .placeholder(android.R.drawable.stat_sys_download)
                .into(movieListViewHolder.mImageMoviePoster);
    }


    @Override
    public int getItemCount() {
        if (null == mMovieList) return 0;
        return mMovieList.size();
    }

    public void setData(ArrayList<Movie> movieList) {
        mMovieList = movieList;
        notifyDataSetChanged();
    }

    public void addData(ArrayList<Movie> movieList){
        mMovieList.addAll(movieList);
        notifyDataSetChanged();
    }
}
