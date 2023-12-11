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
import java.sql.*;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "AddMovieServlet", urlPatterns = "/_dashboard/api/add-movie")
public class AddMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 5L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/master");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        String title = request.getParameter("title");
        int year = Integer.parseInt(request.getParameter("year"));
        String director = request.getParameter("director");
        String star = request.getParameter("star");
        Integer birthYear = null;
        String birthYearStr = request.getParameter("birth-year");
        if (birthYearStr != null && !birthYearStr.isEmpty()) {
            birthYear = Integer.parseInt(birthYearStr);
        }
        String genre = request.getParameter("genre");

        System.out.println(title);
        System.out.println(year);
        System.out.println(director);
        System.out.println(star);
        System.out.println(birthYear);
        System.out.println(genre);


        // Output stream to STDOUT

        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            CallableStatement addMovie = conn.prepareCall("{call add_movie(?, ?, ?, ?, ?, ?)}");


            addMovie.setString(1, title);
            addMovie.setInt(2, year);
            addMovie.setString(3, director);
            addMovie.setString(4, star);
            if (birthYear != null) {
                addMovie.setInt(5, birthYear);
            } else {
                // If param1 is null, setNull is used
                addMovie.setNull(5, java.sql.Types.INTEGER);
            }
            addMovie.setString(6, genre);


            // Perform the query
            boolean hasResults = addMovie.execute();

            JsonObject json = new JsonObject();

            System.out.println("hasResults: " + hasResults);



            if (!hasResults) {
                json.addProperty("message", "failure");
            } else {
                //return message that the movie was added successfully
                // should show movie id, star id, and genre id
                ResultSet rs = addMovie.getResultSet();

                // Iterate through each row of rs
                while (rs.next()) {
                    // new_movie_id, new_star_id, genre_id;
                    String movie_id = rs.getString("new_movie_id");
                    String star_id = rs.getString("new_star_id");
                    int genre_id = rs.getInt("genre_id");

                    // Create a JsonObject based on the data we retrieve from rs
                    json.addProperty("movie_id", movie_id);
                    json.addProperty("star_id", star_id);
                    json.addProperty("genre_id", genre_id);
                    json.addProperty("message", "success");

                }
                rs.close();
            }

            addMovie.close();


            // Write JSON string to output

            out.write(json.toString());

            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (SQLException e) {
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
