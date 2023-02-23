package org.example.management.system.controller;

import lombok.RequiredArgsConstructor;
import org.example.management.system.model.vo.AttachmentVo;
import org.example.management.system.service.AttachmentService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 附件信息
 */
@RestController
@RequestMapping("/api/admin/attachment/v1")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;
    /**
     * 文件处理
     * @param file 文件内容
     * @return
     */
    @PostMapping("upload")
    public AttachmentVo uploadAttachment(MultipartFile file) {
        return attachmentService.updateAttachment(file);
    }


    @DeleteMapping
    public void deleteAttachment(String fileUrl) {
        attachmentService.deleteAttachment(fileUrl);
    }


}
