package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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
import edu.uci.ics.fabflixmobile.ui.singlemovie.SingleMovie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieListActivity extends AppCompatActivity {

//    private final String host = "10.0.2.2";
//    private final String port = "8080";
//    private final String domain = "cs122b_i_3cs_war";
//    private final String baseURL = "http://" + host + ":" + port + "/" + domain;
    private final String host = "18.118.159.241";
    private final String port = "8443";
    private final String domain = "cs122b-i-3cs";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;
    private int pageNum = 1;
    private String searchText = "";
    private boolean lastPage = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        searchText = getIntent().getStringExtra("searchText");
        getMovies();

        Button btnPrev = findViewById(R.id.btnPrev);
        Button btnNext = findViewById(R.id.btnNext);

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle "Prev" button click
                handlePrevButtonClick();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle "Next" button click
                handleNextButtonClick();
            }
        });

//        Log.d("Movies:", movies.toString());
//        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
//        ListView listView = findViewById(R.id.list);
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener((parent, view, position, id) -> {
//            Movie movie = movies.get(position);
//            @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//        });
    }

    @SuppressLint("SetTextI18n")
    public void getMovies() {
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

//        RequestFuture<String> future = RequestFuture.newFuture();
        // request type is POST
        final StringRequest movieRequest = new StringRequest(
            Request.Method.GET,
            baseURL + "/api/search-results?type=fulltext" + "&title=" + searchText + "&count=10" + "&page=" + pageNum,
            response -> {
                Log.d("Movie Initial response", response);
                JSONArray jsonResponse = null;
                final ArrayList<Movie> movies = new ArrayList<>();
                try {
                    jsonResponse = new JSONArray(response);
                    if (jsonResponse.length() < 10) {
                        lastPage = true;
                    }
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        JSONObject jsonMovie = jsonResponse.getJSONObject(i);

                        // Extract movie information from the JSON object
                        String movieTitle = jsonMovie.getString("movie_title");
                        short movieYear = (short) jsonMovie.getInt("movie_year");
                        String movieDirector = jsonMovie.getString("movie_director");
                        String movieGenres = jsonMovie.getString("movie_genres");
                        String movieStars = jsonMovie.getString("movie_stars");
                        String movieId = jsonMovie.getString("movie_id");

                        // Create a Movie object and add it to the list
                        Movie movie = new Movie(movieTitle, movieYear, movieDirector, movieGenres, movieStars, movieId);
                        movies.add(movie);
                        Log.d("Movie # Added: ", movieTitle);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Log.d("Movies:", movies.toString());
                MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
                ListView listView = findViewById(R.id.list);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    Movie movie = movies.get(position);
                    Intent singleMovie = new Intent(MovieListActivity.this, SingleMovie.class);

                    // Add the search text as an extra to the Intent
                    singleMovie.putExtra("id", movie.getId());

                    // Start the MovieListActivity
                    startActivity(singleMovie);
                });
            },
            error -> {
                // error
                Log.d("Grabbing Movie Error", error.toString());
            }) {
        };
        // important: queue.add is where the login request is actually sent
        queue.add(movieRequest);
    }

    private void handlePrevButtonClick() {
        // Implement logic for handling "Prev" button click
        // You can adjust the page number or perform any other action as needed
        if (pageNum != 1) {
            pageNum--;
            getMovies();
        }
    }

    private void handleNextButtonClick() {
        // Implement logic for handling "Next" button click
        // You can adjust the page number or perform any other action as needed
        if (!(lastPage)) {
            pageNum++;
            getMovies();
        }
    }
}