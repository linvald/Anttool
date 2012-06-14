package dk.linvald.anttool;

import javax.xml.parsers.*;

import org.apache.xml.serialize.DOMSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.*;
import java.io.*;

/**
 * This class is a convinience class for handling XML
 * It offers methods to spacify separate elements to retrieve etc.
 *
 * @author linvald@it-c.dk
 */
public class XmlHandler {

                private Node _property = null;
                private DocumentBuilderFactory _factory;
                private DocumentBuilder _builder;
                private Document _document;
                private Node _rootNode;
                private String _defaultPath = "default.xml";
                private String _pathToDoc;



                /**
                 * @return
                 */
                public String get_pathToDoc() {
                        return _pathToDoc;
                }

                /**
                 * @param toDoc
                 */
                public void set_pathToDoc(String toDoc) {
                        _pathToDoc = toDoc;
                }

        /**
         * Constructor for XmlHandler.-You cant handle XML without a document
         */
        public XmlHandler(String xmlDocPath) {
                init();
                this.set_pathToDoc(xmlDocPath);
                try {
                        _document = _builder.parse(xmlDocPath);
                        _rootNode = _document.getDocumentElement();
                }
                catch (Exception e) {
                        System.err.println("Couldn't find xml doc in:" + xmlDocPath + "\n"+ "Check path and filename..."+ "\n" + e);
                }

        }

        /**
         * Initialize the XML Factory and builder
         * used for internal purposes and probably should be private
         */
        public void init(){
                try {
                        _factory = DocumentBuilderFactory.newInstance();
                        _builder = _factory.newDocumentBuilder();
                }
                catch (ParserConfigurationException e) {
                        System.err.println("Error in init:" + e);
                }

        }

        public NodeList getNodes(String tagName){
                return _document.getElementsByTagName(tagName);
        }


// :::::::::::::::::::: get / set ::::::::::::::::::::::::


          /**
                * Method getAttributeInTag.
                * @param tagName - the tag from which you which to retrieve the value
                * @return String - the value in the specific tag
                * Used with an xml file that you know only have one unique attribute - like in a config file
                * Example: <mysql user="john" password="doe"/>
                * - to get uservalue: getAttributeInTag("mysql","user"); --> returns "john"
                 */
                public String getAttributeInTag(String tagName, String attributeName) {
                                String attValue  = null;

                                try {
                                        NodeList element = _document.getElementsByTagName(tagName);
                                        for(int i = 0; i<element.getLength(); i++ ){         
                                        	Node node = element.item(i);
                                        	NamedNodeMap attributes = node.getAttributes();
                                        	for (int j=0; j<attributes.getLength(); j++) {
                                                Attr attr = (Attr)attributes.item(j);
                                                // Get attribute name and value
                                                String attrName = attr.getNodeName().toString();
                                                //System.out.println("Att:" + attrName);
                                                if(attrName.equals(attributeName)){
                                                        attValue = attr.getNodeValue();
                                                        return attValue;
                                              	}
                                        	}
                                        }
                                }
                                catch (Exception e) {
                                        System.err.println("The XML document is not set properly:" + e);
                                }
                        return attValue;
                }

                public void writeAttributeValueInTag(String tagName, String attributeName, String newValue){
                        try {
                                                                NodeList element = _document.getElementsByTagName(tagName);
                                                                Node node = element.item(0);
                                                                NamedNodeMap attributes = node.getAttributes();

                                                                // Get number of attributes in the element
                                                                int numAttrs = attributes.getLength();

                                                                // Process each attribute
                                                                for (int i=0; i<numAttrs; i++) {
                                                                        Attr attr = (Attr)attributes.item(i);

                                                                        // Get attribute name and value
                                                                        String attrName = attr.getNodeName().toString();
                                                                        if(attrName.equals(attributeName)){
                                                                                attr.setNodeValue(newValue);
                                                                                break;
                                                                        }
                                                                }

                                                                FileWriter writer = new FileWriter(new File(this.get_pathToDoc()));
                                                                OutputFormat format = new OutputFormat(this.get_document());
                                                                XMLSerializer ser = new XMLSerializer(writer,format);
                                                                format.setIndent(3);
                                                                format.setLineWidth(60);
                                                                ser.serialize(this._document);
                                                        }
                                                        catch (Exception e) {
                                                                System.err.println("The XML document is not set properly:" + e);
                                                        }
                }

                /**
                 * Returns the _builder.
                 * @return DocumentBuilder
                 */
                public DocumentBuilder get_builder() {
                        return _builder;
                }

                /**
                 * Returns the _document.
                 * @return Document
                 */
                public Document get_document() {
                        return _document;
                }

                /**
                 * Returns the _factory.
                 * @return DocumentBuilderFactory
                 */
                public DocumentBuilderFactory get_factory() {
                        return _factory;
                }

                /**
                 * Returns the _property.
                 * @return Node
                 */
                public Node get_property() {
                        return _property;
                }

                /**
                 * Returns the _rootNode.
                 * @return Node
                 */
                public Node get_rootNode() {
                        return _rootNode;
                }

                /**
                 * Sets the _builder.
                 * @param _builder The _builder to set
                 */
                public void set_builder(DocumentBuilder _builder) {
                        this._builder = _builder;
                }

                /**
                 * Sets the _document.
                 * @param _document The _document to set
                 */
                public void set_document(Document _document) {
                        this._document = _document;
                }

                /**
                 * Sets the _factory.
                 * @param _factory The _factory to set
                 */
                public void set_factory(DocumentBuilderFactory _factory) {
                        this._factory = _factory;
                }

                /**
                 * Sets the _property.
                 * @param _property The _property to set
                 */
                public void set_property(Node _property) {
                        this._property = _property;
                }

                /**
                 * Sets the _rootNode.
                 * @param _rootNode The _rootNode to set
                 */
                public void set_rootNode(Node _rootNode) {
                        this._rootNode = _rootNode;
                }

}
