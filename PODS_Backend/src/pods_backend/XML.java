package pods_backend;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pods_backend.XML.XMLException;

/**
 *
 * @author Josua Frank
 */
public class XML {

    private File xmlFile;//File only for this file
    private final Element e;//Element only for this XML-Type(Element)
    private Document doc;//Document only for this XML-File(test.xml)
    private static LinkedHashMap<Element, XML> LIST = new LinkedHashMap<>();
    private boolean autosave = false;//Saves the file after EVERY change

    //DIENEN NUR DAMIT MAN SIE NICHT IN JEDER METHODE NEU DEKLARIEREN MUSS, KEINE INHALTSABLAGE
    private NodeList ch;//Children
    private Node chi;
    private NamedNodeMap a;//Attributes
    private Node p;//Parent

    /**
     * Creates a new XML without a file representation
     *
     * @param name name of the XML Node
     */
    public XML(String name) {
        doc = getDoc();
        e = doc.createElement(name);
        LIST.put(e, this);
    }

    /**
     * Reads a existing XML file
     *
     * @param file
     * @throws XMLException
     */
    public XML(File file) throws XMLException {
        e = readXML(file);
        LIST.put(e, this);
    }

    /**
     * Creates a new XML file with the specific rootname
     *
     * @param file
     * @param rootname
     */
    public XML(File file, String rootname) {
        if (rootname == null) {
            rootname = "";
        }
        e = createXML(file, rootname);
        LIST.put(e, this);
        if (e != null && autosave) {
            reloadFile();
        }
    }

    /**
     * for existing XMLs to read from
     *
     * @param file
     * @param rootname
     */
    private Element createXML(File file, String rootname) {
        xmlFile = file;
        Element element = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            xmlFile.createNewFile();
            if (doc == null) {
                doc = builder.newDocument();
            }
            if (rootname.isEmpty()) {
                String root = file.getName();
                if (root.contains(".")) {
                    element = doc.createElement(xmlFile.getName().substring(0, xmlFile.getName().lastIndexOf(".")));
                } else {
                    element = doc.createElement(root);
                }
            } else {
                element = doc.createElement(rootname);
            }
        } catch (ParserConfigurationException | IOException ex) {
            System.err.println("Fehler: " + ex);
        }
        return element;
    }

    private Element readXML(File file) throws XMLException {
        xmlFile = file;
        Element element;
        Document document;
        document = isValid(xmlFile);
        document.getDocumentElement().normalize();
        element = document.getDocumentElement();
        doc = document;
        return element;
    }

    private XML(Element element) {
        e = element;
        XML.LIST.put(e, this);
    }

    /**
     *
     * @param xmlFile
     * @return null if invalid, a Document if valid
     * @throws Yes, a lot of shit can happen here
     */
    private Document isValid(File xmlFile) throws XMLException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            return document;
        } catch (ParserConfigurationException ex) {
            throw new XMLException("[INTERNAL] The DocumentBuilder cannot be created with with the requested configuration: " + ex);
        } catch (SAXException ex) {
            throw new XMLException("Parsing error occured: " + ex);
        } catch (IOException ex) {
            throw new XMLException("IO error occured: " + ex);
        }
    }

    /**
     * Checks wether this node has attributes or not
     *
     * @return true if the node has attributes, false if the node has no
     * attributes
     */
    public boolean hasAttributes() {
        return e.hasAttributes();
    }

    /**
     * Checks wether this node has text-content or not
     *
     * @return true if the node has text content, false if the content has no
     * text content
     */
    public boolean hasContent() {
        if (e.hasChildNodes()) {
            
            for (int i = 0; i < (ch = e.getChildNodes()).getLength(); i++) {
                if ((chi = ch.item(i)).getNodeType() == Node.TEXT_NODE) {
                    return !chi.getTextContent().matches("[\\s]*");//not only whitespaces
                }
            }
        }
        return false;
    }
    
    public void test(){
        System.out.println(e.getTextContent());
    }

    /**
     * Checks if this node has child nodes
     *
     * @return true, if this node has at least one child node, false if it has
     * no child nodes
     */
    public boolean hasChildren() {
        if (!e.hasChildNodes()) {
            return false;
        }
        for (int i = 0; i < (ch = e.getChildNodes()).getLength(); i++) {
            if (ch.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks wether this node has parent or not
     *
     * @return true, if this node has a parent, false if this node is the first
     * node in the xml tree
     */
    public boolean hasParent() {
        return !isRoot();

    }

    /**
     * Returns the name of the node
     *
     * @return The name of the node
     */
    public String getName() {
        return e.getTagName();
    }

    /**
     * Returns the attributes as a linked hashmap
     *
     * @return The attributes a a linked hashmap (key = attributename, value =
     * attributevalue)
     */
    public LinkedHashMap<String, String> getAttributes() {
        LinkedHashMap<String, String> r = new LinkedHashMap<>();
        for (int i = 0; i < (a = e.getAttributes()).getLength(); i++) {
            r.put(a.item(i).getNodeName(), a.item(i).getNodeValue());
        }
        return r;
    }

    /**
     * Returns the requested attribute of this node
     *
     * @param name The name of the requested attribute
     * @return The requested attribute
     */
    public String getAttribute(String name) {
        return e.getAttribute(name);
    }

    /**
     * Returns the text content of this node
     *
     * @return The text content of this node
     */
    public String getContent() {
        if (e.hasChildNodes()) {
            for (int i = 0; i < (ch = e.getChildNodes()).getLength(); i++) {
                if ((chi = ch.item(i)).getNodeType() == Node.TEXT_NODE) {
                    if (!chi.getTextContent().matches("[\\s]*")) {//Not only whitepaces
                        return chi.getTextContent();
                    }
                    return "";
                }
            }
        }
        return "";
    }

    /**
     * Returns the first child of this node
     *
     * @return The first child of this node as XML-object
     */
    public XML getFirstChild() {
        for (int i = 0; i < (ch = e.getChildNodes()).getLength(); i++) {
            if ((chi = ch.item(i)).getNodeType() == Node.ELEMENT_NODE) {
                return getXMLByElement((Element) chi);
            }
        }
        return new XML("");
    }

    /**
     * Returns the last child of this node
     *
     * @return The last child of this node as XML-object
     */
    public XML getLastChild() {
        chi = null;
        for (int i = 0; i < (ch = e.getChildNodes()).getLength(); i++) {
            if (ch.item(i).getNodeType() == Node.ELEMENT_NODE) {
                chi = ch.item(i);
            }
        }
        return getXMLByElement((Element) chi);
    }

    /**
     * Returns all children of this node
     *
     * @return All children of this node as ArrayList
     */
    public ArrayList<XML> getChildren() {
        ArrayList<XML> r = new ArrayList<>();
        for (int i = 0; i < (ch = e.getChildNodes()).getLength(); i++) {
            if ((chi = ch.item(i)).getNodeName() == null || chi.getNodeName().matches("[\\s]*")) {
                e.removeChild(chi);
            } else if (chi.getNodeType() == Node.ELEMENT_NODE) {
                r.add(getXMLByElement((Element) ch.item(i)));
            }
        }
        return r;
    }

    /**
     * Returns the requested child, children or null if the child doesnt exist
     *
     * @param name The name of the requested child or the requested children
     * @return A ArrayList with zero, one or more children matching this name
     */
    public ArrayList<XML> getChild(String name) {
        ArrayList<XML> r = new ArrayList<>();
        for (int i = 0; i < (ch = e.getChildNodes()).getLength(); i++) {
            chi = ch.item(i);
            if (chi.getNodeType() == Node.ELEMENT_NODE && chi.getNodeName().equals(name)) {
                r.add(getXMLByElement((Element) chi));
            }
        }
        return r;
    }

    /**
     * Returns the first child with the requested name
     *
     * @param name The requested name of the wanted child
     * @return The first child whose name matches your requested name
     */
    public XML getFirstChild(String name) {
        for (int i = 0; i < (ch = e.getChildNodes()).getLength(); i++) {
            if ((chi = ch.item(i)).getNodeType() == Node.ELEMENT_NODE && chi.getNodeName().equals(name)) {
                return getXMLByElement((Element) chi);
            }
        }
        return new XML("");
    }

    /**
     * Returns the parent of this node
     *
     * @return The parent of this node or an empty XML-object if this node has
     * no parent
     */
    public XML getParent() {
        if (!isRoot()) {
            return getXMLByElement((Element) e.getParentNode());
        }
        return new XML("");
    }

    /**
     * Adds an attribute to this node
     *
     * @param name The name of this attribute
     * @param value The value of this attribute
     * @return This node (for convenience reasons)
     */
    public XML addAttribute(String name, String value) {
        e.setAttribute(name, value);
        if (autosave) {
            reloadFile();
        }
        return this;
    }

    /**
     * Sets the content of this node
     *
     * @param content The content to be set on this node
     * @return This node (for convenience reasons)
     */
    public XML setContent(String content) {
        doc = getDoc();
        e.appendChild(doc.createTextNode(content));
        if (autosave) {
            reloadFile();
        }
        return this;
    }

    ArrayList<XML> alreadyAppended = new ArrayList<>();

    /**
     * Adds child nodes to this node
     *
     * @param children zero, one or more child nodes as XML-object to add
     * @return This node (for convenience reasons)
     */
    public XML addChildren(XML... children) {
        doc = getDoc();
        for (XML child : children) {
            if (!alreadyAppended.contains(child)) {
                if (child.e.getOwnerDocument().getBaseURI().equals(doc.getBaseURI())) {
                    e.appendChild(child.e);
                } else {
                    e.appendChild(doc.adoptNode((Node) child.e));
                }
                alreadyAppended.add(child);
            } else//bla
            {
                if (doc.equals(child.e.getOwnerDocument())) {
                    e.appendChild(child.e.cloneNode(true));
                } else {
                    e.appendChild(doc.adoptNode((Node) child.e.cloneNode(true)));
                }
            }
        }
        if (autosave) {
            reloadFile();
        }
        return this;
    }

    /**
     * Adds new children to this node
     *
     * @param children The zero, one or more names of the new children
     * @return This node (for convenience reasons)
     */
    public XML addChildren(String... children) {
        doc = getDoc();
        XML child;
        for (String childS : children) {
            child = new XML(childS);
            if (!alreadyAppended.contains(child)) {
                if (child.e.getOwnerDocument().equals(doc)) {
                    e.appendChild(child.e);
                } else {
                    e.appendChild(doc.adoptNode((Node) child.e));
                }
                alreadyAppended.add(child);
            } else//
            {
                if (doc.equals(child.e.getOwnerDocument())) {
                    e.appendChild(child.e.cloneNode(true));
                } else {
                    e.appendChild(doc.adoptNode((Node) child.e.cloneNode(true)));
                }
            }
        }
        if (autosave) {
            reloadFile();
        }
        return this;
    }

    /**
     * Deletes a attribute of this node
     *
     * @param name The name of the attribute to be deleted
     * @return This node (for convenience reasons)
     */
    public XML deleteAttribute(String name) {
        e.removeAttribute(name);
        if (autosave) {
            reloadFile();
        }
        return this;
    }

    /**
     * Deletes ALL attributes matching the specified value
     *
     * @param value The specified name after which the all attributes equaling
     * this value will be deleted
     * @return This node (for convenience reasons)
     */
    public XML deleteAttributesByValue(String value) {
        for (int i = 0; i < (a = e.getAttributes()).getLength(); i++) {
            if (a.item(i).getNodeValue().equals(value)) {
                e.removeAttribute(a.item(i).getNodeName());
            }
        }
        if (autosave) {
            reloadFile();
        }
        return this;
    }

    /**
     * Deletes the FIRST attributes matching the specified value
     *
     * @param value The specified name after which the first attribute equaling
     * this value will be deleted
     * @return This node (for convenience reasons)
     */
    public XML deleteFirstAttributeByValue(String value) {
        for (int i = 0; i < (a = e.getAttributes()).getLength(); i++) {
            if (a.item(i).getNodeValue().equals(value)) {
                e.removeAttribute(a.item(i).getNodeName());
                if (autosave) {
                    reloadFile();
                }
                return this;
            }
        }
        return this;
    }

    /**
     * Deletes ALL attributes of this node
     *
     * @return This node (for convenience reasons)
     */
    public XML clearAttributes() {
        for (int i = 0; i < (a = e.getAttributes()).getLength(); i++) {
            e.removeAttribute(a.item(i).getNodeName());
        }
        if (autosave) {
            reloadFile();
        }
        return this;
    }

    /**
     * Deletes the text-content of this node
     *
     * @return This node (for convenience reasons)
     */
    public XML clearContent() {
        if (e.hasChildNodes()) {
            for (int i = 0; i < (ch = e.getChildNodes()).getLength(); i++) {
                if ((chi = ch.item(i)).getNodeType() == Node.TEXT_NODE) {
                    e.removeChild(chi);
                }
            }
            if (autosave) {
                reloadFile();
            }
        }
        return this;
    }

    /**
     * Deletes a child specified by its name
     *
     * @param name The name of the to be deleted child
     * @return This node (for convenience reasons)
     */
    public XML deleteChildren(String name) {
        for (int i = 0; i < (ch = e.getChildNodes()).getLength(); i++) {
            if (((chi = ch.item(i)).getNodeName().equals(name))) {
                e.removeChild(chi);
            }
        }
        if (autosave) {
            reloadFile();
        }
        return this;
    }

    /**
     * Deletes a child specified by its XML-object
     *
     * @param child The XML-object of the child to be deleted
     * @return This node (for convenience reasons)
     */
    public XML deleteChild(XML child) {
        e.removeChild(child.e);
        if (autosave) {
            reloadFile();
        }
        return this;
    }

    /**
     * Deletes ALL children of this node
     *
     * @return This node (for convenience reasons)
     */
    public XML clearChildren() {
        for (int i = 0; i < (ch = e.getChildNodes()).getLength(); i++) {
            e.removeChild(ch.item(i));
        }
        if (autosave) {
            reloadFile();
        }
        return this;
    }

    /**
     * Deletes this node and with it all its children, attributes and contents
     */
    public void delete() {
        if (hasParent()) {
            (chi = e.getParentNode()).removeChild(e);
            if (autosave) {
                getXMLByElement((Element) chi).reloadFile();
            }
        }
    }

    /**
     * Sets the file in which this XML-object is going to be stored in
     *
     * @param file The file in which this XML-object is going to be stored in
     */
    public void setFileToSaveIn(File file) {
        if (hasParent()) {
            getParent().setFileToSaveIn(file);
            return;
        }
        xmlFile = file;
    }

    /**
     * Saves this XML-object to the specified file
     */
    public void save() {
        if (hasParent()) {
            getParent().save();
            return;
        }
        if (xmlFile == null) {
            System.err.println("Fehler, keine Datei angegeben, bitte benutze saveTo(file) oder definiere die Datei mit setFileToSaveIn(file)");
            return;
        }
        saveTo(xmlFile);
    }

    /**
     * Saves this XML-object in the specified filen \nDoes NOT change the
     * standard file to save in, use @link setFileToSaveIn() for this
     *
     * @param toSaveIn The file in which this XML-object is going to be stored
     * in
     */
    public void saveTo(File toSaveIn) {
        if (hasParent()) {
            getParent().saveTo(toSaveIn);
            return;
        }
        try {
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(e);
            if (!toSaveIn.exists()) {
                toSaveIn.getParentFile().mkdir();
                toSaveIn.createNewFile();
            }
            StreamResult result = new StreamResult(toSaveIn);

            // Output to console for testing
            transformer.transform(source, result);
            System.out.println("File saved!");

        } catch (IOException | TransformerException ex) {
            System.err.println("Fehler: " + ex);
        }
    }

    private Document getDoc() {
        if (hasParent()) {
            return getParent().getDoc();
        } else if (doc == null) {
            try {
                return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            } catch (ParserConfigurationException ex) {
                System.err.println("Error, could not create Document instance");
                return null;
            }
        } else {
            return doc;
        }
    }

    private void reloadFile() {
        if (hasParent()) {
            getParent().reloadFile();
            return;
        }
        if (xmlFile != null) {
            save();
        }
    }

    private XML getXMLByElement(Element e) {
        if (XML.LIST.containsKey(e)) {
            return XML.LIST.get(e);
        }
        return new XML(e);
    }

    /**
     * Returns wether this node is the last child or not
     *
     * @return true if this node is the last child of its parents, false if it
     * is not the last child of its parents
     */
    public boolean isLastChild() {
        if (!isRoot()) {
            while ((chi = e.getParentNode().getLastChild()).getNodeType() == Node.TEXT_NODE && chi.getTextContent().matches("[\\s]*")) {
                e.getParentNode().removeChild(chi);
            }
            return e.getParentNode().getLastChild().isSameNode(e);
        } else {
            return true;
        }

    }

    private boolean isRoot() {
        if (e == null) {
            return true;
        }
        p = e.getParentNode();
        short t = 0;
        if (p != null) {
            t = p.getNodeType();
        }
        return p == null || (t != Node.ELEMENT_NODE && t != Node.TEXT_NODE);
    }

    public String toXMLString() {
        return toXMLString(true);
    }

    /**
     * Returns the content of the current node
     *
     * @param inline Wether the output should be in one line or formatted
     * @return The content of the current node as String in XML language
     */
    public String toXMLString(boolean inline) {
        try {
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();            
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");//removes this stuff: <?xml version='1.0' ?>

            if (inline) {
                transformer.setOutputProperty(OutputKeys.INDENT, "no");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "0");
            } else {
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            }

            StreamResult streamResult = new StreamResult(new StringWriter());
            transformer.transform(new DOMSource(e), streamResult);
            String result = streamResult.getWriter().toString();
            return result;

        } catch (TransformerException ex) {
            System.err.println("Fehler: " + ex);
        }
        return "";
    }

    /**
     * Returns a fancy string representation of this XML-object
     *
     * @return A String representation of this XML-object
     */
    @Override
    public String toString() {
        ArrayList<Boolean> uncles = new ArrayList<>();
        return toString(0, uncles);
    }

    private static final int WIDTHOFTHECHART = 3;

    private String toString(int depth, ArrayList<Boolean> unclesAndGreatunclesASO) {//Geschwister meiner Eltern, deren Eltern, usw
        //Funktioniert
        String string = "";
        //string += "[Depth: " + depth + "] [Size Of TRUE/FALSE: " + unclesAndGreatunclesASO.size() + "]";
        if (depth > 0) {
            if (isLastChild()) {
                string += "└─Name: \"" + getName() + "\" ";
            } else {
                string += "├─Name: \"" + getName() + "\" ";
            }
        } else {
            string += "Name: \"" + getName() + "\" ";
        }
        //Funktioniert
        if (hasAttributes()) {
            string += "; Attributes: ";
            boolean first = true;
            for (int i = 0; i < (a = e.getAttributes()).getLength(); i++) {
                if (!first) {
                    string += ", ";
                }
                first = false;
                string += "\"" + a.item(i).getNodeName() + "\" = \"" + a.item(i).getNodeValue() + "\" ";
            }
        }
        //Funktioniert
        if (hasContent()) {
            string += "; Content: \"" + getContent().replace("\n", "[NewLine]").replace("\t", "[TAB]") + "\" ";
        }
        if (hasParent()) {
            string += "; Parent: \"" + getParent().getName() + "\" ";
        }
        String temp = "";
        for (Boolean hasUncle : unclesAndGreatunclesASO) {
            if (hasUncle) {
                temp = "│" + temp;
                for (int i = WIDTHOFTHECHART; i > 1; i--) {
                    temp = " " + temp;
                }
            } else {
                for (int i = WIDTHOFTHECHART; i > 0; i--) {
                    temp = " " + temp;
                }
            }
        }
        if (depth > 0) {
            temp = temp.substring(0, (depth - 1) * WIDTHOFTHECHART);
        }
        string = new StringBuilder(temp).reverse().toString() + string;
        if (depth != 0) {
            string = "\n" + string;
        }
        if (hasChildren()) {
            if (!isRoot()) {
                if (!isLastChild()) {
                    unclesAndGreatunclesASO.add(Boolean.TRUE);
                } else {
                    unclesAndGreatunclesASO.add(Boolean.FALSE);
                }
            }
            ArrayList<XML> childs;
            string += "; Children: " + (childs = getChildren()).size() + " ";
            for (int i = 0; i < childs.size(); i++) {
                string += childs.get(i).toString(depth + 1, unclesAndGreatunclesASO);
            }
        }
        if (isLastChild()) {
            if (unclesAndGreatunclesASO.size() > 0) {
                unclesAndGreatunclesASO.remove(unclesAndGreatunclesASO.size() - 1);
            }
        }
        return string;
    }

    public class XMLException extends Exception {

        public XMLException() {
            super();
        }

        public XMLException(String message) {
            super(message);
        }

    }

}
