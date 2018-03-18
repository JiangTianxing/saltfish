package org.redrock.saltfish.common.component;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class StringUtil {
    public boolean isBlank(String str) {
        return str == null || str.trim().equalsIgnoreCase("");
    }

    public String getSHA256Str(String str) {
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(str.getBytes("UTF-8"));
            encodeStr = Hex.encodeHexString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    public String base64Encode(String str) {
        return Base64.encodeBase64String(str.getBytes());
    }

    public String base64Decode(String str) {
        return new String(Base64.decodeBase64(str));
    }
}