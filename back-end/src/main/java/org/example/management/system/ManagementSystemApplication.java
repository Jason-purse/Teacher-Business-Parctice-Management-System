package org.example.management.system;


import com.generatera.oauth2.resource.server.config.token.LightningOAuth2UserPrincipal;
import com.generatera.security.authorization.server.specification.LightningUserPrincipal;
import com.generatera.security.authorization.server.specification.components.token.JwtClaimsToUserPrincipalMapper;
import com.generatera.security.authorization.server.specification.components.token.format.jwt.JwtEncodingContext;
import com.generatera.security.authorization.server.specification.components.token.format.jwt.customizer.LightningJwtCustomizer;
import com.jianyue.lightning.util.JsonUtil;
import org.example.management.system.config.SystemConfigProperties;
import org.example.management.system.model.entity.User;
import org.example.management.system.model.security.SimpleUserPrincipal;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@EnableJpaRepositories
@EnableConfigurationProperties(SystemConfigProperties.class)
@SpringBootApplication
@ServletComponentScan
public class ManagementSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManagementSystemApplication.class,args);
    }


    /**
     * 测试期间,修改为针对
     * @return
     */
    @Bean
    public JwtClaimsToUserPrincipalMapper mapper() {
        return new JwtClaimsToUserPrincipalMapper() {
            @Override
            public LightningUserPrincipal convert(Map<String, Object> map) {
                Map principal = JsonUtil.getDefaultJsonUtil().fromJson(map.get("principal").toString(), Map.class);
                Object user = principal.get("user");
                User convert = JsonUtil.getDefaultJsonUtil().convertTo(user, User.class);
                return SimpleUserPrincipal.authenticated(
                        convert,
                        ((Collection<GrantedAuthority>) principal.get("principal"))
                );
            }
        };
    }

    @Bean
    public LightningJwtCustomizer jwtCustomizer() {
        return new LightningJwtCustomizer() {
            @Override
            public void customizeToken(JwtEncodingContext jwtEncodingContext) {
                LightningUserPrincipal principal = jwtEncodingContext.getPrincipal();
                Map<String, Object> claims = jwtEncodingContext.getClaims().getClaims();
                claims.put("principal",JsonUtil.getDefaultJsonUtil().asJSON(principal));
            }
        };
    }
}
