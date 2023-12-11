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

// Declaring a WebServlet called SingleMovieServlet, which maps to url "/api/single-movie"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            String query = "SELECT m.id, m.title, m.year, m.director, r.rating " +
            "FROM movies AS m, ratings AS r " +
             "WHERE r.movieId=m.id AND m.id=?";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {

                String movieId = rs.getString("id");
                String movieTitle = rs.getString("title");
                String movieYear = rs.getString("year");
                String movieDirector = rs.getString("director");
                String movieRating = rs.getString("rating");

                System.out.println("NEED GENRE");
                //Genre Query
                String query_genres = "SELECT g.name, g.id " +
                        "FROM genres_in_movies AS gm, genres AS g, movies AS m " +
                        "WHERE gm.genreId=g.id AND m.id=gm.movieId AND m.id=? " +
                        "ORDER BY g.name;";

                PreparedStatement genres = conn.prepareStatement(query_genres);

                genres.setString(1, movieId);
                ResultSet genre_rs = genres.executeQuery();
                String genre_str = "";
                String genre_ids = "";

                while (genre_rs.next()) {
                    genre_str += genre_rs.getString("name") + ", ";
                    genre_ids += genre_rs.getString("id") + ", ";
                }

                //Stars Query
//                String query_stars = "SELECT s.name, s.id " +
//                        "FROM stars_in_movies AS sm, stars AS s, movies AS m " +
//                        "WHERE sm.starId=s.id AND m.id=sm.movieId AND m.id='" + movieId + "' ";
                System.out.println("NEED STARS");
                String query_stars = "SELECT s.name, s.id \n" +
                        "FROM stars_in_movies AS sm\n" +
                        "         JOIN stars AS s ON sm.starId = s.id\n" +
                        "         JOIN movies AS m ON m.id = sm.movieId\n" +
                        "WHERE sm.starId IN (\n" +
                        "    SELECT starId\n" +
                        "    FROM stars_in_movies\n" +
                        "    WHERE movieId = ?) \n" +
                        "GROUP BY s.name, s.id\n" +
                        "ORDER BY COUNT(*) DESC, s.name;";

                PreparedStatement stars = conn.prepareStatement(query_stars);

                stars.setString(1, movieId);
                ResultSet stars_rs = stars.executeQuery();

                String stars_str = "";
                String star_ids = "";

                while (stars_rs.next()) {
                    stars_str += stars_rs.getString("name") + ", ";
                    star_ids += stars_rs.getString("id") + ", ";
                }

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("movie_year", movieYear);
                jsonObject.addProperty("movie_director", movieDirector);
                jsonObject.addProperty("movie_rating", movieRating);
                jsonObject.addProperty("movie_genre_ids", genre_ids.substring(0, genre_ids.length() - 2));
                jsonObject.addProperty("movie_genres", genre_str.substring(0, genre_str.length() - 2));
                jsonObject.addProperty("movie_stars", stars_str.substring(0, stars_str.length() - 2));
                jsonObject.addProperty("movie_star_ids", star_ids.substring(0, star_ids.length() - 2));

                jsonArray.add(jsonObject);

                //Close all connections
                genre_rs.close();
                genres.close();
                stars_rs.close();
                stars.close();
            }
            rs.close();
            statement.close();

            // Write JSON string to output
            out.write(jsonArray.toString());

            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            //Close DB
            out.close();
        }
    }

}
