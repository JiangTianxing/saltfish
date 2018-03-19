package org.redrock.saltfish.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.redrock.saltfish.gateway.component.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
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
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        String authentication = request.getHeader("Authorization");
        if (stringUtil.isBlank(authentication)) return null;
        authentication = authentication.trim();
        if (!authentication.startsWith("Bearer ")) return null;
        String accessToken = authentication.substring(authentication.indexOf(" ") + 1);
        String accessTokenKey = "access_token:" + accessToken;
        String jwt;
        if (!redisTemplate.hasKey(accessTokenKey) || stringUtil.isBlank(jwt = redisTemplate.opsForValue().get(accessTokenKey))) {
            response.setCharacterEncoding("UTF-8");
            String msg = "{\"errmsg\":\"access_token 无效\"}";
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.BAD_GATEWAY.value());
            context.setResponseBody(msg);
            return null;
        }
        context.addZuulRequestHeader("Authorization", "jwt " + jwt);
        return null;
    }
}