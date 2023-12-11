import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class ThreadXML {
    private static int failCounter = 0;
//    private static DataSource dataSource;
    public void executorRunner(HashMap<String, Movie> movies) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "mytestuser", "My6$Password")) {
            conn.setAutoCommit(false);


            int i = 0;
            for (Map.Entry<String, Movie> entry : movies.entrySet()) {
                Movie movie = entry.getValue();
                if (!movie.isNull()) {
                    QueryWorker worker = new QueryWorker(i, movie, conn);
                    executor.execute(worker);
                    i++;
                }
//                    if (i == 500) {
//                        break;
//                    }
            }
            i = 0;
            for (Map.Entry<String, Movie> entry : movies.entrySet()) {
                Movie movie = entry.getValue();
                if (!movie.isNull()) {
                    //movie.printMovie();
                    QueryWorker2 worker2 = new QueryWorker2(i, movie, conn);
                    executor.execute(worker2);
                    i++;
                }
//                    if (i == 500) {
//                        break;
//                    }
            }


            executor.shutdown();
            while (!executor.isTerminated()) {
            }



            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
        }
    }



    static class QueryWorker implements Runnable {
        Random random;
        String connection;
        String movieQuery;
        String starQuery;
        String genreQuery;
        Movie currMovie;

        Connection conn;
        int num;

//        Connection conn;

        QueryWorker(int i, Movie m, Connection c) {
            random = new Random(i);
            this.num = i;
            //connection = "New connection " + i;
            //System.out.println(connection);
//            this.movieQuery = "CALL addMovieXML(?,?,?);";
//            this.starQuery = "CALL addStarXML(?,?,?);";
//            this.genreQuery = "CALL addGenreXML(?,?);";
            this.currMovie = m;
            this.conn = c;

        }

        @Override
        public void run() {

            //System.out.println(String.format("Executing query: %s", query));
//            try (CallableStatement addMovie = conn.prepareCall("{CALL addMovieXML(?,?,?)}");
//                 CallableStatement addStar = conn.prepareCall("{CALL addStarXML(?,?,?)}");
//                 CallableStatement addGenre = conn.prepareCall("{CALL addGenreXML(?,?)}")) {
            try (PreparedStatement addMovie = conn.prepareStatement("INSERT INTO movies (id, title, year, director) VALUES (?, ?, ?, ?);");
                 PreparedStatement addRating = conn.prepareStatement("INSERT INTO ratings (movieId, rating, numVotes) VALUES (?, 0, 0);");
                 PreparedStatement addStar = conn.prepareStatement("INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?);");
                 PreparedStatement addGenre = conn.prepareStatement("INSERT INTO genres (id, name) VALUES (?, ?);")) {

                    // Movie Title

                addMovie.setString(1, currMovie.id);
                addRating.setString(1, currMovie.id);

                if (currMovie.title != null) {
                    addMovie.setString(2, currMovie.title);
                } else {
                    addMovie.setNull(2, Types.VARCHAR);
                }
                // Movie Year
                if (currMovie.year != null) {
                    addMovie.setInt(3, currMovie.year);
                } else {
                    addMovie.setNull(3, java.sql.Types.INTEGER);
                }
                // Movie Director
                if (currMovie.director != null) {
                    addMovie.setString(4, currMovie.director);
                } else {
                    addMovie.setNull(4, Types.VARCHAR);
                }
                addMovie.executeUpdate();
                addRating.executeUpdate();
                //System.out.println(this.num + " Added: " + currMovie.title);

                for (Star star: currMovie.stars) {
                    // Movie ID
                    if (star.insert) {


                        addStar.setString(1, star.id);
                        // Star Name
                        if (star.name != null) {
                            addStar.setString(2, star.name);
                        } else {
                            addStar.setNull(2, Types.VARCHAR);
                        }
                        // Star DOB
                        if (star.birthYear != null) {
                            addStar.setInt(3, star.birthYear);
                        } else {
                            addStar.setNull(3, java.sql.Types.INTEGER);
                        }
                        addStar.addBatch();
                    }

                }
                for (Genre genre: currMovie.genres) {
//                        System.out.println("GENRE: " + genre.name + " ID: " +
//                                genre.id + " | " + genre.insert);
                    if (genre.insert) {

                        // Movie ID
                        addGenre.setInt(1, genre.id);
                        // Genre Name
                        if (genre.name != null) {
                            addGenre.setString(2, genre.name);
                        } else {
                            addGenre.setNull(2, Types.VARCHAR);
                        }
                        addGenre.addBatch();
                    }

                }

                addGenre.executeBatch();
                addStar.executeBatch();

                } catch (SQLException e) {
                    e.printStackTrace();
                    //System.out.println(currMovie.toString());
                } finally {
                    //System.out.println(this.num + " Added: " + currMovie.title);
                }
//            try {
//                Thread.sleep(random.nextInt(500));
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
        }
    }
    static class QueryWorker2 implements Runnable {
        Random random;
        String connection;
        String movieQuery;
        String starQuery;
        String genreQuery;
        Movie currMovie;

        Connection conn;
        int num;

//        Connection conn;

        QueryWorker2(int i, Movie m, Connection c) {
            random = new Random(i);
            this.num = i;
            //connection = "New connection " + i;
            //System.out.println(connection);
//            this.movieQuery = "CALL addMovieXML(?,?,?);";
//            this.starQuery = "CALL addStarXML(?,?,?);";
//            this.genreQuery = "CALL addGenreXML(?,?);";
            this.currMovie = m;
            this.conn = c;

        }

        @Override
        public void run() {

            //System.out.println(String.format("Executing query: %s", query));
//            try (CallableStatement addMovie = conn.prepareCall("{CALL addMovieXML(?,?,?)}");
//                 CallableStatement addStar = conn.prepareCall("{CALL addStarXML(?,?,?)}");
//                 CallableStatement addGenre = conn.prepareCall("{CALL addGenreXML(?,?)}")) {
            try (PreparedStatement addStarInMovie = conn.prepareStatement("INSERT INTO stars_in_movies (starId, movieId) VALUES (?, ?);");
                 PreparedStatement addGenreInMovie = conn.prepareStatement("INSERT INTO genres_in_movies (genreId, movieId) VALUES (?, ?);")) {

                // Movie Title

                for (Genre genre: currMovie.genres) {
//                        System.out.println("GENRE: " + genre.name + " ID: " +
//                                genre.id + " | " + genre.insert);

                    addGenreInMovie.setInt(1, genre.id);
                    addGenreInMovie.setString(2, currMovie.id);
                    addGenreInMovie.addBatch();
                }
                addGenreInMovie.executeBatch();

                for (Star star: currMovie.stars) {

                    addStarInMovie.setString(1, star.id);
                    //System.out.println("star: " + star.id);
                    addStarInMovie.setString(2, currMovie.id);
                    //System.out.println("movie: " + currMovie.id);

                    addStarInMovie.addBatch();
                }


                addStarInMovie.executeBatch();

            } catch (SQLException e) {
//                e.printStackTrace();
//                currMovie.printMovie();
//                failCounter++;
//                System.out.println(failCounter);
            } finally {
                    //System.out.println(this.num + " Added InMovies/Stars tables: " + currMovie.title);
            }
//            try {
//                Thread.sleep(random.nextInt(100));
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }

        }
    }
}
