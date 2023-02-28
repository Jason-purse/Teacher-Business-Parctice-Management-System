package org.example.management.system.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.Paths;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@ConfigurationProperties(prefix = "bp.management.config")
public class SystemConfigProperties {

    /**
     * 可以设置相对路径,或者绝对路径,根据操作系统文件系统路径进行设置
     */
    private String fileStoreDir = System.getProperty("user.home") + "/teacher-bp-management-system";

    @Data
    public static class Admin {
        private String password = "123456";

        public String getPassword() {
            if (StringUtils.hasText(password)) {
                return "{noop}" + password;
            }
            return "{noop}123456";
        }
    }

    private Admin admin = new Admin();


    public String getFileStoreDir() {
        return resolveDir(fileStoreDir);
    }

    private String resolveDir(String fileStoreDir) {
        String separator = File.separator;
        fileStoreDir = fileStoreDir.replaceAll("//", separator);
        File file = Paths.get(fileStoreDir).toFile();
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                boolean mkdirs = parentFile.mkdirs();
                if (!mkdirs) {
                    throw new IllegalArgumentException("当前程序没有权限创建目录, 当前目录路径为" + parentFile.getPath() + "。请给定合适的目录权限!!!");
                }
            }

            boolean newFile = file.mkdir();
            if (!newFile) {
                log.warn("文件创建失败,它可能被其他应用程序或者用户创建,将使用这个文件目录存储项目文件!!!");
            }
        }

        if (!file.isDirectory()) {
            throw new IllegalArgumentException("当前给定的文件不是一个目录,请替换为使用文件目录 !!!");
        }

        try {
            return file.toURI().toURL().toExternalForm();
        } catch (Exception e) {
            throw new IllegalArgumentException("此文件目录不是一个有效的url路径 !!!");
        }
    }
}
