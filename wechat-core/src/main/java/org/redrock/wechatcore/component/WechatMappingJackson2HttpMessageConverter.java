package org.redrock.wechatcore.component;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class WechatMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
    WechatMappingJackson2HttpMessageConverter(){
        super();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_PLAIN);
        setSupportedMediaTypes(mediaTypes);
    }
}