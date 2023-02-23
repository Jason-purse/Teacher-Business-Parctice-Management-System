package org.example.management.system.config;

import com.jianyue.lightning.result.Result;
import org.example.management.system.utils.ResultExt;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 实现 Result 套壳
 */
public class ResultMessageConverter extends MappingJackson2HttpMessageConverter {
    @Override
    protected void writeInternal(@NotNull Object object, Type type, @NotNull HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        Result<Object> result = ResultExt.success(object);
        super.writeInternal(result, type, outputMessage);
    }
}
