package org.redrock.saltfish.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.redrock.saltfish.gateway.component.StringUtil;
import org.redrock.saltfish.gateway.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

@Component
public class AuthHeaderFilter extends ZuulFilter {

    private Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    StringUtil stringUtil;
    @Autowired
    UserRepository userRepository;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return !stringUtil.isBlank(RequestContext.getCurrentContext().getRequest().getHeader("Authorization"));
    }

    /**
     * 约定大于配置
     * 如果当前请求header中携带Authroization字段，则自动将其转化为用户信息jwt
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        String[] items = request.getHeader("Authorization").split(" ");
        if (items != null && items.length == 2) {
            String type = items[0];
            String accessToken = items[1];
            String accessTokenKey = "access_token:" + accessToken;
            String userInfoJwt;
            if (!redisTemplate.hasKey(accessTokenKey) || stringUtil.isBlank(userInfoJwt = redisTemplate.opsForValue().get(accessTokenKey))) {
                logger.info("<=====> accessToken 不存在: " + accessTokenKey);
                response.setCharacterEncoding("UTF-8");
                String msg = "{\"errmsg\":\"access_token 无效\"}";
                context.setSendZuulResponse(false);
                context.setResponseStatusCode(HttpStatus.NOT_FOUND.value());
                context.setResponseBody(msg);
                return null;
            }
            if (type.equals("Detailed")) {
                String openId = userRepository.getOpenIdFromUserInfoJwt(userInfoJwt);
                String openIdKey = "openId:" + openId;
                String detailedUserInfoJwt = (String) redisTemplate.opsForHash().get(openIdKey, "detailed_user_info");
                context.addZuulRequestHeader("detailed", detailedUserInfoJwt);
            }
//            context.addZuulRequestHeader("Authorization", "userInfo " + userInfoJwt);
            context.addZuulRequestHeader("userInfo", userInfoJwt);
        }
        return null;
    }
}