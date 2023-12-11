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

public class StarInMovieParser {

    HashMap<String, ArrayList<Star>> starInMovies = new HashMap<>();
    Document dom;

    HashMap<String, Integer> stars;
    HashMap<String, String> dbStars;
    DatabaseIDs ids;
    public StarInMovieParser(HashMap<String, Integer> stars, HashMap<String, String> dbStars, DatabaseIDs ids) {
        this.stars = stars;
        this.dbStars = dbStars;
        this.ids = ids;
    }
    public void runParsing() {

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
            dom = documentBuilder.parse("XMLFiles/casts124.xml");
        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseDocument() {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();

        // get a nodelist of employee Elements, parse each into Employee object
        NodeList nodeList = documentElement.getElementsByTagName("m");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            //String director = getTextValue(element, "dirname");
            parseStarInMovie(element);
//            NodeList filmNodeList = element.getElementsByTagName("film");
//            for (int j = 0; j < filmNodeList.getLength(); j++) {
//                Element film = (Element) filmNodeList.item(j);
//                parseMovie(film);
//            }

            // get the Employee object


        }
        System.out.println("Parsing Complete");
    }

    /**
     * It takes an employee Element, reads the values in, creates
     * an Employee object for return
     */
    private void parseStarInMovie(Element element) {

        // for each <employee> element get text or int values of
        // name ,id, age and name
        String fid = getTextValue(element, "f");
        String starName = getTextValue(element, "a");
        Integer birthYear = null;
        if (stars.containsKey(starName)) {
            birthYear = stars.get(starName);
        }
        String k = (starName + birthYear).toUpperCase();
        String id;
        boolean insert;
        if (dbStars.containsKey(k)) {
            id = dbStars.get(k);
            insert = false;
        } else {
            id = ids.getNextStarId();
            dbStars.put(k, id);
            insert = true;
        }
        Star star = new Star(id, starName, birthYear, insert);




        if (starInMovies.containsKey(fid)) {
            starInMovies.get(fid).add(star);
        } else {
            ArrayList<Star> temp = new ArrayList<>();
            temp.add(star);
            starInMovies.put(fid, temp);
        }
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

        System.out.println("Total parsed " + starInMovies.size() + " stars");

        for (Map.Entry<String, ArrayList<Star>> entry : starInMovies.entrySet()) {
            String key = entry.getKey();
            ArrayList<Star> stars = entry.getValue();
            System.out.print("fid: " + key + " | stars: ");
            for (Star star : stars) {
                System.out.print(star.name + " (" + star.birthYear + "), ");
            }
            System.out.println();
            //System.out.println(key + ": " + value);
        }
    }


}
