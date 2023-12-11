import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SearchResultsServlet", urlPatterns = "/api/search-results")
public class SearchResultsServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;
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
        long ts_start = System.nanoTime();
        long tj_start = 0;
        long tj_end = 0;
        response.setContentType("application/json");
        String type = request.getParameter("type");
        String val = request.getParameter("val");
        // Retrieve parameter id from url request.
        String movieTitle = request.getParameter("title");
        int movieYear;
        if ("".equals(request.getParameter("year")) || request.getParameter("year") == null) {
            movieYear = -1;
        } else {
            movieYear = Integer.parseInt(request.getParameter("year"));
        }
        String movieDirector = request.getParameter("director");
        String movieStar = request.getParameter("star");
        int page;
        if ("null".equals(request.getParameter("page")) || request.getParameter("page") == null) {
            page= 1;
        } else {
            page = Integer.parseInt(request.getParameter("page"));
        }
        int sortBy;
        if ("null".equals(request.getParameter("sortBy")) || request.getParameter("sortBy") == null) {
            sortBy= 1;
        } else {
            sortBy = Integer.parseInt(request.getParameter("sortBy"));
        }
        int count;
        if ("null".equals(request.getParameter("count")) || request.getParameter("count") == null) {
            count= 10;
        } else {
            count = Integer.parseInt(request.getParameter("count"));
        }
        HttpSession session = request.getSession();
        if (type == null) {
            //get from session data, check
            HashMap<String, String> resultsParams = (HashMap<String, String>) session.getAttribute("resultsParams");
            type = resultsParams.get("type");
            val = resultsParams.get("val");
            movieTitle = resultsParams.get("title");
            movieYear = Integer.parseInt(resultsParams.get("year"));
            movieDirector = resultsParams.get("director");
            movieStar = resultsParams.get("star");
            int tempPage = Integer.parseInt(resultsParams.get("page"));
            if ("null".equals(request.getParameter("page")) || request.getParameter("page") == null) {
                page = tempPage;
            }
            int sort = Integer.parseInt(resultsParams.get("sortBy"));
            if ("null".equals(request.getParameter("sortBy")) || request.getParameter("sortBy") == null) {
                sortBy = sort;
            }
            resultsParams.put("sortBy", Integer.toString(sortBy));
            int tempCount = Integer.parseInt(resultsParams.get("count"));
            if ("null".equals(request.getParameter("count")) || request.getParameter("count") == null) {
                count = tempCount;
            }
            resultsParams.put("count", Integer.toString(count));
            resultsParams.put("page", Integer.toString(page));
            session.setAttribute("resultsParams", resultsParams);
            System.out.println(resultsParams);
        } else {
            HashMap<String, String> resultsParams = (HashMap<String, String>) session.getAttribute("resultsParams");
            if (resultsParams == null) {
                resultsParams = new HashMap<String, String>();
            }
            resultsParams.put("type", type);
            resultsParams.put("val", val);
            resultsParams.put("title", movieTitle);
            resultsParams.put("year", Integer.toString(movieYear));
            resultsParams.put("director", movieDirector);
            resultsParams.put("star", movieStar);
            resultsParams.put("sortBy", Integer.toString(sortBy));
            resultsParams.put("count", Integer.toString(count));
            resultsParams.put("page", Integer.toString(page));
            session.setAttribute("resultsParams", resultsParams);
            System.out.println(resultsParams);
        }

//        System.out.println(genreId);
        String orderQuery = new StringVariations().getSorting(sortBy) + " " +
                new StringVariations().getCount(count) + " OFFSET " + ((page - 1) * count) + ";";
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = null;
            PreparedStatement statement = null;
            if (type.equals("genre")) {
                String query = "SELECT m.id, m.title, m.year, m.director, r.rating, g.name\n" +
                        "FROM movies AS m, ratings AS r, genres AS g, genres_in_movies AS gm\n" +
                        "WHERE gm.genreId=g.id AND gm.movieId=m.id AND r.movieId=m.id AND g.id=?\n";
                query += orderQuery;
                // Declare our statement
                statement = conn.prepareStatement(query);

                statement.setString(1, val);

                // Perform the query
                System.out.println("GENRE");
                rs = statement.executeQuery();
            } else if (type.equals("char")) {


                String query = "SELECT m.id, m.title, m.year, m.director, r.rating \n" +
                        "FROM movies AS m, ratings AS r\n" +
                        "WHERE r.movieId=m.id AND UPPER(m.title) ";

                if (val.equals("*")) {
                    query += "NOT REGEXP '^[a-zA-Z0-9]'";
                } else {
                    query += "LIKE '" + val + "%' ";
                }
                query += orderQuery;
                statement = conn.prepareStatement(query);
                System.out.println("CHAR");
                rs = statement.executeQuery();

            } else if(type.equals("search")) {
                //ArrayList<String> params = new ArrayList<String>();
                String[] params = new String[4];
                String query = "SELECT m.id, m.title, m.year, m.director, r.rating\n" +
                        "FROM movies AS m, ratings AS r\n" +
                        "WHERE r.movieId=m.id ";
                if (!movieTitle.isEmpty()) {
                    query += "AND UPPER(m.title) LIKE ? ";
                    //params.add("%" + movieTitle.toUpperCase() + "%");
                    params[0] = "%" + movieTitle.toUpperCase() + "%";
                }
                if (movieYear != -1) {
                    query +=  "AND m.year=? ";
                    //params.add(Integer.toString(movieYear));
                    params[1] = Integer.toString(movieYear);
                }
                if (!movieDirector.isEmpty()) {
                    query += "AND UPPER(m.director) LIKE ? ";
                    //params.add("%" + movieDirector.toUpperCase() + "%");
                    params[2] = "%" + movieDirector.toUpperCase() + "%";
                }
                if (!movieStar.isEmpty()) {
                    query += "AND m.id IN (SELECT sm.movieId\n" +
                            "            FROM stars AS s, stars_in_movies AS sm\n" +
                            "            WHERE s.id=sm.starId AND UPPER(s.name) LIKE ?)\n";
                    //params.add("%" + movieStar.toUpperCase() + "%");
                    params[3] = "%" + movieStar.toUpperCase() + "%";
                }
                query += orderQuery;
                statement = conn.prepareStatement(query);
                //System.out.println(query);
                int var = 1;
                for (int i = 0; i < 4; i++) {
                    //System.out.println(Arrays.toString(params));
                    if (params[i] != null) {
                        if (i == 1) {
                            statement.setInt(var, Integer.parseInt(params[i]));
                        } else {
                            statement.setString(var, params[i]);
                        }
                        var ++;
                    }
                }
                // Declare our statement



                // Perform the query
                System.out.println("SEARCH");
                rs = statement.executeQuery();
            } else if (type.equals("fulltext")) {
                JsonArray jsonArray = new JsonArray();


                String query = "SELECT m.id, m.title, m.year, m.director, r.rating " +
                        "FROM movies AS m, ratings AS r\n" +
                        "WHERE r.movieId=m.id AND MATCH(title) AGAINST (? IN BOOLEAN MODE)";

                // Perform the query
                String[] words = movieTitle.split(" ");
                String identifier = "";

                for (String word : words) {
                    identifier += "+" + word + "* ";
                }
                query += orderQuery;
                tj_start = System.nanoTime();
                statement = conn.prepareStatement(query);

                statement.setString(1, identifier);

                rs = statement.executeQuery();
            }


            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_id = rs.getString("m.id");
                System.out.println(movie_id);
                String movie_title = rs.getString("m.title");
                String movie_year = rs.getString("m.year");
                String movie_director = rs.getString("m.director");
                Float rating = rs.getFloat("r.rating");
//                String genre = rs.getString("g.name");

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
                if (!genre_str.isEmpty()) {
                    genre_str = genre_str.substring(0, genre_str.length() - 2);
                    genre_ids = genre_ids.substring(0, genre_ids.length() - 2);
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
                if (!stars_str.isEmpty()) {
                    stars_str = stars_str.substring(0, stars_str.length() - 2);
                    star_ids = star_ids.substring(0, star_ids.length() - 2);
                }

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_rating", rating);
                jsonObject.addProperty("movie_genres", genre_str);
                jsonObject.addProperty("movie_genre_ids", genre_ids);
                jsonObject.addProperty("movie_stars", stars_str);
                jsonObject.addProperty("movie_star_ids", star_ids);
                jsonObject.addProperty("sortBy", sortBy);
                jsonObject.addProperty("count", count);
                jsonObject.addProperty("page", page);


                jsonArray.add(jsonObject);
                genre_rs.close();
                genres.close();
                stars_rs.close();
                stars.close();
            }
            rs.close();
            statement.close();
            tj_end = System.nanoTime();

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            long ts_end = System.nanoTime();
            long ts = ts_end - ts_start;
            long tj = tj_end - tj_start;
            try {
                //local path
//                String path = "/Users/rithvijpochampally/Desktop/log.txt";
                //aws path
                String path = "/home/logs/log.txt";
                System.out.println(path);
                File file = new File(path);
                FileWriter myWriter = new FileWriter(file, true);
                myWriter.write(ts + " " + tj + "\n");
                myWriter.close();
                System.out.println(ts + " " + tj);
            } catch (IOException ex) {
                System.out.println("An error occurred while writing to file.");
                System.out.println(ex.toString());
            }
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
