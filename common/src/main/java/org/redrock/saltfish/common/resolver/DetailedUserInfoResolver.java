package org.redrock.saltfish.common.resolver;

import org.redrock.saltfish.common.bean.DetailedUserInfo;
import org.redrock.saltfish.common.bean.UserInfo;
import org.redrock.saltfish.common.component.StringUtil;
import org.redrock.saltfish.common.exception.RequestException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

@Component
public class DetailedUserInfoResolver implements HandlerMethodArgumentResolver {

    private final static String attrName = DetailedUserInfo.class.toString();
    private final static String userInfoAttrName = UserInfo.class.toString();

    private Logger logger = Logger.getLogger(getClass().getName());
    private StringUtil stringUtil = new StringUtil();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(DetailedUserInfo.class);
    }

    @Nullable
    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String userInfoJwt = request.getHeader("userInfo");
        if (stringUtil.isBlank(userInfoJwt)) {
            logger.info("<=====> userInfo is " + userInfoJwt);
            throw new RequestException(HttpStatus.NOT_FOUND, "查询不到用户信息");
        }
        String[] items = userInfoJwt.split("\\.");
        if (items.length == 3) {
            String userInfoJson = stringUtil.base64Decode(items[1]);
            UserInfo userInfo = (UserInfo) request.getAttribute(userInfoAttrName);
            DetailedUserInfo detailedUserInfo = new DetailedUserInfo();
            detailedUserInfo.setDetailedUserInfo(userInfoJson);
            detailedUserInfo.setType(userInfo.getType());
            detailedUserInfo.setNickname(userInfo.getType());
            detailedUserInfo.setSex(userInfo.getSex());
            detailedUserInfo.setHeadimgurl(userInfo.getHeadimgurl());
            detailedUserInfo.setOpenid(userInfo.getOpenid());
            System.out.println(detailedUserInfo.toString());
            request.setAttribute(attrName, detailedUserInfo);
            return detailedUserInfo;
        }
        return null;
    }
}

