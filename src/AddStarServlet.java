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
@WebServlet(name = "AddStarServlet", urlPatterns = "/_dashboard/api/add-star")
public class AddStarServlet extends HttpServlet {
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

        String name = request.getParameter("name");
        Integer birthYear = null;
        String birthYearStr = request.getParameter("birth-year");
        if (birthYearStr != null && !birthYearStr.isEmpty()) {
            birthYear = Integer.parseInt(birthYearStr);
        }

        System.out.println(name);
        System.out.println(birthYear);


        // Output stream to STDOUT

        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            CallableStatement addStar = conn.prepareCall("{call add_star(?, ?)}");


            addStar.setString(1, name);
            if (birthYear != null) {
                addStar.setInt(2, birthYear);
            } else {
                // If param1 is null, setNull is used
                addStar.setNull(2, java.sql.Types.INTEGER);
            }


            // Perform the query
            boolean hasResults = addStar.execute();

            JsonObject json = new JsonObject();

            System.out.println("hasResults: " + hasResults);



            if (!hasResults) {
                json.addProperty("message", "failure");
            } else {
                ResultSet rs = addStar.getResultSet();

                // Iterate through each row of rs
                while (rs.next()) {;
                    String star_id = rs.getString("new_star_id");

                    // Create a JsonObject based on the data we retrieve from rs
                    json.addProperty("star_id", star_id);

                }
                rs.close();
            }

            addStar.close();


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
