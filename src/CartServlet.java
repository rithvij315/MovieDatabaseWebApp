import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        long lastAccessTime = session.getLastAccessedTime();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());

        HashMap<String, MovieInCart> previousItems = (HashMap<String, MovieInCart>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new HashMap<String, MovieInCart>();
        }
        // Log to localhost log
        request.getServletContext().log("getting " + previousItems.size() + " items");
        JsonArray previousItemsJsonArray = new JsonArray();
        int flag = -1;
        for (Map.Entry<String, MovieInCart> entry : previousItems.entrySet()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("item_name", entry.getKey());
            jsonObject.addProperty("item_count", entry.getValue().count);
            jsonObject.addProperty("item_cost", entry.getValue().cost);
            flag = entry.getValue().saleId;
            jsonObject.addProperty("sale_id", flag);
            previousItemsJsonArray.add(jsonObject);
        }
        responseJsonObject.add("previousItems", previousItemsJsonArray);

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());

        if (flag != -1) {
            previousItems.clear();
        }
    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String item = request.getParameter("item");
        String type = request.getParameter("type");
        String movieId = request.getParameter("id");
        System.out.println(movieId);
        HttpSession session = request.getSession();

        HashMap<String, MovieInCart> previousItems =
                (HashMap<String, MovieInCart>) session.getAttribute("previousItems");

        if (previousItems == null) {
//            previousItems = new ArrayList<MovieInCart>();
            previousItems = new HashMap<String, MovieInCart>();
            previousItems.put(item, new MovieInCart(item, 1, 5.00, movieId));
            session.setAttribute("previousItems", previousItems);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (previousItems) {
                if (previousItems.containsKey(item) && "sub".equals(type)) {
                    if (previousItems.get(item).count != 1) {
                        previousItems.get(item).count--;
                    }
                } else if (previousItems.containsKey(item) && "del".equals(type)) {
                    previousItems.remove(item);
                } else if (previousItems.containsKey(item)) {
                    previousItems.get(item).count ++;
                }else {
                    previousItems.put(item, new MovieInCart(item, 1, 5.00, movieId));
                }
            }
        }

        JsonObject responseJsonObject = new JsonObject();

        JsonArray previousItemsJsonArray = new JsonArray();
        for (Map.Entry<String, MovieInCart> entry : previousItems.entrySet()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("item_name", entry.getKey());
            jsonObject.addProperty("item_count", entry.getValue().count);
            jsonObject.addProperty("item_cost", entry.getValue().cost);
            jsonObject.addProperty("sale_id", entry.getValue().saleId);
            previousItemsJsonArray.add(jsonObject);
        }
        responseJsonObject.add("previousItems", previousItemsJsonArray);


        response.getWriter().write(responseJsonObject.toString());
    }
}