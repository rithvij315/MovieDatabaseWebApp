import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


// Declaring a WebServlet called MovieServlet, which maps to url "/api/movies"
@WebServlet(name = "MovieServlet", urlPatterns = "/api/top20")
public class MovieServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement

            String query = "SELECT m.id, m.title, m.year, m.director, r.rating " +
                    "FROM movies AS m, ratings AS r " +
                    "WHERE m.id=r.movieId " +
                    "ORDER BY r.rating DESC LIMIT 20;";

            // Perform the query

            PreparedStatement statement = conn.prepareStatement(query);

            ResultSet rs = statement.executeQuery();


            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_id = rs.getString("m.id");
                String movie_title = rs.getString("m.title");
                String movie_year = rs.getString("m.year");
                String movie_director = rs.getString("m.director");
                Float rating = rs.getFloat("r.rating");

                //Genre Query
                String query_genres = "SELECT g.name, g.id " +
                        "FROM genres_in_movies AS gm, genres AS g, movies AS m " +
                        "WHERE gm.genreId=g.id AND m.id=gm.movieId AND m.id=? " +
                        "ORDER BY g.name " +
                        "LIMIT 3;";

                PreparedStatement genres = conn.prepareStatement(query_genres);

                genres.setString(1, movie_id);
                ResultSet genre_rs = genres.executeQuery();

                String genre_str = "";
                String genre_ids = "";

                while (genre_rs.next()) {
                    genre_str += genre_rs.getString("name") + ", ";
                    genre_ids += genre_rs.getString("id") + ", ";
                }

                //Stars Query
                String query_stars = "SELECT s.name, s.id \n" +
                        "FROM stars_in_movies AS sm\n" +
                        "         JOIN stars AS s ON sm.starId = s.id\n" +
                        "         JOIN movies AS m ON m.id = sm.movieId\n" +
                        "WHERE sm.starId IN (\n" +
                        "    SELECT starId\n" +
                        "    FROM stars_in_movies\n" +
                        "    WHERE movieId = ?) \n" +
                        "GROUP BY s.name, s.id\n" +
                        "ORDER BY COUNT(*) DESC, s.name " +
                        "LIMIT 3;";


                PreparedStatement stars = conn.prepareStatement(query_stars);

                stars.setString(1, movie_id);
                ResultSet stars_rs = stars.executeQuery();

                String stars_str = "";
                String star_ids = "";

                while (stars_rs.next()) {
                    stars_str += stars_rs.getString("name") + ", ";
                    star_ids += stars_rs.getString("id") + ", ";
                }

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_rating", rating);
                jsonObject.addProperty("movie_genres", genre_str.substring(0, genre_str.length() - 2));
                jsonObject.addProperty("movie_genre_ids", genre_ids.substring(0, genre_ids.length() - 2));
                jsonObject.addProperty("movie_stars", stars_str.substring(0, stars_str.length() - 2));
                jsonObject.addProperty("movie_star_ids", star_ids.substring(0, star_ids.length() - 2));

                jsonArray.add(jsonObject);
                genre_rs.close();
                genres.close();
                stars_rs.close();
                stars.close();
            }
            rs.close();
            statement.close();


            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());

            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            //Close DB Connection
            out.close();
        }
    }
}
