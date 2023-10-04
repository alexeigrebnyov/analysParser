package com.example.analysParser.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Parser {

    public Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }
    public Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        map.put("Herpes simplex virus II (кач.)ДНК","VPG_2_PCR");
        map.put("Herpes simplex virus I (кач.)ДНК","VPG");
        map.put("Herpes simplex virus I,II (кач.)ДНК","VPG_1_2_PCR");
        map.put("Cytomegalovirus (кач.) ДНК","CMV_PCR");
        map.put("Посев на Ureaplasma spp. и АЧ","UREAPLAZMA_POSEV");
        map.put("Посев на M. hominis и АЧ","MIKOPLAZMA_POSEV");
        map.put("anti - HSV (1 и 2 типов) IgG","ANTI_VPG_1_2_IGG");
        map.put("anti - HSV (1 и 2 типов) IgM","ANTI_VPG_1_2_IGM");
        return map;

    }


    public Map<String,String> values(String FILENAME) throws IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Map<String,String> values = new HashMap<>();





        try {



                // optional, but recommended
                // process XML securely, avoid attacks like XML External Entities (XXE)
                dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

                // parse XML file
                DocumentBuilder db = dbf.newDocumentBuilder();

                Document doc = db.parse(new File(FILENAME));

                // optional, but recommended
                // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                doc.getDocumentElement().normalize();

            Element pat = (Element) doc.getElementsByTagName("Patient").item(0);
            Element sent = (Element) doc.getElementsByTagName("Sent").item(0);


//           NodeList list = doc.getElementsByTagName("Requisition").item(0).getChildNodes();
//            for (int i = 0; i < list.getLength(); i++) {
//                System.out.println(list.item(i).getNodeName());
//            }
            values.put("Surname",pat.getAttribute("Surname"));
            values.put("FirstName",pat.getAttribute("FirstName"));
            values.put("BirthDate", pat.getAttribute("BirthDate"));

             NodeList tags =   doc.getElementsByTagName("Analysis");
             NodeList tagsPosev =   doc.getElementsByTagName("Culture");
            for (int i = 0; i < tags.getLength(); i++) {
              Element el = (Element)  tags.item(i);
              Element elPosev = (Element)  tagsPosev.item(i);
              String atr = el.getAttribute("AnaName");
              String atrPosev = elPosev!=null?!elPosev.getAttribute("Growth").equals("")?elPosev.getAttribute("Growth"):elPosev.getAttribute("Finding"):"";
              if (getMap().get(atr)!=null) {
                  values.put(atr, !el.getAttribute("Value").equals("")?el.getAttribute("Value"):atrPosev + " " + el.getAttribute("Unit"));
                  values.put(atr + "_" + "Date", !el.getAttribute("AnalysisDateTime").equals("")?el.getAttribute("AnalysisDateTime"):sent!=null?sent.getAttribute("DateTime"):"");
              }
            }
//                int indexVal = 0;
//                int indexPat = 0;
//
//                NodeList list = doc.getElementsByTagName("Requisition").item(0).getChildNodes();
//               int l = list.getLength();
////            System.out.println(l);
//                if (l==9) {
//                    indexVal=7;
//                    indexPat=3;
//                } else {
//                    if (l==11) {
//                        indexVal = 9;
//                        indexPat = 5;
//                    } else {indexVal=11; indexPat=7;}
//                }
//                NodeList valList = list.item(indexVal).getChildNodes().item(1).getChildNodes();

//                for (int i = 0; i < valList.getLength(); i++) {
//                    System.out.println(valList.item(i).getNodeName());
//                }

//                Element elementValue = (Element) valList.item(1);
//                Element elementPat = (Element) list.item(indexPat);
//                values.put("Value",elementValue.getAttribute("Value"));
//                values.put("Unit",elementValue.getAttribute("Unit"));
//                values.put("AnalysisDateTime",elementValue.getAttribute("AnalysisDateTime"));
//                values.put("AnaName",elementValue.getAttribute("AnaName"));
//                values.put("Surname",elementPat.getAttribute("Surname"));
//                values.put("FirstName",elementPat.getAttribute("FirstName"));
//                values.put("BirthDate", elementPat.getAttribute("BirthDate"));


        }catch (ParserConfigurationException | SAXException | IOException e) {
            try(FileOutputStream fos = new FileOutputStream(new File("//192.168.7.100/c$/JavaServices/analysParser/logs.txt"))) {
              fos.write(("parseEror " +e.getMessage()+System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            }




        }


                return values.keySet().size()>3? values:null;
    }
}
