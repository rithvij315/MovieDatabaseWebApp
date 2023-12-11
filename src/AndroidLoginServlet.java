import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "AndroidLoginServlet", urlPatterns = "/api/androidLogin")
public class AndroidLoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    private static final long serialVersionUID = 4L;

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
        PrintWriter out = response.getWriter();
        StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String userType = request.getParameter("user");

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        //response.setContentType("application/json");
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "Please complete the reCAPTCHA");
            response.getWriter().write(responseJsonObject.toString());

            return;
        }

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            String table = "";
            if ("employee".equals(userType)) {
                table = "employees";
            } else {
                table = "customers";
            }
            String query = "SELECT c.email, c.password " +
                    "FROM " + table + " AS c " +
                    "WHERE c.email= ?;";

            // Declare our statement

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);

            ResultSet validation = statement.executeQuery();

            String rs_username = "";
            String rs_password = "";

            while (validation.next()) {
                rs_username = validation.getString("email");
                rs_password = validation.getString("password");
            }

            statement.close();
            validation.close();

            JsonObject responseJsonObject = new JsonObject();
//            System.out.println(password + "||" + rs_password);
            if (username.equals(rs_username) && passwordEncryptor.checkPassword(password, rs_password)) {
                // Login success:
                // set this user into the session

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
                if ("employee".equals(userType)) {
                    request.getSession().setAttribute("employee", new User(username));
                } else {
                    request.getSession().setAttribute("user", new User(username));
                }

            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");
                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                if (!username.equals(rs_username)) {
                    responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
                } else {
                    responseJsonObject.addProperty("message", "incorrect password");
                }
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