package utils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.dom4j.DocumentException;

import net.sf.json.JSONObject;

/**
 * Created by dihua.wu on 2018/3/22.
 */
public interface XmlMethod {

    /**
     * XML转JSON，输出所有节点的XPath
     *
     * @param xmlFile XML文件
     * @param depth   深度
     * @throws IOException
     * @throws DocumentException
     */
    JSONObject getXPath(File xmlFile, int depth) throws DocumentException, IOException;


    /**
     * 显示map中XPath的内容
     *
     * @param xmlFile XML文件
     * @param map     key 主数据,  value XPath
     * @throws DocumentException
     */
    JSONObject showContentByXPath(File xmlFile, Map<String, String> map)
            throws DocumentException;

    /**
     * 按照深度显示剩余XPath的内容
     *
     * @param xmlFile XML文件
     * @param map     key 主数据,  value XPath
     * @param depth   深度
     * @return
     * @throws DocumentException
     */
    JSONObject showContentByRestXPath(File xmlFile, Map<String, String> map, int depth)
            throws DocumentException;

    /**
     * 获取某个深度下XPath的个数
     *
     * @param xmlFile XML文件
     * @param depth   深度
     * @return
     * @throws DocumentException
     */
    int xpathCountByDepth(File xmlFile, int depth) throws DocumentException;

    /**
     * 解析XML文件里指定深度下节点内容
     * Map<xpath, value>
     *
     * @param xmlFile XML文件
     * @param depth   深度
     * @return
     * @throws Exception
     */
    Map<String, String> parseXml(File xmlFile, int depth) throws Exception;

    /**
     * 解析XML字符串里指定深度下节点内容
     * Map<xpath, value>
     *
     * @param xmlStr XML字符串
     * @param depth  深度
     * @return
     * @throws Exception
     */
    Map<String, String> parseXml(String xmlStr, int depth) throws Exception;

}
