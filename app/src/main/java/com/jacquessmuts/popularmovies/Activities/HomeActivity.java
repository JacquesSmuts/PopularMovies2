package com.jacquessmuts.popularmovies.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jacquessmuts.popularmovies.R;
import com.jacquessmuts.popularmovies.Utils.Server;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Server.getPopularMovies(new PopularMoviesListener());
    }

    public class PopularMoviesListener implements Server.ServerListener{

        @Override
        public void serverResponse(String response) {
            Log.d("Server Response", response);
        }
    }
}
