package org.redrock.saltfish.common.bean;

import lombok.Data;

@Data
public class UserInfo extends CurlError{
    private String openid;
    private String nickname;
    private String headimgurl;
    private String unionid;
    private int sex;
}