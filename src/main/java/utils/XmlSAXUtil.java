package utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import net.sf.json.JSONObject;

/**
 *
 * @author dihua.wu
 * @date 2018/3/22
 */
public class XmlSAXUtil implements XmlMethod {

    private static final Logger logger = LogManager.getLogger(XmlSAXUtil.class);

    @Override
    public JSONObject getXPath(File xmlFile, int depth) throws DocumentException {

        if (depth < 0) {
            depth = 0;
        }
        ConvertHandler convertHandler = new ConvertHandler();
        convertHandler.xpathDepth = depth;
        SAXReader saxReader = new SAXReader();
        saxReader.setDefaultHandler(convertHandler);
        saxReader.read(xmlFile);

		return convertHandler.getResult();

    }


    @Override
    public JSONObject showContentByXPath(File xmlFile, Map<String, String> map) throws DocumentException {

        ElementHandler elementHandler = new ConvertHandler();
        SAXReader saxReader = new SAXReader();
        saxReader.setDefaultHandler(elementHandler);
        Document xmlDocument = saxReader.read(xmlFile);

        JSONObject resultJson = new JSONObject();

        for (String key : map.keySet()) {
            String xpath = map.get(key);
            Node node = xmlDocument.selectSingleNode(xpath);
            if (node == null) {
                resultJson.put(key, "");
            } else if (xpath.contains("@") || xpath.contains("text()")) {
                resultJson.put(key, node.getText());
            } else {
                resultJson.put(key, node.asXML());
            }
        }
        logger.info(resultJson.toString(2));
        return resultJson;

    }


    @Override
    public JSONObject showContentByRestXPath(File xmlFile, Map<String, String> map, int depth)
            throws DocumentException {

        ConvertHandler convertHandler = new ConvertHandler();
        SAXReader saxReader = new SAXReader();
        saxReader.setDefaultHandler(convertHandler);
        Document xmlDocument = saxReader.read(xmlFile);

        List<String> xpathList = new ArrayList<String>();
        for (String key : map.keySet()) {
            Node node = xmlDocument.selectSingleNode(map.get(key));
            //key主数据对应的xpath放入xpathList中
            xpathList.add(map.get(key));
            if (node != null) {
                //移除已有的节点
                node.detach();
            }
        }

        //xpathList 剩余的xpath
        convertHandler.allXpathList.removeAll(xpathList);
        xpathList = convertHandler.allXpathList;

        JSONObject resultJson = new JSONObject();
        for (String xpath : xpathList) {
            if (depth < 0) {
                //如果 xpath包含命名空间，则value = ""
                if (xpath.contains(":")) {
                    resultJson.put(xpath, "");
                } else {
                    Node node = xmlDocument.selectSingleNode(xpath);
                    if (node != null) {
                        if (xpath.contains("@")) {

                            resultJson.put(xpath, node.getText());

                        } else if (xpath.contains("text()")) {

                            resultJson.put(xpath, node.getText());

                        } else {

                            resultJson.put(xpath, node.asXML());

                        }
                    }
                }
            }

            if (StringUtils.countMatches(xpath, "/") <= depth) {
                if (xpath.contains(":")) {
                    resultJson.put(xpath, "");
                } else {
                    Node node = xmlDocument.selectSingleNode(xpath);
                    if (node != null) {
                        if (xpath.contains("@")) {

                            resultJson.put(xpath, node.getText());

                        } else if (xpath.contains("text()")) {

                            resultJson.put(xpath, node.getText());

                        } else {

                            resultJson.put(xpath, node.asXML());

                        }
                    }
                }
            }
        }
        logger.info(resultJson.toString(2));
        return resultJson;
    }

    @Override
    public int xpathCountByDepth(File xmlFile, int depth) throws DocumentException {
        ConvertHandler convertHandler = new ConvertHandler();
        SAXReader saxReader = new SAXReader();
        saxReader.setDefaultHandler(convertHandler);
        saxReader.read(xmlFile);

        int xpathCount = 0;

        List<String> xpathList = new ArrayList<String>();

        if (depth < 0) {

            xpathCount = convertHandler.allXpathList.size();

        } else {

            for (String xpath : convertHandler.allXpathList) {
                if (StringUtils.countMatches(xpath, "/") <= depth) {
                    logger.info(xpath);
                    xpathList.add(xpath);
                }
            }

            xpathCount = xpathList.size();
        }

        logger.info("xpathCount:{}", xpathCount);
        return xpathCount;
    }

    @Override
    public Map<String, String> parseXml(File xmlFile, int depth) throws Exception {
        String str = readFile(xmlFile);
        return parseXml(str, depth);
    }

    @Override
    public Map<String, String> parseXml(String xmlStr, int depth) throws Exception {
        ConvertHandler convertHandler = new ConvertHandler();
        SAXReader saxReader = new SAXReader();
        saxReader.setDefaultHandler(convertHandler);
        Document xmlDocument = saxReader.read(new ByteArrayInputStream(xmlStr.getBytes(StandardCharsets.UTF_8)));

        Map<String, String> xpathValueMap = new TreeMap<String, String>();
        List<String> xpathList = new ArrayList<String>();
        xpathList = convertHandler.allXpathList;
        for (String xpath : xpathList) {
            if (depth == showDepth(xpath)) {
                Node node = xmlDocument.selectSingleNode(xpath);
                if (node == null) {
                    xpathValueMap.put(xpath, "");
                } else if (xpath.contains("@") || xpath.contains("text()")) {
                    xpathValueMap.put(xpath, node.getText());
                } else {
                    xpathValueMap.put(xpath, node.asXML());
                }
            }
        }
        return xpathValueMap;
    }

    //计算xpath的深度
    private int showDepth(String xpath) {
        return StringUtils.countMatches(xpath, "/");
    }

    //文件读取
    private String readFile(File xmlFile) {
        String str;
        try (FileInputStream in = new FileInputStream(xmlFile)) {
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            str = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return str;
    }

}
	

