package Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.dom4j.DocumentException;
import org.junit.Test;

import net.sf.json.JSONObject;
import utils.XmlSAXUtil;

/**
 * Created by dihua.wu on 2018/3/22.
 */
public class ConvertTest {

	private final File xmlFile = new File("src/test/resources/xml/1.2.3.4.119087109070129120.1574228627648.1.xml");

	private XmlSAXUtil xmlSAXUtil = new XmlSAXUtil();

	@Test
	public void testGetXPath() throws DocumentException, IOException{

		JSONObject json = xmlSAXUtil.getXPath(xmlFile,100);
		System.out.println(json.size());
	}


	@Test
	public void testShowContentByXPath() throws DocumentException {
		Map<String,String> map = new HashMap<String,String>();
		map.put("root","/ClinicalDocument/typeId/@root");
		map.put("schl","/ClinicalDocument/@schemaLocation ");
		map.put("DicomStudyUidList","/ClinicalDocument/component/structuredBody/section/text/RISReport/DicomStudyUidList");
		map.put("text","/ClinicalDocument/author/assignedAuthor/assignedAuthoringDevice/softwareName/text()");
		map.put("nil","/ClinicalDocument/component/structuredBody/section/text/RISReport/StudyRoom/@nil");

//		map.put("city","/school/student/address/city");
//		map.put("text","/school/student/name/text()");
//		map.put("nid","/school/student/name/@nid");
//		map.put("author","/ClinicalDocument/author");
		xmlSAXUtil.showContentByXPath(xmlFile,map);
	}

	@Test
	public void testShowContentByRestXPath() throws DocumentException {
		Map<String,String> map = new HashMap<String,String>();
		map.put("root","/ClinicalDocument/typeId/@root");
		map.put("schl","/schl");
		map.put("DicomStudyUidList","/ClinicalDocument/component/structuredBody/section/text/RISReport/DicomStudyUidList");
		map.put("text","/ClinicalDocument/author/assignedAuthor/assignedAuthoringDevice/softwareName/text()");

//		map.put("sid","/school/student/@sid");
//		map.put("nid","/school/student/name/@nid");
//		map.put("city","/school/student/address/city");
		xmlSAXUtil.showContentByRestXPath(xmlFile,map,2);
	}

	@Test
	public void testXpathCountByDepth() throws DocumentException{
		xmlSAXUtil.xpathCountByDepth(xmlFile, 2);
	}

	@Test
	public void testParseXml() throws Exception {
		Map<String, String> xpathValueMap = new TreeMap<String, String>();

		xpathValueMap = xmlSAXUtil.parseXml(xmlFile, 2);
		System.out.println(xpathValueMap.size());
		for (Map.Entry<String, String> entry : xpathValueMap.entrySet()) {
			System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		}
	}


}
