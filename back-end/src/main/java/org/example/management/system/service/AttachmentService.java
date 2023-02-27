package org.example.management.system.service;

import com.generatera.security.authorization.server.specification.components.token.format.plain.UuidUtil;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.vo.AttachmentVo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * 资源服务
 */
@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final FileService fileService;

    public AttachmentVo updateAttachment(MultipartFile file) {
        try {
            return AttachmentVo
                    .builder()
                    .fileUrl(
                            fileService.saveFile(file.getInputStream(),
                            "attachment",getFileName(file.getOriginalFilename()))
                    )
                    .build();
        }catch (Exception e) {
            throw new IllegalStateException("无法保存此附件,原因是无系统权限或者系统目录配置不正确或者其他原因 !!!");
        }
    }

    private String getFileName(String fileName) {
        String id = UuidUtil.nextId();
        long time = new Date().getTime();
        return id + "-" + time + "-" + fileName;
    }

    public void deleteAttachment(String fileUrl) {
        fileService.deleteFile("attachment",fileUrl);
    }
}
