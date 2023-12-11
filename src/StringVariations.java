import java.util.HashMap;
import java.util.Map;

public class StringVariations {
    private Map<Integer, String> variations;
    private Map<Integer, String> counts;

    public StringVariations() {
        variations = new HashMap<>();
        variations.put(1, "ORDER BY m.title ASC, r.rating DESC");
        variations.put(2, "ORDER BY m.title DESC, r.rating ASC");
        variations.put(3, "ORDER BY m.title DESC, r.rating DESC");
        variations.put(4, "ORDER BY m.title ASC, r.rating ASC");
        variations.put(5, "ORDER BY r.rating ASC, m.title ASC");
        variations.put(6, "ORDER BY r.rating DESC, m.title ASC");
        variations.put(7, "ORDER BY r.rating ASC, m.title DESC");
        variations.put(8, "ORDER BY r.rating DESC, m.title DESC");

        counts = new HashMap<>();
        counts.put(10, "LIMIT 10");
        counts.put(25, "LIMIT 25");
        counts.put(50, "LIMIT 50");
        counts.put(100, "LIMIT 100");
    }

    public String getSorting(int number) {
        return variations.get(number);
    }

    public String getCount(int number) {
        return counts.get(number);
    }
}