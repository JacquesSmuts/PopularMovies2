package com.jacquessmuts.popularmovies.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jacquessmuts.popularmovies.Models.Trailer;
import com.jacquessmuts.popularmovies.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TrailerFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_TRAILERS = "trailers";

    private int columnCount = 1;

    private List<Trailer> trailers;

    private OnListFragmentInteractionListener listener;
    private TrailerRecyclerViewAdapter adapter;

    @BindView(R.id.recyclerview_trailer) RecyclerView recyclerview_trailer;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TrailerFragment() {
    }

    public static TrailerFragment newInstance(int columnCount, OnListFragmentInteractionListener listener, ArrayList<Trailer> trailers) {
        TrailerFragment fragment = new TrailerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putParcelableArrayList(ARG_TRAILERS, trailers);
        fragment.setArguments(args);
        fragment.setListener(listener);
        return fragment;
    }

    /**
     * OVERRIDES
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            columnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            trailers = getArguments().getParcelableArrayList(ARG_TRAILERS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trailer_list, container, false);
        ButterKnife.bind(this, view);

        // Set the adapter
        Context context = view.getContext();
        if (columnCount <= 1) {
            recyclerview_trailer.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerview_trailer.setLayoutManager(new GridLayoutManager(context, columnCount));
        }
        adapter = new TrailerRecyclerViewAdapter(trailers, listener);
        recyclerview_trailer.setAdapter(adapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            listener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * METHODS
     */

    public void setTrailers(List<Trailer> mTrailers) {
        this.trailers = mTrailers;
        adapter.setTrailers(trailers);
    }

    public void setListener(OnListFragmentInteractionListener mListener) {
        this.listener = mListener;
    }

    /**
     * INTERFACES
     */

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onTrailerClick(Trailer item);
    }
}
