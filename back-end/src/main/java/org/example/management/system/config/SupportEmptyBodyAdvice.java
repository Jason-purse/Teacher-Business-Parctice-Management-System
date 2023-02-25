package org.example.management.system.config;

import org.example.management.system.utils.ResultExt;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.util.MethodInvocationUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
@RestControllerAdvice(basePackages = "org.example.management.system.controller")
@Order(Ordered.LOWEST_PRECEDENCE)
public class SupportEmptyBodyAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(@NotNull MethodParameter returnType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getParameterType() == void.class || Void.class == returnType.getParameterType();
    }

    @Override
    public Object beforeBodyWrite(Object body, @NotNull MethodParameter returnType, @NotNull MediaType selectedContentType, @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType, @NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response) {
        // 直接返回success !!!
        return ResultExt.success();
    }
}
