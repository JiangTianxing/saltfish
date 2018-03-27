package org.redrock.saltfish.gateway.component;

import com.google.gson.JsonObject;
import jdk.nashorn.internal.runtime.regexp.joni.constants.NodeType;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import sun.misc.BASE64Decoder;

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

    /**
     * xml 转化微信
     * @param xml
     * @return
     */
    public String xmlToJson(String xml) {
        String result = "";
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
                JsonObject data = new JsonObject();
                for (int i = 0; i < len; i++) {
                    Node childNode = childNodes.item(i);
                    if (childNode.getNodeType() != NodeType.CANY) {
                        String name = childNode.getNodeName();
                        String value = childNode.getTextContent();
                        data.addProperty(name, value);
                    }
                }
                result = data.toString();
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }

    public String base64Decode(String base64Str) {
        String result = null;
        try {
            BASE64Decoder base64Decoder = new BASE64Decoder();
            result = new String(base64Decoder.decodeBuffer(base64Str), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}