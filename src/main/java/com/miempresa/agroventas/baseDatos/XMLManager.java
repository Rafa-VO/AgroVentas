package com.miempresa.agroventas.baseDatos;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Lee el XML de configuración de conexión y crea un ConnectionProperties.
 */
public class XMLManager {
    private static final String CONFIG_FILE = "connection.xml";
    private final Document document;

    public XMLManager() throws Exception {
        // Carga el XML desde src/main/resources
        File xml = new File(
                getClass().getClassLoader()
                        .getResource(CONFIG_FILE)
                        .toURI()
        );
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(xml);
    }

    public ConnectionProperties loadProperties() {
        Element root = document.getDocumentElement();
        String server   = root.getElementsByTagName("server").item(0).getTextContent();
        int    port     = Integer.parseInt(root.getElementsByTagName("port").item(0).getTextContent());
        String database = root.getElementsByTagName("dataBase").item(0).getTextContent();
        String user     = root.getElementsByTagName("user").item(0).getTextContent();
        String password = root.getElementsByTagName("password").item(0).getTextContent();
        return new ConnectionProperties(server, port, database, user, password);
    }
}
