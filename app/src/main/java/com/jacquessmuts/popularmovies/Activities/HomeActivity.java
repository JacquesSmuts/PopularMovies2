package com.jacquessmuts.popularmovies.Activities;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jacquessmuts.popularmovies.R;
import com.jacquessmuts.popularmovies.Utils.Server;

public class HomeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Server.getPopularMovies(new PopularMoviesListener());
        setupRecyclerView();
    }

    private void setupRecyclerView(){
        //TODO do that whole recyclerview setup thing throughout
    }

    @Override
    public void onRefresh() {
        //TODO: when the user refreshes through pull-to-refresh, or after data gets successfully loaded
    }

    public class PopularMoviesListener implements Server.ServerListener{

        @Override
        public void serverResponse(String response) {
            Log.d("Server Response", response);
            //TODO Handle ServerResponse here. Use Gson.
        }
    }
}
