import java.util.ArrayList;

public class Movie {
    String fid;

    String id;
    String title;
    String director;
    Integer year;
    ArrayList<Star> stars;
    ArrayList<Genre> genres;


    public Movie(String fid, String id, String title, String director,
                 Integer year, ArrayList<Genre> genres) {
        this.fid = fid;
        this.title = title;
        this.director = director;
        this.year = year;
        this.stars = new ArrayList<>();
        this.genres = genres;
        this.id = id;
    }

    public boolean isNull() {
        if (stars.isEmpty() || genres.isEmpty()) {
            return true;
        }
        return false;
    }

    public void printMovie() {
        System.out.print("fid: " + fid + " | ");
        System.out.print("title: " + title + " | ");
        System.out.print("director: " + director + " | ");
        System.out.println("year: " + year);
        System.out.print("stars: ");
        for (Star star : stars) {
            System.out.print(star.name + "(" + star.birthYear + ")" + ", ");
        }
        System.out.println();
        System.out.print("genres: ");
        for (Genre genre : genres) {
            System.out.print(genre.name + "(" + genre.insert + ")" + ", ");
        }
        System.out.println();
    }
}