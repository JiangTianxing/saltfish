package org.redrock.wechatcore.bean;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Token extends WechatError{
    @JsonAlias("access_token")
    String accessToken;

    @JsonAlias("expires_in")
    int expiresIn;

    @JsonAlias("refresh_token")
    String refreshToken;

    @JsonAlias("openid")
    String openid;

    @JsonAlias("scope")
    String scope;
}
