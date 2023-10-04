package com.example.analysParser.parser;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class PDFdataParser {
 public String getPDFData(String filename) throws IOException {
     File f = new File(filename);
     String parsedText;
     PDFParser parser = new PDFParser(new RandomAccessFile(f, "r"));
     parser.parse();
     COSDocument cosDoc = parser.getDocument();
     PDFTextStripper pdfStripper = new PDFTextStripper();


//     for (int i = 0; i < strings.length; i++) {
//         System.out.println(i+" "+strings[i]);
//     }


//     System.out.println(strings[22].substring(2) +"\n"+strings[23].substring(2));
     try(PDDocument pdDoc = new PDDocument(cosDoc)
     ) {
         parsedText = pdfStripper.getText(pdDoc);
         String[] strings = parsedText.split("\n");
         String s = strings[23].substring(2).length()>100?"":"\n"+strings[23].substring(2);
         return strings[22].substring(2) +s;
     } catch (Exception e) {
         try(FileOutputStream fos = new FileOutputStream(new File("//192.168.7.100/c$/JavaServices/analysParser/logs.txt"))) {
             fos.write(("parsePDF_Eror " +e.getMessage()+System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
         }
         return "";
     }

 }

    public String[] getPDFZpp (String filename) throws IOException {
        File f = new File(filename);
        String parsedText;
        PDFParser parser = new PDFParser(new RandomAccessFile(f, "r"));
        parser.parse();
        COSDocument cosDoc = parser.getDocument();
        PDFTextStripper pdfStripper = new PDFTextStripper();
        PDDocument pdDoc = new PDDocument(cosDoc);
        parsedText = pdfStripper.getText(pdDoc);
        String[] strings = parsedText.split("\n");
     for (int i = 0; i < strings.length; i++) {
         System.out.println(i+" "+strings[i]);
     }
        String[] outs = new String[2];


//     System.out.println(strings[22].substring(2) +"\n"+strings[23].substring(2));
        try {
//            outs[0] = strings[23].substring(2).length()>100?"":"\n"+strings[23].substring(2);
//            outs[1] = strings[23].substring(2).length()>100?"":"\n"+strings[23].substring(2);
            return outs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
