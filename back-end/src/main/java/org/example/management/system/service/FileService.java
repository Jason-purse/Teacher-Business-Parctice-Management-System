package org.example.management.system.service;

import lombok.RequiredArgsConstructor;
import org.example.management.system.config.SystemConfigProperties;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 文件服务
 */
@Service
public class FileService {

    private final Path fileStoreDir;

    private final String canonicalFileStoreDirPath;

    public FileService(SystemConfigProperties properties) {
        this.fileStoreDir = Paths.get(properties.getFileStoreDir());
        this.canonicalFileStoreDirPath = fileStoreDir.toFile().getCanonicalPath();
    }

    public String saveFile(InputStream inputStream,String directory,String filename) {
        Path resolve = fileStoreDir.resolve(directory.trim()).resolve(filename.trim());
        try {
            Files.copy(inputStream,resolve,
                    StandardCopyOption.COPY_ATTRIBUTES,StandardCopyOption.REPLACE_EXISTING);
            String canonicalPath = resolve.toFile().getCanonicalPath();
            return canonicalPath.substring(canonicalFileStoreDirPath.length());
        }catch (Exception e) {
            throw new IllegalArgumentException("文件保存失败,错误原因: " + e.getMessage());
        }
    }

    public void deleteFile(String directory,String filename) {
        try {
            Files.deleteIfExists(fileStoreDir.resolve(directory).resolve(filename));
        }catch (Exception e) {
            throw new IllegalArgumentException("文件删除失败,错误原因: " + e.getMessage());
        }
    }
}
