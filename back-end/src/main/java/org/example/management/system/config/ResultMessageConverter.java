package org.example.management.system.config;

import com.jianyue.lightning.result.Result;
import org.example.management.system.utils.HttpRequestUtil;
import org.example.management.system.utils.ResultExt;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 实现 Result 套壳
 *
 * 去掉swagger响应封装 ...
 */
public class ResultMessageConverter extends MappingJackson2HttpMessageConverter {

    private List<String> whiteUrlList;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public ResultMessageConverter(List<String> whiteUrlList) {
        this.whiteUrlList = whiteUrlList;
    }

    public void setWhiteUrlList(List<String> whiteUrlList) {
        this.whiteUrlList = whiteUrlList;
    }

    @Override
    protected void writeInternal(@NotNull Object object, Type type, @NotNull HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if(!(object instanceof Result<?>)) {
            HttpServletRequest request = HttpRequestUtil.getCurrentHttpServletRequest();
            if(request != null) {
                String requestURI = request.getRequestURI();
                boolean needWrapper = true;
                if(whiteUrlList != null && whiteUrlList.size() > 0) {
                    for (String s : whiteUrlList) {
                        if (antPathMatcher.match(s,requestURI)) {
                            needWrapper = false;
                            break;
                        }
                    }

                }

                if(needWrapper) {
                    object = ResultExt.success(object);
                }
            }
        }

        super.writeInternal(object, type, outputMessage);
    }
}
