package org.redrock.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.http.HttpStatus;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthHeaderFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return null;
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
//        try {
//            RequestContext requestContext = RequestContext.getCurrentContext();
//            HttpServletRequest request = requestContext.getRequest();
//            HttpServletResponse response = requestContext.getResponse();
//            String userAccessToken = requestContext.getRequest().getHeader("Authorization");
//            if (null == userAccessToken || "".equalsIgnoreCase(userAccessToken.trim())) {
//                Map<String, String> msg = new HashMap<>();
//                msg.put("errmsg", "没有用户权限");
//                response.setStatus(HttpStatus.BAD_REQUEST.value());
//                response.getWriter().write(msg.toString());
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return null;
    }
}
