package com.jacquessmuts.popularmovies.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacquessmuts.popularmovies.Fragments.TrailerFragment.OnListFragmentInteractionListener;
import com.jacquessmuts.popularmovies.Models.Trailer;
import com.jacquessmuts.popularmovies.R;
import com.jacquessmuts.popularmovies.Utils.Server;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
                .inflate(R.layout.list_item_trailer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = trailers.get(position);
        holder.trailer_name.setText(holder.item.getName());
        holder.trailer_site.setText(holder.item.getSite());

        Context context = holder.trailer_image.getContext();

        Picasso.with(context)
                .load(Server.buildYouTubeImageUrl(holder.item.getKey()))
                .placeholder(android.R.drawable.stat_sys_download)
                .into(holder.trailer_image);

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
        @BindView(R.id.trailer_name) TextView trailer_name;
        @BindView(R.id.trailer_site) TextView trailer_site;
        @BindView(R.id.trailer_image) ImageView trailer_image;

        public Trailer item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + trailer_name.getText() + "'";
        }
    }
}
