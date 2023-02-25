package org.example.management.system.config;

import com.generatera.authorization.application.server.config.token.HttpRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class RequestLogFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
      log.info("current request is {},request method is {},request param is {}",request.getRequestURI(), request.getMethod(),HttpRequestUtil.getParameters(request));
      filterChain.doFilter(request,response);
    }
}
