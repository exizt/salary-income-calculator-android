package kr.asv.apps.salarycalculator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import kr.asv.sqlite.SQLiteAdapter;

/**
 * 단어 사전 Database 테이블
 * Created by EXIZT on 2016-04-30.
 */
class WordDictionaryTable {
	@SuppressWarnings("FieldCanBeLocal")
	private final String TABLE_NAME = "word_dictionary";
	@SuppressWarnings("FieldCanBeLocal")
	private final String DATA_URL = "https://chosim.asv.kr/salary_description.xml";
	private final SQLiteAdapter database;
	private final List<DataItem> dataList = new ArrayList<>();
	@SuppressWarnings("FieldCanBeLocal")
	private final boolean isDebug = false;
	private String debugTag = "[EXIZT-DEBUG][WordDictionaryTable]";

	/**
	 * 생성자
	 *
	 * @param database SQLiteAdapter
	 */
	WordDictionaryTable(SQLiteAdapter database) {
		debug("constructor");
		this.database = database;
		init();
	}

	/**
	 * 초기 호출 되는 메서드
	 */
	private void init() {
		generatedDataList();
	}


	/**
	 * 초기 호출 되는 메서드
	 */
	private void generatedDataList() {
		try {
			URL url = new URL(DATA_URL);
			HttpURLConnection connection;
			connection = (HttpURLConnection) url.openConnection();

			//connection.setRequestMethod("POST");
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
			connection.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8");
			connection.setRequestProperty("CONTENT-TYPE", "text/xml");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			connection.setConnectTimeout(8000);
			connection.setReadTimeout(8000);
			//connection.setDoOutput(true);

			Document document = getDocument(connection.getInputStream());
			document.getDocumentElement().normalize();
			setDataListFromXMLDocument(document);

		} catch (ParserConfigurationException e) {
			debug("ParserConfigurationException", true);
			debug("Exception occur >>" + e.toString());
		} catch (SAXException e) {
			debug("SAXException", true);
			debug("SAXException occur >>" + e.toString());
		} catch (IOException e) {
			debug("IOException", true);
			debug("IOException occur >>" + e.toString());
		} catch (Exception e) {
			debug("Exception", true);
			debug("Exception occur >>" + e.toString());
		}
	}

	/**
	 * InputStream to Document 메서드
	 *
	 * @param stream stream
	 * @return Document
	 * @throws Exception exception
	 */
	private Document getDocument(InputStream stream)
			throws Exception {
		DocumentBuilderFactory objDocumentBuilderFactory;
		DocumentBuilder objDocumentBuilder;
		Document doc;
		try {
			objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
			objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();

			doc = objDocumentBuilder.parse(stream);
		} catch (Exception ex) {
			debug(ex.toString());
			throw ex;
		}
		return doc;
	}

	/**
	 * @param document document 개체
	 */
	private void setDataListFromXMLDocument(Document document) {
		NodeList nodeList = document.getDocumentElement().getElementsByTagName("item");
		if (nodeList.getLength() == 0) return;
		int iSuccessCount = 0;
		debug("Data Parsing..", true);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node nodeItem = nodeList.item(i);
			debug("Data Parsing[" + i + "]");

			try {
				DataItem item = new DataItem();
				//item.id =  ((Element)nodeItem).getElementsByTagName("id").item(0).getTextContent();
				//item.explanation =  ((Element)nodeItem).getElementsByTagName("explanation").item(0).getTextContent();
				item.id = getTagValue("id", (Element) nodeItem);
				item.subject = getTagValue("subject", (Element) nodeItem);
				item.explanation = getTagValue("explanation", (Element) nodeItem);
				item.history = getTagValue("history", (Element) nodeItem);
				item.process = getTagValue("process", (Element) nodeItem);

				dataList.add(item);

				iSuccessCount++;
				debug("Data Parsing Success [" + iSuccessCount + "]", true);
			} catch (Exception e) {
				debug("Exception occur >>" + e.toString());
			}
		}
		debug("Data has been generated. [" + iSuccessCount + "]", true);
	}

	/**
	 * 초기 호출 되는 메서드
	 * http 연결은 잘 되는데, https 연결이 안 됨. 403 에러를 리턴.
	 *
	 * @deprecated deprecated
	 */
	@SuppressWarnings("unused")
	private void init_deprecated() {
		debug("Data Importing..", true);

		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document xml = documentBuilder.parse(DATA_URL);
			//document.getDocumentElement().normalize();
			//-- 여기까지는 도큐먼트를 읽어오는 과정

			// >> 여기부터는 파싱 과정
			Element root = xml.getDocumentElement();
			System.out.println("Root element :" + root.getTagName());

			//NodeList nodeList = element.getElementsByTagName("list");
			//NodeList nodeList = element.getChildNodes();
			NodeList nodeList = root.getElementsByTagName("item");

			if (nodeList.getLength() == 0) return;

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nodeItem = nodeList.item(i);
				debug("Data Importing[" + i + "]", true);

				//debug(nodeItem.toString());

				try {
					DataItem item = new DataItem();
					//item.id =  ((Element)nodeItem).getElementsByTagName("id").item(0).getTextContent();
					//item.explanation =  ((Element)nodeItem).getElementsByTagName("explanation").item(0).getTextContent();
					item.id = getTagValue("id", (Element) nodeItem);
					item.subject = getTagValue("subject", (Element) nodeItem);
					item.explanation = getTagValue("explanation", (Element) nodeItem);
					item.history = getTagValue("history", (Element) nodeItem);
					item.process = getTagValue("process", (Element) nodeItem);

					dataList.add(item);

					debug("Data Importing[" + i + "] Success");
				} catch (Exception e) {
					debug("Exception occur >>" + e.toString());
				}
			}
		} catch (ParserConfigurationException e) {
			debug("ParserConfigurationException", true);
			debug("Exception occur >>" + e.toString());
		} catch (SAXException e) {
			debug("SAXException", true);
			debug("SAXException occur >>" + e.toString());
		} catch (IOException e) {
			debug("IOException", true);
			debug("IOException occur >>" + e.toString());
		} catch (Exception e) {
			debug("Exception", true);
			debug("Exception occur >>" + e.toString());
		}
	}

	private String getTagValue(String sTag, Element element) {
		try {
			return element.getElementsByTagName(sTag).item(0).getTextContent();
		} catch (Exception e) {
			return "";
		}

	}

	/**
	 * 조회결과를 디비에 생성
	 */
	void createDatabase() {
		List<String> queries = new ArrayList<>();
		queries.add("drop table if exists " + TABLE_NAME);
		queries.add(
				"create table " + TABLE_NAME + " (key integer primary key, id string, subject string,explanation string,process string,history string)");
		database.execute(queries);
		debug("Create Table [" + TABLE_NAME + "]");
		try {
			database.getConnection().setAutoCommit(false);
			PreparedStatement statement = database
					.prepare("insert into " + TABLE_NAME + "(id,subject,explanation,process,history) values (?,?,?,?,?)");
			int i = 0;
			int iSuccess = 0;

			for (DataItem item : dataList) {
				// queries.add("insert into
				// devices_android(manufacturer,marketing,device) values
				// ('"+device.manufacturer+"','"+device.marketName+"','"+device.model+"')");

				statement.setString(1, item.id);
				statement.setString(2, item.subject);
				statement.setString(3, item.explanation);
				statement.setString(4, item.process);
				statement.setString(5, item.history);
				database.execute();
				iSuccess++;
				debug("counting-success[" + iSuccess + "]");

				i++;
			}

			database.getConnection().commit();
			debug("Counting-complete[" + i + "]");
		} catch (SQLException e) {
			debug("Error Failed");
		}
		debug("process complete");
	}

	/**
	 * 디버깅 메서드
	 *
	 * @param msg messages
	 */
	@SuppressWarnings("unused")
	private void debug(String msg) {
		debug(msg, false);
	}

	/**
	 * 디버깅 메서드.
	 * 강제적으로 출력하고 싶을 때에 사용한다.
	 * debug("messages",true)
	 *
	 * @param msg messages
	 */
	@SuppressWarnings({"unused", "SameParameterValue"})
	private void debug(String msg, boolean debug) {
		//noinspection ConstantConditions
		if (isDebug || debug) {
			System.out.println(debugTag + msg);
		}
	}

	@SuppressWarnings("unused")
	public void setDebugTag(String debugTag) {
		this.debugTag = debugTag;
	}

	class DataItem {
		String id;
		String subject;
		String explanation;
		String process;
		String history;

		public String toString() {
			return "DataItem{id=[" + id + "], subject=[" + subject + "] explanation=[" + explanation + "], process=[" + process + "], history=[" + history + "]}";
		}
	}
}

