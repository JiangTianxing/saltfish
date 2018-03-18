package org.redrock.saltfish.wechatcore.bean;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.ToString;
import org.redrock.saltfish.common.bean.CurlError;

@Data
@ToString
public class Token extends CurlError {
    @JsonAlias("access_token")
    String accessToken;

    @JsonAlias("expires_in")
    long expiresIn;

    @JsonAlias("refresh_token")
    String refreshToken;

    @JsonAlias("openid")
    String openid;

    @JsonAlias("scope")
    String scope;
}
