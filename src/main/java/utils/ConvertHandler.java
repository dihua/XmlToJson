package utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.*;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.dom4j.tree.DefaultElement;

/**
 * Created by dihua.wu on 2018/3/22.
 */
public class ConvertHandler implements ElementHandler {

    private static final Logger logger = LogManager.getLogger(ConvertHandler.class);

    //输出的JSON
    JSONObject wholeJson = new JSONObject();

    //所有节点的XPath集合
    List<String> allXpathList = new ArrayList<String>();

    //配置文件中的深度
    int xpathDepth;

    public void onStart(ElementPath elementPath) {

    }

    /**
     * currentElement     	当前节点
     * currentElementName 	当前节点名称
     * currentElementText 	当前节点文本内容
     * elePath 	                                    元素XPath
     * textPath           	文本XPath
     * attXPath     	            属性XPath
     * childJson            元素子JSON
     * attJson              属性JSON
     * textJson             文本JSON
     */
    public void onEnd(ElementPath elementPath) {

        Element currentElement = elementPath.getCurrent();
		if (currentElement instanceof DefaultElement) {
			((DefaultElement) currentElement).setNamespace(Namespace.NO_NAMESPACE);
		}

		try {
            convert(currentElement);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void convert(Element currentElement) throws Exception {

        //元素XPath,文本XPath,属性XPath都存入allXPathList中
        String elePath = currentElement.getUniquePath();
        allXpathList.add(elePath);

        String textPath = elePath + "/text()";
        String currentElementText = currentElement.getText().trim();
        //如果当前节点有文本内容
        if (currentElementText.length() > 0) {
            allXpathList.add(textPath);
        }

        String attPath = null;
        for (int i = 0; i < currentElement.attributeCount(); i++) {
            attPath = currentElement.attribute(i).getUniquePath();
            allXpathList.add(attPath);
        }

        if (currentElement.isRootElement()) {
            showElement(currentElement);
        }
    }

    //输出元素节点
    public JSONObject showElement(Element element) {
        logger.info(element.getNamespace().getStringValue());
        JSONObject childJson = new JSONObject();
        childJson.put("xpath", element.getUniquePath());
        logger.info("----{}", element.getUniquePath());
        childJson.put("width", 20);
        showElementContent(element, childJson);

        //用JSONArray 包裹  JSONObject
        JSONArray childJsonArray = new JSONArray();
        childJsonArray.add(childJson);
        if (showDepth(element.getUniquePath()) <= xpathDepth) {
            wholeJson.put("children", childJsonArray);
        }

        return wholeJson;
    }

    //输出元素节点内容
    public void showElementContent(Element element, JSONObject json) {
        //attribute
        if (element.attributeCount() > 0) {
            Iterator<Attribute> attIterator = element.attributeIterator();
            while (attIterator.hasNext()) {
                Attribute attribute = (Attribute) attIterator.next();
                //深度判断
                if (showDepth(attribute.getUniquePath()) <= xpathDepth) {
                    JSONObject attJson = new JSONObject();
                    attJson.put("xpath", attribute.getUniquePath());
                    attJson.put("width", 20 * showDepth(attribute.getUniquePath()));

                    //判断json中是否存在key="children"的,有则再添加children json,无则添加children jsonarray
                    if (json.containsKey("children")) {
                        json.accumulate("children", attJson);
                    } else {
                        JSONArray attJsonArray = new JSONArray();
                        attJsonArray.add(attJson);
                        json.accumulate("children", attJsonArray);
                    }

                }
            }
        }
        //子节点
        if (element.elements().size() > 0) {
            Iterator<Element> eleIterator = element.elementIterator();
            while (eleIterator.hasNext()) {
                Element childElement = eleIterator.next();
                JSONObject childJson = new JSONObject();
                childJson.put("xpath", childElement.getUniquePath());
                childJson.put("width", 20 * showDepth(childElement.getUniquePath()));
                if (childElement.elements().size() > 0) {
                    showElementContent(childElement, childJson);
                } else {
                    //子节点 attribute
                    if (childElement.attributeCount() > 0) {
                        Iterator<Attribute> childAttIterator = childElement.attributeIterator();
                        while (childAttIterator.hasNext()) {
                            Attribute attribute = (Attribute) childAttIterator.next();
                            JSONObject attJson = new JSONObject();
                            attJson.put("xpath", attribute.getUniquePath());
                            attJson.put("width", 20 * showDepth(attribute.getUniquePath()));
                            //深度判断
                            if (showDepth(attribute.getUniquePath()) <= xpathDepth) {
                                if (childJson.containsKey("children")) {
                                    childJson.accumulate("children", attJson);
                                } else {
                                    JSONArray attJsonArray = new JSONArray();
                                    attJsonArray.add(attJson);
                                    childJson.accumulate("children", attJsonArray);
                                }
                            }
                        }
                    }
                    //子节点 text
                    if (childElement.getText().trim().length() > 0) {
                        JSONObject textJson = new JSONObject();
                        textJson.put("xpath", childElement.getUniquePath() + "/text()");
                        textJson.put("width", 20 * showDepth(childElement.getUniquePath() + "/text()"));
                        //深度判断
                        if (showDepth(childElement.getUniquePath() + "/text()") <= xpathDepth) {
                            if (childJson.containsKey("children")) {
                                childJson.accumulate("children", textJson);
                            } else {
                                JSONArray textJsonArray = new JSONArray();
                                textJsonArray.add(textJson);
                                childJson.accumulate("children", textJsonArray);
                            }
                        }
                    }
                }
                //深度判断
                if (showDepth(childElement.getUniquePath()) <= xpathDepth) {
                    if (json.containsKey("children")) {
                        json.accumulate("children", childJson);
                    } else {
                        JSONArray childJsonArray = new JSONArray();
                        childJsonArray.add(childJson);
                        json.accumulate("children", childJsonArray);
                    }
                }
            }
        } else {
            if (element.getText().trim().length() > 0) {
                JSONObject textJson = new JSONObject();
                textJson.put("xpath", element.getUniquePath() + "/text()");
                textJson.put("width", 20 * showDepth(element.getUniquePath() + "/text()"));
                //深度判断
                if (showDepth(element.getUniquePath() + "/text()") <= xpathDepth) {
                    if (json.containsKey("children")) {
                        json.accumulate("children", textJson);
                    } else {
                        JSONArray textJsonArray = new JSONArray();
                        textJsonArray.add(textJson);
                        json.accumulate("children", textJsonArray);
                    }
                }
            }
        }
    }

    //计算xpath的深度
    public int showDepth(String xpath) {
        int depth = StringUtils.countMatches(xpath, "/");
        return depth;
    }

    //显示最终JSON
    public JSONObject getResult() {
        logger.info("\n{}", wholeJson.toString(2));
        return wholeJson;
    }


}
