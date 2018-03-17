package org.redrock.gateway.filter;

import com.google.gson.Gson;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.redrock.gateway.component.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

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
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String accessToken = request.getHeader("Authentication");
        Gson gson = new Gson();
        Map<String, String> errors = new HashMap<>();
        if (stringUtil.isBlank(accessToken)) {
            errors.put("errmsg", "access_token 不可为空");
            String errmsg = gson.toJson(errors);
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseStatusCode(HttpStatus.BAD_REQUEST.value());
            requestContext.setResponseBody(errmsg);
            return null;
        }
        String key = "access_token:" + accessToken;
        String jwt;
        if (!redisTemplate.hasKey(key) || stringUtil.isBlank(jwt = redisTemplate.opsForValue().get(key))) {
            errors.put("errmsg", "access_token 无效");
            String errmsg = gson.toJson(errors);
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseStatusCode(HttpStatus.BAD_REQUEST.value());
            requestContext.setResponseBody(errmsg);
            return null;
        }
        requestContext.addZuulRequestHeader("Authentication", jwt);
        return null;
    }
}
