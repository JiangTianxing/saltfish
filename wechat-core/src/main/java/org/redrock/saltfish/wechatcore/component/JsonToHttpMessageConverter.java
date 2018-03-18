package org.redrock.saltfish.wechatcore.component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

public class JsonToHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    public JsonToHttpMessageConverter(){
        super();
        //添加返回处理类型
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_PLAIN);
        setSupportedMediaTypes(mediaTypes);
        //将返回对象中的空值过滤
        this.setObjectMapper(new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL));
    }
}