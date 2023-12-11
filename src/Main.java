import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws SQLException {
        HashMap<String, String> dbMovies = new HashMap<>();
        HashMap<String, String> dbStars = new HashMap<>();
        HashMap<String, Integer> dbGenre = new HashMap<>();
        DatabaseIDs ids = null;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "mytestuser", "My6$Password")) {
            String movieQuery = "SELECT UPPER(CONCAT(title, year, director)) as k, id FROM movies;";
            String starQuery = "SELECT UPPER(CONCAT(name, IFNULL(birthYear, ''))) as k, id FROM stars;";
            String genreQuery = "SELECT UPPER(name) as k, id FROM genres;";
            String idsQuery = "SELECT movieId, starId, genreId FROM newids;";

            try (PreparedStatement statement = conn.prepareStatement(movieQuery);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String k = resultSet.getString("k");
                    String id = resultSet.getString("id");

                    dbMovies.put(k, id);
                }
            }
            try (PreparedStatement statement = conn.prepareStatement(starQuery);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String k = resultSet.getString("k");
                    String id = resultSet.getString("id");

                    dbStars.put(k, id);
                }
            }
            try (PreparedStatement statement = conn.prepareStatement(genreQuery);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String k = resultSet.getString("k");
                    int id = resultSet.getInt("id");

                    dbGenre.put(k, id);
                }
            }
            try (PreparedStatement statement = conn.prepareStatement(idsQuery);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String mId = resultSet.getString("movieId");
                    String sId = resultSet.getString("starId");
                    int gId = resultSet.getInt("genreId");

                    ids = new DatabaseIDs(mId, sId, gId);
                }
            }

        }

        // Got current db info
//        for (Map.Entry<String, Integer> entry : dbGenre.entrySet()) {
//            String genre = entry.getKey();
//            int id = entry.getValue();
//            System.out.println(genre + " id: " + id);
//        }

        long startTime = System.currentTimeMillis();
        StarParser starParser = new StarParser();
        starParser.runParsing();

        //This hashmap is the star's name to the date of birth

        //System.out.println(ids.movieId + " " + ids.starId + " " + ids.genreId);
        MovieParser movieParser = new MovieParser(dbMovies, ids, dbGenre);
        movieParser.runParsing();

        HashMap<String, Movie> movies = movieParser.movies;
        HashMap<String, Integer> stars = starParser.stars;
        StarInMovieParser starInMovieParser = new StarInMovieParser(stars, dbStars, ids);
        starInMovieParser.runParsing();
        HashMap<String, ArrayList<Star>> starInMovies = starInMovieParser.starInMovies;
        //System.out.println(ids.movieId + " " + ids.starId + " " + ids.genreId);
        for (Map.Entry<String, Movie> entry : movies.entrySet()) {
            String key = entry.getKey();
            Movie movie = entry.getValue();
            if (starInMovies.containsKey(key) && !starInMovies.get(key).isEmpty()) {
                ArrayList<Star> movieStars = starInMovies.get(key);
                movie.stars.addAll(movieStars);
            }
            //movie.printMovie();
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "mytestuser", "My6$Password")) {
            String updateIds = "UPDATE newids\n" +
                    "SET movieId = ?,\n" +
                    "    starId = ?,\n" +
                    "    genreId = ?\n" +
                    "WHERE id = 1;";
            PreparedStatement statement = conn.prepareStatement(updateIds);
            statement.setString(1, ids.movieId);
            statement.setString(2, ids.starId);
            statement.setInt(3, ids.genreId);
            statement.executeUpdate();
            statement.close();
        }
        System.out.println("Threads started!");
        System.out.println("Adding " + movies.size() + " Movies");
        ThreadXML threader = new ThreadXML();
        threader.executorRunner(movies);
        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time for StarInMovie Parser in seconds: " + ((endTime - startTime) / 1000.0));
    }
}
