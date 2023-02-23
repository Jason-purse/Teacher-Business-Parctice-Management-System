package org.example.management.system.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class ManagementConfiguration implements WebMvcConfigurer {


    @Override
    public void extendMessageConverters(@NotNull List<HttpMessageConverter<?>> converters) {
        // 增加全局Result 处理
        int index = converters.size();
        for (int i = 0; i < converters.size(); i++) {
            if (converters.get(i) instanceof MappingJackson2HttpMessageConverter) {
                index = i;
                break;
            }
        }
        converters.add(index,new ResultMessageConverter());
    }
}
