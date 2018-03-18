package org.redrock.saltfish.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.redrock.saltfish.gateway.component.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class WechatMsgFileter extends ZuulFilter {
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
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        if (pathes.size() > 0) {
            String uri = request.getRequestURI();
            for (String path : pathes) {
                if (uri.equalsIgnoreCase(path)) return true;
            }
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String method = request.getMethod().toLowerCase();
        if (method.equals("get")) return doGet(request, requestContext);
        else if (method.equals("post")) return doPost(request, requestContext);
        return null;
    }

    private Object doGet(HttpServletRequest request, RequestContext context) {
        String result = "";
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String echostr = request.getParameter("echostr");
        String nonce = request.getParameter("nonce");
        if (!stringUtil.hasBlank(signature, timestamp, nonce, echostr)) {
            String[] items = new String[]{timestamp, token, nonce};
            Arrays.sort(items);
            StringBuilder builder = new StringBuilder();
            for (String item : items) {
                builder.append(item);
            }
            String checkStr = stringUtil.getSHA1Str(builder.toString());
            if (checkStr.equalsIgnoreCase(signature)) {
                result = echostr;
            }
        }
        context.setResponseBody(result);
        context.setResponseStatusCode(HttpStatus.OK.value());
        return null;
    }

    private Object doPost(HttpServletRequest request, RequestContext context) {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            request.getInputStream(), "UTF-8"
                    ));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            String xml = builder.toString();
            stringUtil.xmlToJson(xml);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Autowired
    StringUtil stringUtil;

    @Value("${wechat.token}")
    String token;

    @Value("${wechat.msg.path}")
    List<String> pathes;
}
