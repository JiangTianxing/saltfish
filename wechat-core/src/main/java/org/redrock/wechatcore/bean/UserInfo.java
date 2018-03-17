package org.redrock.wechatcore.bean;

import lombok.Data;

@Data
public class UserInfo extends WechatError{
    private String openid;
    private String nickname;
    private String headimgurl;
    private String unionid;
    private int sex;
}
