package com.jacquessmuts.popularmovies.Fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jacquessmuts.popularmovies.Fragments.TrailerFragment.OnListFragmentInteractionListener;
import com.jacquessmuts.popularmovies.Fragments.dummy.DummyContent.DummyItem;
import com.jacquessmuts.popularmovies.Models.Trailer;
import com.jacquessmuts.popularmovies.R;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TrailerRecyclerViewAdapter extends RecyclerView.Adapter<TrailerRecyclerViewAdapter.ViewHolder> {

    private List<Trailer> trailers;
    private OnListFragmentInteractionListener listener;

    public TrailerRecyclerViewAdapter(List<Trailer> items, OnListFragmentInteractionListener listener) {
        this.trailers = items;
        this.listener = listener;
    }

    public void setTrailers(List<Trailer> trailers){
        this.trailers = trailers;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_trailer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = trailers.get(position);
        holder.idView.setText(trailers.get(position).getName());
        holder.contentView.setText(trailers.get(position).getSite());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onTrailerClick(holder.item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (trailers != null){
            size = trailers.size();
        }
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView idView;
        public final TextView contentView;
        public Trailer item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            idView = (TextView) view.findViewById(R.id.id);
            contentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + contentView.getText() + "'";
        }
    }
}
