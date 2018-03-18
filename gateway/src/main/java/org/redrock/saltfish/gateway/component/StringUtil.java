package org.redrock.saltfish.gateway.component;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class StringUtil {
    public boolean isBlank(String str) {
        return str == null || str.trim().equalsIgnoreCase("");
    }

    public boolean hasBlank(String... strs) {
        for (String str : strs) {
            if (str == null || str.equalsIgnoreCase("")) return true;
        }
        return false;
    }

    public String getSHA1Str(String str) {
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] hash = messageDigest.digest(str.getBytes("UTF-8"));
            encodeStr = Hex.encodeHexString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    public String xmlToJson(String xml) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(xml);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String parseXml(String xml) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader reader = new StringReader(xml);
            InputSource inputSource = new InputSource(reader);
            Document document = db.parse(inputSource);
            Element root = document.getDocumentElement();
            NodeList childNodes = root.getChildNodes();
            int len;
            if (childNodes != null && (len = childNodes.getLength()) > 0) {
                for (int i = 0; i < len; i++) {
                    Node childNode = childNodes.item(i);
                    String name = childNode.getNodeName();
                    String value = childNode.getTextContent();
                    System.out.println(name);
                    System.out.println(value);
                    System.out.println(childNode.getNodeType());
                    if (childNode.hasChildNodes()) {
                        NodeList nodeList = childNode.getChildNodes();
                        int length = 0;
                        if (nodeList != null && (length = nodeList.getLength()) > 0) {
                            for (int j = 0; j < length; j++) {
                                Node node = nodeList.item(j);
                                String nName = node.getNodeName();
                                String nValue = node.getNodeValue();
                                System.out.println(nName);
                                System.out.println(nValue);
                                System.out.println(childNode.getNodeType());
                            }
                        }
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
//        String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><breakfast_menu><food><name>Belgian Waffles</name><price>$5.95</price><description>Two of our famous Belgian Waffles with plenty of real maple syrup</description><calories>650</calories></food><food><name>Strawberry Belgian Waffles</name><price>$7.95</price><description>Light Belgian waffles covered with strawberries and whipped cream</description><calories>900</calories></food><food><name>Berry-Berry Belgian Waffles</name><price>$8.95</price><description>Light Belgian waffles covered with an assortment of fresh berries and whipped cream</description><calories>900</calories></food><food><name>French Toast</name><price>$4.50</price><description>Thick slices made from our homemade sourdough bread</description><calories>600</calories></food><food><name>Homestyle Breakfast</name><price>$6.95</price><description>Two eggs, bacon or sausage, toast, and our ever-popular hash browns</description><calories>950</calories></food></breakfast_menu>";
        String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><xml><ToUserName><![CDATA[sdfsdf]]></ToUserName><FromUserName><![CDATA[xsdfxdfsdf]]></FromUserName><CreateTime>123456789</CreateTime><MsgType><![CDATA[zxfczxczxc]]></MsgType><Event><![CDATA[subscribe]]></Event></xml>\n";
        parseXml(xml);
    }
}