import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    private static final long serialVersionUID = 6L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String firstName = request.getParameter("first_name");
        String lastName = request.getParameter("last_name");
        String ccid = request.getParameter("cc_number");
        String expDate = request.getParameter("exp_date");

        System.out.println("Date:" + expDate);

        String currDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT c.id\n" +
                    "FROM customers AS c, creditcards AS cc\n" +
                    "WHERE cc.id=? AND cc.firstName=? AND cc.lastName=? AND cc.expiration=?;";


            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1, ccid);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, expDate);

            ResultSet validation = statement.executeQuery();

            int rs_cid = -1;

            while (validation.next()) {
                rs_cid = validation.getInt("id");
            }

            statement.close();
            validation.close();

            JsonObject responseJsonObject = new JsonObject();

            if (rs_cid != -1) {
                HttpSession session = request.getSession();
                // Login success:
                // set this user into the session
                // Insert into sales
                //For movie in sessions:
                HashMap<String, MovieInCart> previousItems =
                        (HashMap<String, MovieInCart>) session.getAttribute("previousItems");

                for (Map.Entry<String, MovieInCart> entry : previousItems.entrySet()) {
                    String salesUpdate = "INSERT INTO sales (customerId, movieId, saleDate)\n" +
                            "VALUES (?, ?, ?);";

                    PreparedStatement updateSales = conn.prepareStatement(salesUpdate);

                    updateSales.setInt(1,rs_cid);
                    updateSales.setString(2,entry.getValue().movieId);
                    updateSales.setString(3,currDate);

                    updateSales.executeUpdate();

                    String salesIdQuery = "SELECT LAST_INSERT_ID() as saleId;";
                    PreparedStatement saleIdStatement = conn.prepareStatement(salesIdQuery);
                    ResultSet rs_saleId = saleIdStatement.executeQuery();
                    while (rs_saleId.next()) {
                        previousItems.get(entry.getKey()).saleId = rs_saleId.getInt("saleId");
                    }
                    updateSales.close();
                    saleIdStatement.close();
                }


                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "invalid credit card");
            }
            response.getWriter().write(responseJsonObject.toString());

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            response.getWriter().write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            //Close DB
            response.getWriter().close();
        }
    }
}
