public class Star {
    String name;
    Integer birthYear;
    String id;
    boolean insert = true;

    public Star(String id, String name, Integer birthYear, boolean insert) {
        this.name = name;
        this.birthYear = birthYear;
        this.id = id;
        this.insert = insert;
    }

    public Star(String name) {
        this.name = name;
        this.birthYear = null;
    }

}
