package org.redrock.saltfish.common.bean;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class DetailedUserInfo extends CurlError{
    private String detailedUserInfo;
    private String openid;
    private String nickname;
    private String headimgurl;
    private String unionid;
    private int sex;
    private String type;
}
