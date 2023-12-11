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
@WebServlet(name = "FullTextServlet", urlPatterns = "/api/fulltext")
public class FullTextServlet extends HttpServlet {
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

            String search = request.getParameter("query");
            System.out.println(search);
            JsonArray jsonArray = new JsonArray();

            if (search == null || search.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            String query = "SELECT id, title, year FROM movies\n" +
                    "WHERE MATCH(title) AGAINST (? IN BOOLEAN MODE) LIMIT 10;";

            // Perform the query
            String[] words = search.split(" ");
            String identifier = "";

            for (String word : words) {
                identifier += "+" + word + "* ";
            }

            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1, identifier);

            System.out.println(statement.toString());
            System.out.println(statement);
            ResultSet rs = statement.executeQuery();




            // Iterate through each row of rs
            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                JsonObject additionalJsonObject = new JsonObject();
                jsonObject.addProperty("value", movie_title + " (" + movie_year + ")");
                additionalJsonObject.addProperty("movie_id", movie_id);
                jsonObject.add("data", additionalJsonObject);

                jsonArray.add(jsonObject);
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
