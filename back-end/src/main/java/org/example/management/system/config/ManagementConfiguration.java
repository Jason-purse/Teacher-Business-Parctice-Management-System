package org.example.management.system.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.UrlResource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class ManagementConfiguration implements WebMvcConfigurer {

    private final SystemConfigProperties properties;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            registry.addResourceHandler("/attachment")
                    .addResourceLocations(new UrlResource(Paths.get(URI.create(properties.getFileStoreDir()).resolve("attachment")).toUri()));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("提供了一个无效的文件存储路径,无法进行资源映射放行 !!!");
        }
    }
}
