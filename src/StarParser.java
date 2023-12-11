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
import java.util.List;
import java.util.Map;

public class StarParser {

    HashMap<String, Integer> stars = new HashMap<>();
    Document dom;

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
            dom = documentBuilder.parse("XMLFiles/actors63.xml");
        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseDocument() {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();

        // get a nodelist of employee Elements, parse each into Employee object
        NodeList nodeList = documentElement.getElementsByTagName("actor");
        System.out.println("nodeList len: " + nodeList.getLength());
        for (int i = 0; i < nodeList.getLength(); i++) {
            // get the employee element
            Element element = (Element) nodeList.item(i);

            //System.out.println(element.getElementsByTagName("stagename").item(0).getTextContent());
            // get the Employee object
            parseStar(element);

        }
        System.out.println("Parsing Complete");
    }

    /**
     * It takes an employee Element, reads the values in, creates
     * an Employee object for return
     */
    private void parseStar(Element element) {

        // for each <employee> element get text or int values of
        // name ,id, age and name
        String name = getTextValue(element, "stagename");
        //System.out.println("stagename: " + name);
        Integer birthYear = null;
        String birthYearText = getTextValue(element, "dob");
        if (birthYearText != null && !birthYearText.isEmpty()) {
            //check for inconsistency
            try {
                birthYear = Integer.parseInt(birthYearText);
            } catch (NumberFormatException e) {
                System.out.println("Inconsistency: Star Birth Year is: " + birthYearText);
            }

        } //add else to print inconsistency

        stars.put(name, birthYear);
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

        System.out.println("Total parsed " + stars.size() + " stars");

        for (Map.Entry<String, Integer> entry : stars.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println(key + ": " + value);
        }
    }


}
