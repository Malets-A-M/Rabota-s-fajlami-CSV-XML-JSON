import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class ReadShopXML {
    private boolean loadEnabled;
    private String loadFileName;
    private String loadFormat;

    private boolean saveEnabled;
    private String saveFileName;
    private String saveFormat;

    private boolean logEnabled;
    private String logFileName;



    public void loadConfigShopXML (File file) throws TransformerException, ParserConfigurationException, IOException, SAXException {
        NodeList nodeList = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file).getChildNodes().item(0).getChildNodes();
        DOMSource domSource = new DOMSource(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file));
        StreamResult streamResult = new StreamResult(file);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;
                if (node.getNodeName().equals("load")) {
                    loadEnabled = Boolean.parseBoolean(element.getElementsByTagName("enabled").item(0).getTextContent());
                    loadFileName = element.getElementsByTagName("fileName").item(0).getTextContent();
                    loadFormat = element.getElementsByTagName("format").item(0).getTextContent();
                }
                if (node.getNodeName().equals("save")) {
                    saveEnabled = Boolean.parseBoolean(element.getElementsByTagName("enabled").item(0).getTextContent());
                    saveFileName = element.getElementsByTagName("fileName").item(0).getTextContent();
                    saveFormat = element.getElementsByTagName("format").item(0).getTextContent();
                }
                if (node.getNodeName().equals("log")) {
                    logEnabled = Boolean.parseBoolean(element.getElementsByTagName("enabled").item(0).getTextContent());
                    logFileName = element.getElementsByTagName("fileName").item(0).getTextContent();
                }
            }
        }
        transformer.transform(domSource, streamResult);
    }

    public boolean isLoadEnabled() {
        return loadEnabled;
    }

    public String getLoadFileName() {
        return loadFileName;
    }

    public String getLoadFormat() {
        return loadFormat;
    }

    public boolean isSaveEnabled() {
        return saveEnabled;
    }

    public String getSaveFileName() {
        return saveFileName;
    }

    public String getSaveFormat() {
        return saveFormat;
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public String getLogFileName() {
        return logFileName;
    }
}
