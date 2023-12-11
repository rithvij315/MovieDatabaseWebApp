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

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "MetadataServlet", urlPatterns = "/_dashboard/api/metadata")
public class MetadataServlet extends HttpServlet {
    private static final long serialVersionUID = 5L;

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

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {


            String query = "SELECT\n" +
                    "    t.TABLE_NAME,\n" +
                    "    c.COLUMN_NAME,\n" +
                    "    c.DATA_TYPE\n" +
                    "FROM\n" +
                    "    INFORMATION_SCHEMA.COLUMNS AS c, information_schema.tables AS t\n" +
                    "WHERE c.TABLE_NAME=t.TABLE_NAME AND table_type='BASE TABLE' AND t.table_schema = 'moviedb';";

            PreparedStatement statement = conn.prepareStatement(query);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonObject json = new JsonObject();
            // Iterate through each row of rs
            while (rs.next()) {

                String tableName = rs.getString("table_name");
                String colName = rs.getString("column_name");
                String dataType = rs.getString("data_type");

                if (!json.has(tableName)) {
                    JsonObject columnInfo = new JsonObject();
                    columnInfo.addProperty(colName, dataType);
                    json.add(tableName, columnInfo);
                } else {
                    JsonObject columnInfo = json.getAsJsonObject(tableName);
                    columnInfo.addProperty(colName, dataType);
                }


            }
            rs.close();
            statement.close();

            // Write JSON string to output
            out.write(json.toString());
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
