package com.example.analysParser.db;

import com.example.analysParser.parser.PDFdataParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
@ComponentScan(value = "com")
public class DBLogic {
    private PDFdataParser parser = new PDFdataParser();

    @Bean
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("");
        dataSource.setUrl("" +
                "encrypt=true;TrustServerCertificate=true;");
        dataSource.setUsername("");
        dataSource.setPassword("");
        return dataSource;
    }
    public Connection getConnection() throws SQLException {
        return  getDataSource().getConnection();
    }




    public int wrightData(String nom, String prenom,  String nele, String field,
                                    String value, String data) throws SQLException, IOException {
        String datac = field + "_Data";
        int out = 0;
        int motconsu = 0;
        int output = 0;

        String query = "declare @pat_id int,\n" +
//                "        @motconsu_id int,\n" +
//                "        @data_motconsu datetime ,\n" +
                "        @id int,\n" +
                "        @exist_id int,\n" +
                "        @output int,\n" +
//                "        @c_motconsu_ int,\n" +
                "        @value varchar(50),\n" +
                "        @data varchar(50)\n" +
                "\n" +
                "set @pat_id =(select top 1 PATIENTS_ID from PATIENTS WITH(NOLOCK) where NOM= '" + nom + "' and PRENOM= '" + prenom +
                "' and NE_LE = '" + nele + "' order by PATIENTS_ID desc)\n" +
                "set @exist_id= (select count (PATIENTS_ID) from DATA_W693_PARSE_ANALYS where PATIENTS_ID=@pat_id) \n" +
//                "set @motconsu_id = (select top 1 MOTCONSU_ID from MOTCONSU WITH(NOLOCK) where MODELS_ID=760 and PATIENTS_ID=@pat_id order by DATE_CONSULTATION desc)\n" +
//                "set @data_motconsu = (select top 1 DATE_CONSULTATION from MOTCONSU WITH(NOLOCK) where MODELS_ID=760 and PATIENTS_ID=@pat_id order by DATE_CONSULTATION desc)\n" +
                "set @value= '" + value + "'\n" +
                "set @data = '" + data + "'\n" +
                "\n" +
                "-- select @pat_id, @motconsu_id\n" +
                "if @exist_id<1 begin" +
//                " if @pat_id is not null and @motconsu_id is not null and CAST (@data_motconsu as date) >= CAST (@data as date)\n" +
//                " if @pat_id is not null and @motconsu_id is not null and DATEDIFF(day, @data, @data_motconsu ) < 180 )\n" +
//                "     begin\n" +
                "     set @id=ISNULL((select max(DATA_W693_PARSE_ANALYS_ID) from DATA_W693_PARSE_ANALYS WITH(NOLOCK)),0)+1\n" +
                "    " +
//                "     set @c_motconsu_= ISNULL((select top 1 MOTCONSU_ID from DATA_W693_PARSED_ANALYSIS WITH(NOLOCK) where PATIENTS_ID=@pat_id order by MOTCONSU_ID desc),0 )\n" +
//                "     if @motconsu_id>@c_motconsu_\n" +
//                "         begin\n" +
                "    insert into DATA_W693_PARSE_ANALYS (DATA_W693_PARSE_ANALYS_ID,PATIENTS_ID, MEDECINS_ID,  " + field + ", " + datac + ")\n" +
                "    values (@id,\n" +
                "            @pat_id, 399,  @value  ,  @data)\n" +
                "             set @output=1\n" +
                "            end else\n" +
                "                begin\n" +
                "                    update DATA_W693_PARSE_ANALYS set " + field + "= '" + value + "', DATA_W693_PARSE_ANALYS." + datac + "=@data where PATIENTS_ID=@pat_id\n" +
                "                     set @output=1\n" +
                "                end\n" +
//                "end\n" +
//                "DATEDIFF(day, @data, @data_motconsu ) >= 180 set @output=1\n" +
                "select @pat_id , @id";


        try (Connection con = getConnection();) {


            Statement st = con.createStatement();
            Statement st1 = con.createStatement();
            Statement st2 = con.createStatement();
//            st.executeUpdate(query);
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                out = rs.getInt(1);
                motconsu = rs.getInt(2);
            }

            String file;
            String folder;


            String pgt = "declare @file varchar(50),\n" +
                    "        @folder varchar(50),\n" +
                    "        @date datetime,\n" +
                    "        @pat int = "+ out+"\n" +
//                    "        @motconsu_date datetime\n" +
//                    "select @pat= PATIENTS_ID, @motconsu_date=DATE_CONSULTATION from MOTCONSU WITH(NOLOCK) where MOTCONSU_ID=" + motconsu + "\n" +
                    " select top 1 @file= IMAGES.FileName, @folder= IMAGES.FOLDER, @date=IMAGES.Date_Consultation " +
                    "from MOTCONSU WITH(NOLOCK) \n" +
                    "join IMAGES on IMAGES.MOTCONSU_ID=MOTCONSU.MOTCONSU_ID where\n" +
                    "IMAGES.PATIENTS_ID =@pat and MODELS_ID=921\n" +
                    "order by MOTCONSU.Date_Consultation desc" +
//                    " if cast(@motconsu_date as date)>=cast(@date as date)\n" +
                    " if (@file is not null and @date is not null )\n" +
                    "    begin select @folder, @file, convert(varchar(25), @date ,104) end\n" +
                    "        else begin select '' end";


            ResultSet rs1 = st1.executeQuery(pgt);
            while (rs1.next()) {
                if (!rs1.getString(1).equals("")) {
                    folder = rs1.getString(1).replace("\\", "/") + "/";
                    file = rs1.getString(2);

                    String result = parser.getPDFData("//192.168.7.100/m$/IMAGES/IMAGES/" +
                            folder + file);
                    String pgtInsert = "update DATA_W693_PARSE_ANALYS set PGT='" + result + "' where PATIENTS_ID =" + out;
                    st2.executeUpdate(pgtInsert);


                }
            }

        } catch (Exception e) {
            e.printStackTrace();
    }

//        System.out.println(nom);
//        System.out.println(prenom);
//        System.out.println(nele);
//        System.out.println(field);
//        System.out.println(value);
//        System.out.println(data);
//        System.out.println(out);
        return out;
    }

    public List<String> getDataMotconsu() throws SQLException {
        List<String> data = new ArrayList<>();
        try (Connection con = getConnection();) {
            Statement stData = con.createStatement();
            ResultSet rs = stData.executeQuery("select MOTCONSU_ID from DATA_W693_PARSED_ANALYSIS WITH(NOLOCK)");
            while (rs.next()) {
                data.add(rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public Set<String> getFileNames() throws SQLException {
        Set<String> set = new HashSet<>();
        Statement statement = getConnection().createStatement();
        ResultSet rs = statement.executeQuery("select file_name from parsed_files");
        while (rs.next()) {
           set.add(rs.getString(1));
        }
        return set;
    }
    public void writeFileName(String name) throws SQLException {
        try (Connection con = getConnection();) {
            Statement statement = con.createStatement();
            statement.executeUpdate("insert into parsed_files (file_name, data_zapisi, ID) " +
                    "values('" + name + "', GETDATE(), ISNULL((select max(id) from parsed_files),0)+1)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getDiagnosis() throws SQLException {
        List<String> list= new ArrayList<>();
        try (Connection con = getConnection();) {

            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(
//                    "select MOTCONSU.MOTCONSU_ID, DATA_DIAGNOSIS.PATIENTS_ID, coalesce(DIAGNOZ, DATA_W671_VRT_ZAKL.DIAGNOZ_), " +
//                    " MOTCONSU.DATE_CONSULTATION, MOTCONSU.KRN_CREATE_USER_ID from MOTCONSU\n" +
//                    "         join DATA_DIAGNOSIS on MOTCONSU.MOTCONSU_ID=DATA_DIAGNOSIS.MOTCONSU_ID\n" +
//                    "          left join DATA_W671_VRT_ZAKL on MOTCONSU.MOTCONSU_ID=DATA_W671_VRT_ZAKL.MOTCONSU_ID" +
//                    "    where (MODELS_ID =846 or MODELS_ID=859 and REC_NAME like '%акушер%') and dbo.DATE(MOTCONSU.KRN_CREATE_DATE) = dbo.DATE(GETDATE())" +
//                    "and (DIAGNOZ like '%Беременность%' or DIAGNOZ_ like '%Беременность%')"
                        "select MOTCONSU.MOTCONSU_ID, MOTCONSU.PATIENTS_ID, DATA_W693_DATA185.RAZMERY_PLODA_SOOTVETSTV1, MOTCONSU.DATE_CONSULTATION,\n" +
                                "                            MOTCONSU.KRN_CREATE_USER_ID from motconsu WITH(NOLOCK)\n" +
                                "                                                        join DATA_W693_DATA185 WITH(NOLOCK) on MOTCONSU.MOTCONSU_ID=DATA_W693_DATA185.MOTCONSU_ID\n" +
                                "                            where MODELS_ID=839 and dbo.DATE(MOTCONSU.KRN_CREATE_DATE) = dbo.DATE(GETDATE())"
            );
            while (rs.next()) {
//            list.add(rs.getString(1)+" "+rs.getString(2)+" "+rs.getString(3) );
                int day = 0;
                try {
                    String diagnoz = rs.getString(3);
                    if (diagnoz != null && !diagnoz.equals("")) {
                    list.add(diagnoz
//                            .substring(0,30)
                                    .replaceAll("[^\\d{1,2}\\s]", " ").trim()
//                    .replaceAll("[^\\d{1,2}]", "")
                    );




//
                    day = Integer.parseInt(
                            diagnoz
//                            .substring(0,30)
//                            .replaceAll("[^\\d{1,2}\\s]", " ").trim().split("\\s+")[0]
                    );
                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String motconsu = rs.getString(1);
                String pat = rs.getString(2);
                Date data = rs.getDate(4);
                String user = rs.getString(5);

                if (day != 0) {
                    writePregnancyLevel(motconsu, pat, data, day, user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void writePregnancyLevel(String motconsu, String pat, Date data, int day, String user) throws SQLException {
        try (Connection con = getConnection();) {
            Statement statement = getConnection().createStatement();
            statement.executeUpdate("if(select count(DATA_W693_PREGNANCY_LEVEL_ID) from DATA_W693_PREGNANCY_LEVEL WITH(NOLOCK) where" +
                    " MOTCONSU_ID='" + motconsu + "' )<1 begin " +
                    "insert into DATA_W693_PREGNANCY_LEVEL (DATA_W693_PREGNANCY_LEVEL_ID, PATIENTS_ID, " +
                    "DATE_CONSULTATION, MOTCONSU_ID, DAY_OF_PREGNANCY, KRN_CREATE_DATE, KRN_MODIFY_USER_ID)\n" +
                    "                                        values ((select max(DATA_W693_PREGNANCY_LEVEL_ID) " +
                    "from DATA_W693_PREGNANCY_LEVEL)+1, '" + pat + "', " + data + ", '" + motconsu + "', " + day + ", GETDATE(), '" + user + "') end ");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
