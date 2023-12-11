package edu.uci.ics.fabflixmobile.ui.singlemovie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.main.MainPage;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListViewAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingleMovie extends AppCompatActivity {

//    private final String host = "10.0.2.2";
//    private final String port = "8080";
//    private final String domain = "cs122b_i_3cs_war";
//    private final String baseURL = "http://" + host + ":" + port + "/" + domain;

    private final String host = "18.118.159.241";
    private final String port = "8443";
    private final String domain = "cs122b-i-3cs";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    private String movieTitle;
    private short movieYear;
    private String movieDirector;
    private String movieGenres;
    private String movieStars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String movieId = getIntent().getStringExtra("id");
        getMovieData(movieId);
    }

    @SuppressLint("SetTextI18n")
    public void getMovieData(String id) {
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

//        RequestFuture<String> future = RequestFuture.newFuture();
        // request type is POST
        final StringRequest singleMovieRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/single-movie?id=" + id,
                response -> {
                    JSONArray jsonResponse = null;
                    Log.d("Single Movie Data", response);
                    try {
                        jsonResponse = new JSONArray(response);
                        JSONObject jsonMovie = jsonResponse.getJSONObject(0);

                        // Extract movie information from the JSON object
                        movieTitle = jsonMovie.getString("movie_title");
                        movieYear = (short) jsonMovie.getInt("movie_year");
                        movieDirector = jsonMovie.getString("movie_director");
                        movieGenres = jsonMovie.getString("movie_genres");
                        movieStars = jsonMovie.getString("movie_stars");

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    updateUI();
                },
                error -> {
                    // error
                    Log.d("Grabbing Single Movie Error", error.toString());
                }) {
        };
        // important: queue.add is where the login request is actually sent
        queue.add(singleMovieRequest);
    }

    private void updateUI() {
        setContentView(R.layout.single_movie);

        TextView titleTextView = findViewById(R.id.title);
        TextView yearTextView = findViewById(R.id.year);
        TextView directorTextView = findViewById(R.id.director);
        TextView genresTextView = findViewById(R.id.genres);
        TextView starsTextView = findViewById(R.id.stars);

        titleTextView.setText(movieTitle);
        yearTextView.setText(String.valueOf(movieYear));
        directorTextView.setText(movieDirector);
        genresTextView.setText("Genres: " + movieGenres);
        starsTextView.setText("Stars: " + movieStars);
    }

}