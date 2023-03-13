package org.example.management.system.config;

import com.jianyue.lightning.boot.starter.util.ElvisUtil;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.constant.DictConstant;
import org.example.management.system.model.entity.Dict;
import org.example.management.system.model.entity.User;
import org.example.management.system.model.param.UserWithRoleParam;
import org.example.management.system.repository.RoleRRURepository;
import org.example.management.system.repository.UserRepository;
import org.example.management.system.service.DictService;
import org.example.management.system.service.RoleService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Example;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class ManagementConfiguration implements WebMvcConfigurer, ApplicationListener<ContextRefreshedEvent> {

    private final SystemConfigProperties properties;

    private final UserRepository userRepository;

    private final RoleService roleService;

    private final DictService dictService;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            registry.addResourceHandler("/attachment/**")
                    .addResourceLocations(new UrlResource(Paths.get(URI.create(properties.getFileStoreDir()).resolve("attachment")).toUri()));
            registry.addResourceHandler("/admin/**")
                    .addResourceLocations("classpath:/admin/");
            registry.addResourceHandler("/static/**")
                    .addResourceLocations("classpath:/admin/static/");
            registry.addResourceHandler("/*.worker.js")
                    .addResourceLocations("classpath:/admin/");

        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("提供了一个无效的文件存储路径,无法进行资源映射放行 !!!");
        }
    }


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/")
                .setViewName("redirect:/admin/index.html");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/attachment/**")
                .allowedOrigins("*");
    }

    @Transactional
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Optional<User> admin = userRepository.findOne(
                Example.of(
                        User.builder()
                                .email("admin")
                                .build()
                )
        );
        List<Dict> items = dictService.getDictItemsBy(DictConstant.ROOT);
        if(items == null || items.size() <= 0) {
            dictService.initAllDicts();
        }
        if (admin.isEmpty()) {
            userRepository.save(
                    User.builder()
                            .email("admin")
                            .username("admin")
                            .password(properties.getAdmin().getPassword())
                            .build()
            );
            Optional<User> user = userRepository.findOne(Example.of(User.builder().email("admin").build()));
            List<Dict> dictItemsBy = dictService.getDictItemsBy(DictConstant.ROLE);
            user.ifPresent(ele -> {
                roleService.updateUserWithRole(new UserWithRoleParam(
                        ele.getId(),
                        dictItemsBy.stream().map(Dict::getId).toList()
                ));
            });
        }



    }
}
