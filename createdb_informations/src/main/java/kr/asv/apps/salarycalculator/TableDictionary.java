package kr.asv.salary;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import kr.asv.sqlite.SQLiteAdapter;

/**
 * Created by Administrator on 2016-04-30.
 */
public class TableDictionary {
    private SQLiteAdapter database;
    @SuppressWarnings("FieldCanBeLocal")
    private final String TABLE_NAME = "word_dictionary";
    @SuppressWarnings("FieldCanBeLocal")
    private final String DATA_URL = "http://calculators.asv.kr/salaryCalculator/datas/android.xml";
    public List<DataItem> dataList = new ArrayList<>();
    @SuppressWarnings("FieldCanBeLocal")
    private boolean isDebug = false;

    /**
     * 생성자
     * @param database SQLiteAdapter
     */
    public TableDictionary(SQLiteAdapter database)
    {
        debug("constructor");
        this.database = database;
        init();
    }

    /**
     * 초기 호출 되는 메서드
     */
    private void init(){
        debug("Data Importing..");
        try{
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document xml = documentBuilder.parse(DATA_URL);
            //document.getDocumentElement().normalize();

            Element root = xml.getDocumentElement();
            System.out.println("Root element :" + root.getTagName());

            //NodeList nodeList = element.getElementsByTagName("list");
            //NodeList nodeList = element.getChildNodes();
            NodeList nodeList = root.getElementsByTagName("item");

            if(nodeList.getLength() ==0) return;

            for(int i=0; i<nodeList.getLength(); i++){
                Node nodeItem = nodeList.item(i);
                debug("Data Importing["+i+"]");

                //debug(nodeItem.toString());

                try {
                    DataItem item = new DataItem();

                    //item.id =  ((Element)nodeItem).getElementsByTagName("id").item(0).getTextContent();
                    //item.explanation =  ((Element)nodeItem).getElementsByTagName("explanation").item(0).getTextContent();


                    item.id = getTagValue("id",(Element)nodeItem);
                    item.subject = getTagValue("subject",(Element)nodeItem);
                    item.explanation = getTagValue("explanation",(Element)nodeItem);
                    item.history = getTagValue("history",(Element)nodeItem);
                    item.process = getTagValue("process",(Element)nodeItem);

                    dataList.add(item);

                    debug("Data Importing["+i+"] Success");
                } catch (Exception e) {
                    debug("Exception occur >>"+e.toString());
                }
            }
        } catch (ParserConfigurationException e){
            debug("Exception occur >>"+e.toString());
        } catch (SAXException e){
            debug("SAXException occur >>"+e.toString());
        } catch (IOException e){
            debug("IOException occur >>"+e.toString());
        } catch (Exception e){
            debug("Exception occur >>"+e.toString());
        }
    }


    private String getTagValue(String sTag, Element element) {
        try{
            String result = element.getElementsByTagName(sTag).item(0).getTextContent();
            return result;
        } catch(NullPointerException e){
            return "";
        } catch(Exception e){
            return "";
        }

    }
    /**
     * 조회결과를 디비에 생성
     */
    public void createWithInsert()
    {
        List<String> queries = new ArrayList<>();
        queries.add("drop table if exists "+TABLE_NAME);
        queries.add(
                "create table "+TABLE_NAME+" (key integer primary key, id string, subject string,explanation string,process string,history string)");
        database.execute(queries);
        debug("Create Table ["+TABLE_NAME+"]");
        try
        {
            database.getConnection().setAutoCommit(false);
            PreparedStatement prestmt = database
                    .prepare("insert into "+TABLE_NAME+"(id,subject,explanation,process,history) values (?,?,?,?,?)");
            int i = 0;
            int iSuccess = 0;

            for (DataItem item : dataList)
            {
                // queries.add("insert into
                // devices_android(manufacturer,marketing,device) values
                // ('"+device.manufacturer+"','"+device.marketName+"','"+device.model+"')");

                prestmt.setString(1, item.id);
                prestmt.setString(2, item.subject);
                prestmt.setString(3, item.explanation);
                prestmt.setString(4, item.process);
                prestmt.setString(5, item.history);
                database.execute();
                iSuccess++;
                debug("counting-success[" + iSuccess + "]");

                i++;
            }

            database.getConnection().commit();
            debug("Counting-complete[" + i + "]");
        } catch (SQLException e)
        {
            debug("Error Failed");
        }
        debug("process complete");
    }

    /**
     * 디버깅 메서드
     * @param msg 메시지
     */
    private void debug(String msg)
    {
        if(isDebug)
        System.out.println("[EXIZT-MODULE-DEBUG][DictionaryTable] " + msg);
    }

    public class DataItem{
        String id;
        String subject;
        String explanation;
        String process;
        String history;

        public String toString()
        {
            return "DataItem{id=[" + id + "], subject=["+subject + "] explanation=[" + explanation + "], process=[" + process + "], history=[" + history + "]}";
        }
    }


}

