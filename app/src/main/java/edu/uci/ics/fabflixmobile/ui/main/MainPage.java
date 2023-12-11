package edu.uci.ics.fabflixmobile.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;

import java.util.ArrayList;

public class MainPage extends AppCompatActivity {

    private EditText searchEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        // Initialize the EditText
        searchEditText = findViewById(R.id.searchEditText);

        // Get the Button reference and set its OnClickListener
        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call a method to handle the search
                handleSearch();
            }
        });
    }

    public interface OnMoviesLoadedCallback {
        void onMoviesLoaded(ArrayList<Movie> movies);
    }

    private void handleSearch() {
        // Get the text from the EditText
        String searchText = searchEditText.getText().toString();

        // Create an Intent to start the MovieListActivity
        Intent movieListIntent = new Intent(MainPage.this, MovieListActivity.class);

        // Add the search text as an extra to the Intent
        movieListIntent.putExtra("searchText", searchText);

        // Start the MovieListActivity
        startActivity(movieListIntent);
    }
}