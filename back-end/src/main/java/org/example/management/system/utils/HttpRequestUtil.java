package org.example.management.system.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class HttpRequestUtil {

    private HttpRequestUtil() {

    }
    public static HttpServletRequest getCurrentHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes != null) {
            return attributes.getRequest();
        }
        return null;
    }
}
