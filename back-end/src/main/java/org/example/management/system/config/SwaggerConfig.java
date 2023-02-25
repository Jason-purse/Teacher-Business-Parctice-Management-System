package org.example.management.system.config;

import com.jianyue.lightning.boot.starter.util.ElvisUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.boot.starter.autoconfigure.SpringfoxConfigurationProperties;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
@RequiredArgsConstructor
public class SwaggerConfig  implements WebMvcConfigurer {

    private final SpringfoxConfigurationProperties properties;

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
        converters.add(index,new ResultMessageConverter(
                List.of(
//                        |/configuration/security | /swagger-resources/configuration/security | Configuring swagger-ui security
//                |/configuration/ui | /swagger-resources/configuration/ui | Configuring swagger-ui options
                getUrlPattern("/swagger-resources/**"),
                getUrlPattern("/v2/api-docs/**")

        )));
    }

    private String getUrlPattern(String pattern) {
        return StringUtils
                .trimTrailingCharacter(ElvisUtil.stringElvis(properties.getSwaggerUi().getBaseUrl(),""),'/')
                + pattern;
    }

    /**
     * 文档基本信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("swagger口文档")
                .description("swagger接口文档")
                .version("2.0")
                .build();
    }

    /**
     * 全局通用属性（摘要）配置
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo()) //应用文档基本信息
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.example.management.system.controller")) // swagger扫描路径
                .paths(PathSelectors.any()) // 应用于包下所有路径
                .build()
//                .ignoredParameterTypes(User.class, AdminUser.class) // 忽略此类型输入参数（viewResovler全局添加的）
                .globalRequestParameters(getGlobalRequestParameters()); // 设置全局通用参数
    }

    /**
     * 项目通用参数，添加全局参数——登录认证token（若无可省略）
     */
    private List<RequestParameter> getGlobalRequestParameters() {
        List<RequestParameter> parameters = new ArrayList<>();
        parameters.add(new RequestParameterBuilder()
                .name("token") // 参数名
                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING))) //参数类型
                .description("登录认证token") // 描述
                .required(false) // 非必传
                .in(ParameterType.HEADER) //请求头中的参数，其它类型可以点进ParameterType类中查看
                .build());
        return parameters;
    }
}
