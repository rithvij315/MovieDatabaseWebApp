public class MovieInCart {

    public final String movieName;
    public int count;

    public double cost;

    public String movieId;

    public int saleId;
    public MovieInCart(String movieName, int count, double cost, String movieId) {
        this.movieName = movieName;
        this.count = count;
        this.cost = cost;
        this.movieId = movieId;
        this.saleId=-1;
    }

}