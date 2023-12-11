public class DatabaseIDs {
    String movieId;
    String starId;
    int genreId;

    public DatabaseIDs (String movieId, String starId, int genreId) {
        this.movieId = movieId;
        this.starId = starId;
        this.genreId = genreId;
    }
    public String getNextMovieId () {
        String numericPart = movieId.substring(2);
        long numericValue = Long.parseLong(numericPart) + 1;
        movieId = "tt" + String.format("%07d", numericValue);
        //movieId = "tt" + (Integer.parseInt(movieId.substring(2)) + 1);
        return movieId;
    }
    public String getNextStarId () {
        String numericPart = starId.substring(2);
        long numericValue = Long.parseLong(numericPart) + 1;
        starId = "nm" + String.format("%07d", numericValue);
        //starId = "nm" + (Integer.parseInt(starId.substring(2)) + 1);
        return starId;
    }
    public int getNextGenreId () {
        genreId++;
        return genreId;
    }
}

