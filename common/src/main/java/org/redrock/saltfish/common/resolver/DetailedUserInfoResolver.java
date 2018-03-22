package org.redrock.saltfish.common.resolver;

import org.redrock.saltfish.common.bean.DetailedUserInfo;
import org.redrock.saltfish.common.bean.UserInfo;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import javax.servlet.http.HttpServletRequest;

@Component
public class DetailedUserInfoResolver implements HandlerMethodArgumentResolver {

    private final static String attrName = UserInfo.class.toString();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(DetailedUserInfo.class);
    }

    @Nullable
    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        UserInfo userInfo;
        if ((userInfo = (UserInfo) request.getAttribute(attrName)) == null) return null;
        DetailedUserInfo detailedUserInfo = new DetailedUserInfo();
        detailedUserInfo.setOpenid(userInfo.getOpenid());
        detailedUserInfo.setHeadimgurl(userInfo.getHeadimgurl());
        detailedUserInfo.setSex(userInfo.getSex());
        detailedUserInfo.setNickname(userInfo.getNickname());
        detailedUserInfo.setType(userInfo.getType());
        detailedUserInfo.setDetailedUserInfo(request.getHeader("detailed").split("\\.")[1]);
        return detailedUserInfo;
    }
}
