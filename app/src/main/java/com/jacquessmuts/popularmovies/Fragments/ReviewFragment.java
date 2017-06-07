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

import com.jacquessmuts.popularmovies.Models.Review;
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
public class ReviewFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_REVIEWS = "reviews";

    private int columnCount = 1;

    private List<Review> reviews;

    private ReviewFragment.OnListFragmentInteractionListener listener;
    private ReviewRecyclerViewAdapter adapter;

    @BindView(R.id.recyclerview_reviews) RecyclerView recyclerview_reviews;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ReviewFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ReviewFragment newInstance(int columnCount, ReviewFragment.OnListFragmentInteractionListener listener, ArrayList<Review> reviews) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putParcelableArrayList(ARG_REVIEWS, reviews);
        fragment.setArguments(args);
        fragment.setListener(listener);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            columnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            reviews = getArguments().getParcelableArrayList(ARG_REVIEWS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        ButterKnife.bind(this, view);

        // Set the adapter
        Context context = view.getContext();
        if (columnCount <= 1) {
            recyclerview_reviews.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerview_reviews.setLayoutManager(new GridLayoutManager(context, columnCount));
        }
        adapter = new ReviewRecyclerViewAdapter(reviews, listener);
        recyclerview_reviews.setAdapter(adapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
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

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        if (adapter != null){
            adapter.setReviews(reviews);
        } else {
            adapter = new ReviewRecyclerViewAdapter(reviews, listener);
        }
    }

    public void setListener(ReviewFragment.OnListFragmentInteractionListener mListener) {
        this.listener = mListener;
    }

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
        void onListFragmentInteraction(Review item);
    }
}
