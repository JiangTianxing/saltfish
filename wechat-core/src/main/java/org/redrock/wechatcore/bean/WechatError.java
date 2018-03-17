package org.redrock.wechatcore.bean;

import lombok.Data;

@Data
public class WechatError {
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
