package org.redrock.saltfish.common.bean;

import lombok.Data;

@Data
public class CurlError {
    private Integer errcode;

    private String errmsg;

    public boolean valid(){
        return errcode == null || errcode == 0;
    }

    @Override
    public String toString() {
        return "WxError{" +
                "errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                '}';
    }
}