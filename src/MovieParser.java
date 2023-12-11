import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieParser {

    HashMap<String, Movie> movies = new HashMap<>();

    HashMap<String, String> categories = new HashMap<>();
    Document dom;

    HashMap<String, String> dbMovies;
    DatabaseIDs ids;

    HashMap<String, Integer> dbGenre;

    public MovieParser (HashMap<String, String> dbMovies, DatabaseIDs ids, HashMap<String, Integer> dbGenre) {
        this.dbMovies = dbMovies;
        this.ids = ids;
        this.dbGenre = dbGenre;
    }

    public void runParsing() {

        updateGenres();

        parseXmlFile();

        parseDocument();

        //printData();


    }

    private void parseXmlFile() {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {

            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            dom = documentBuilder.parse("XMLFiles/mains243.xml");
        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseDocument() {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();

        // get a nodelist of employee Elements, parse each into Employee object
        NodeList nodeList = documentElement.getElementsByTagName("directorfilms");
        System.out.println("nodeList len: " + nodeList.getLength());
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            //String director = getTextValue(element, "dirname");
            NodeList filmNodeList = element.getElementsByTagName("film");
            for (int j = 0; j < filmNodeList.getLength(); j++) {
                Element film = (Element) filmNodeList.item(j);
                parseMovie(film);
            }

            // get the Employee object


        }
        System.out.println("Parsing Complete");
    }

    /**
     * It takes an employee Element, reads the values in, creates
     * an Employee object for return
     */
    private void parseMovie(Element element) {

        // for each <employee> element get text or int values of
        // name ,id, age and name
        String fid = getTextValue(element, "fid");
        String title = getTextValue(element, "t");
        Integer year = null;
        String director = getTextValue(element, "dirn");
        try {
            year = getIntValue(element, "year");
        } catch (NumberFormatException e) {
            System.out.println("Inconsistency: Movie Year is: " + getTextValue(element, "year"));
        }

        String k = (title + year + director).toUpperCase();
        String id;
        boolean insert;
        if (!dbMovies.containsKey(k)) {
            id = ids.getNextMovieId();
            dbMovies.put(k, id);


            ArrayList<Genre> genres = new ArrayList<>();

            NodeList catNodeList = element.getElementsByTagName("cat");
            for (int j = 0; j < catNodeList.getLength(); j++) {
                Element genre = (Element) catNodeList.item(j);
                String genreText = genre.getTextContent();
                //System.out.println(genreText);
                if (categories.containsKey(genreText)) {
                    genreText = categories.get(genreText);
                    //System.out.println(genreText);
                }
                int genreId;
                if (dbGenre.containsKey(genreText.toUpperCase())) {
                    genreId = dbGenre.get(genreText.toUpperCase());
                    insert = false;
                    //System.out.println("old genre: " + genreText);
                } else {
                    genreId = ids.getNextGenreId();
                    dbGenre.put(genreText.toUpperCase(), genreId);
                    //System.out.println("new genre: " + genreText + " id: " + genreId);
                    insert = true;
                }
                genres.add(new Genre(genreId, genreText, insert));
            }
            //System.out.println("stagename: " + name);


            if (title != null && !title.isEmpty() && director != null && !director.isEmpty()
                    && year != null) {
                movies.put(fid, new Movie(fid, id, title, director, year, genres));
            } else {
                System.out.println("Inconsistency: Illegal null Value(s) in movie");
            }
        }
        //movies.get(fid).printMovie();
    }

    /**
     * It takes an XML element and the tag name, look for the tag and get
     * the text content
     * i.e for <Employee><Name>John</Name></Employee> xml snippet if
     * the Element points to employee node and tagName is name it will return John
     */
    private String getTextValue(Element element, String tagName) {
        String textVal = null;
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            // here we expect only one <Name> would present in the <Employee>
            textVal = nodeList.item(0).getTextContent();

        }
        return textVal;
    }

    /**
     * Calls getTextValue and returns a int value
     */
    private int getIntValue(Element ele, String tagName) {
        // in production application you would catch the exception
        return Integer.parseInt(getTextValue(ele, tagName));
    }

    /**
     * Iterate through the list and print the
     * content to console
     */
    private void printData() {

        System.out.println("Total parsed " + movies.size() + " stars");

        for (Map.Entry<String, Movie> entry : movies.entrySet()) {
            String key = entry.getKey();
            Movie value = entry.getValue();
            //System.out.println(key + ": " + value);
        }
    }

    private void updateGenres() {
        categories.put("Ctxx","Uncategorized");
        categories.put("Actn","Action");
        categories.put("Camp","Camp");
        categories.put("Comd","Comedy");
        categories.put("Disa","Disaster");
        categories.put("Epic","Epic");
        categories.put("Horr","Horror");
        categories.put("ScFi","Sci-Fi");
        categories.put("West","Western");
        categories.put("Advt","Adventure");
        categories.put("Cart","Cartoon");
        categories.put("Docu","Documentary");
        categories.put("Faml","Family");
        categories.put("Musc","Musical");
        categories.put("Porn","Pornography");
        categories.put("Surl","Surreal");
        categories.put("AvGa","Avant Garde");
        categories.put("CnR","Cops and Robbers");
        categories.put("Dram","Drama");
        categories.put("Hist","History");
        categories.put("Myst","Mystery");
        categories.put("Romt","Romantic");
        categories.put("Susp","Thriller");
    }

}
