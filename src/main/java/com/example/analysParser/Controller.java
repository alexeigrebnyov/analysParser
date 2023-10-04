package com.example.analysParser;

import com.example.analysParser.db.DBLogic;
import com.example.analysParser.parser.PDFdataParser;
import com.example.analysParser.parser.Parser;
import org.springframework.data.relational.core.sql.In;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

@RestController
public class Controller {
    DBLogic db = new DBLogic();
    Parser p = new Parser();

    public Controller() {
    }

    @GetMapping("/getMotconsu")
    public List<String> getData() throws SQLException {
        return db.getDataMotconsu();
    }

    public static String getRightDate(String date) {
        if (date.length()>8) {date=date.substring(0,8);}
        return date.substring(6,8) +"."+date.substring(4,6)+"." +date.substring(0,4);
    }

    public static Set<String> repairedFiles() throws FileNotFoundException {
        Set<String> set = new HashSet<>();

        Scanner sc = new Scanner(new File("C:/Users/Grebnev_A/list.txt"));
        while (sc.hasNextLine()) {
            set.add(sc.nextLine());
        }
        sc.close();
        return set;

    }
    @Scheduled(fixedDelay = 900000, initialDelay = 15000)
    @GetMapping("/getDiagnoz")
    public List<String> getDiagnoz() throws SQLException {
        List<String> diagnozis = db.getDiagnosis();
//        int i = 0;
//        int day = 0;
//        for (String d: diagnozis) {
//            String[] fst = d.split("\\s+");
////            String[] scn = fst[0].split("-");
////            if (scn.length>1) {
////                i=Integer.parseInt(scn[1]);
////            } else i = Integer.parseInt(fst[0]);
////
////            if (fst[1].length()<=2) {
////                day = Integer.parseInt(fst[1]);
////            }
//
////            System.out.println(fst[0]);
//        }
        return diagnozis;
    }
    @Scheduled(fixedDelay = 600000, initialDelay = 10000)
    @GetMapping("/run")
    public  void mainMethod() throws SQLException, IOException {

        try {

//        File folder = new File("C:/Users/Grebnev_A/anlys");
        File folder = new File("//192.168.7.100/f$/invitro");
        File[] fileList = folder.listFiles();
        Set<String> nameSet = new HashSet<>();
        for (File f : fileList) {
            String name =f.getName();
            if ( p.getExtensionByStringHandling(name).get().equalsIgnoreCase("xml"))
                nameSet.add(name);
        }
//        System.out.println("namesetbnefore "+nameSet);


//        FileOutputStream fos = new FileOutputStream("C:/Users/Grebnev_A/list.txt", true);
//        DBLogic logic = new DBLogic();
        nameSet.removeAll(db.getFileNames());
//        System.out.println("namesetafter "+nameSet);
        for (String s : nameSet) {


//        for (int i = 0; i < nameSet.size() ; i++) {
//                    File file = fileList[i];
//                    String filename = file.getName();


//                if (file.isFile() && p.getExtensionByStringHandling(filename).get().equalsIgnoreCase("xml"))  {
//                    BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
//                    System.out.println("lastModifiedTime: " + attr.lastModifiedTime());
//            Map<String, String> map = p.values("C:/Users/Grebnev_A/anlys/" + s);
            Map<String, String> map = p.values("//192.168.7.100/f$/invitro/"+s);

//                    System.out.println(getRightDate(map.get("BirthDate")));
            if (map != null) {
//                System.out.println("map "+map);

                for (String atr : map.keySet()) {
                    String field = p.getMap().get(atr);
//                    System.out.println("atr "+atr);
//                    System.out.println("field "+field);
                    if (field != null) {
                        String[] io = map.get("FirstName").split(" ");
                        int out = db.wrightData(map.get("Surname"), io[0], getRightDate(map.get("BirthDate")), field, map.get(atr), getRightDate(map.get(atr + "_Date")));
//                        System.out.println("out "+out);
//                        if (out != 0) {
                            System.out.println(map);
//                            fos.write((s + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
                            db.writeFileName(s);

//                        }

                    }
//                    System.out.println(s);


                }


            } else {
//                fos.write((s + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
                db.writeFileName(s);
            }

//                }
//        }

//        logic.getSomeData()
//                .stream()
//                .forEach(System.out::println);
        }
//        fos.close();

//        System.out.println(p.values("//192.168.7.100/f$/invitro/185874460_210414732_0_БЕХТУГАНОВА.XML"));

        } catch (Exception e) {
//          try(FileOutputStream fos = new FileOutputStream(new File("//192.168.7.102/c$/JavaServices/analysParser/logs.txt"))) {
//              fos.write(("MainMethodError " +e.getMessage()+System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
//          }
            e.printStackTrace();
        }


    }
    public List<Integer> getParsedData(String source) {
        List<Integer> integers = new ArrayList<>();
        Scanner sc = new Scanner(" sdfsfg 25 35 56");

        int i =0;
        while (sc.hasNextLine()) {
            Scanner sc1 = new Scanner(sc.nextLine());
            while (sc1.hasNextInt()) {
                integers.add(sc1.nextInt());
            }
            sc1.hasNextInt();
        }
        return integers;
    }
}
