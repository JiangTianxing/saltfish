package org.redrock.saltfish.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.redrock.saltfish.gateway.component.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthHeaderFilter extends ZuulFilter {

    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    StringUtil stringUtil;

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
//        String[] authItems = request.getHeader("Authorization").split(" ");

        //        String accessToken = authentication.substring(authentication.indexOf(" ") + 1);
//        String accessTokenKey = "access_token:" + accessToken;
//        String userinfo;
//        if (!redisTemplate.hasKey(accessTokenKey) || stringUtil.isBlank(userinfo = redisTemplate.opsForValue().get(accessTokenKey))) {
//            response.setCharacterEncoding("UTF-8");
//            String msg = "{\"errmsg\":\"access_token 无效\"}";
//            context.setSendZuulResponse(false);
//            context.setResponseStatusCode(HttpStatus.NOT_FOUND.value());
//            context.setResponseBody(msg);
//            return null;
//        }
//        context.addZuulRequestHeader("Authorization", "userInfo " + userinfo);
////        if (stringUtil.isBlank(request.getHeader("detailed")))
//        String expect = request.getref
//        return null;
        String[] items = request.getHeader("Authorization").split(" ");
        if (items != null && items.length == 2) {
            String type = items[0];
            String accessToken = items[1];
            String accessTokenKey = "access_token:" + accessToken;
            String userinfo;
            if (!redisTemplate.hasKey(accessTokenKey) || stringUtil.isBlank(userinfo = redisTemplate.opsForValue().get(accessTokenKey))) {
                response.setCharacterEncoding("UTF-8");
                String msg = "{\"errmsg\":\"access_token 无效\"}";
                context.setSendZuulResponse(false);
                context.setResponseStatusCode(HttpStatus.NOT_FOUND.value());
                context.setResponseBody(msg);
                return null;
            }
//        context.addZuulRequestHeader("Authorization", "userInfo " + userinfo);
//            DispatcherServlet dispatcherServlet = new DispatcherServlet();
        }
        return null;
    }
}