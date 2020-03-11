package com.example.viikko9_2;

import android.os.StrictMode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TheatreManager {
    private ArrayList<Theatre> theatreList = new ArrayList<Theatre>();
    private static TheatreManager tm = new TheatreManager();

    public TheatreManager() {
    }

    // Singleton for TheatreManager
    public static TheatreManager getInstance() {
        return tm;
    }

    public void getTheatreData() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String url = "https://www.finnkino.fi/xml/TheatreAreas/";
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(url);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getDocumentElement().getElementsByTagName("TheatreArea");
            for (int i=0; i<nList.getLength(); i++) {
                Node node = nList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    int y = Integer.parseInt(element.getElementsByTagName("ID").item(0).getTextContent());
                    Theatre temp = new Theatre(element.getElementsByTagName("Name").item(0).getTextContent(), y);
                    theatreList.add(temp);
                }

            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getSpinnerData()  {
        ArrayList<String> temp = new ArrayList<String>();

        for (int x = 0; x<this.theatreList.size(); x++) {
            temp.add(theatreList.get(x).getName());
        }

        return temp;
    }
}
