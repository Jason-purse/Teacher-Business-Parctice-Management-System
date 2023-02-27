package org.example.management.system.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.param.AttachmentParam;
import org.example.management.system.model.vo.AttachmentVo;
import org.example.management.system.service.AttachmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 附件信息
 */
@RestController
@RequestMapping("/api/admin/attachment/v1")
@RequiredArgsConstructor
@Api
public class AttachmentController {

    private final AttachmentService attachmentService;
    /**
     * 文件处理
     * @param file 文件内容
     * @return 上传结果
     */
    @PostMapping("upload")
    public AttachmentVo uploadAttachment(MultipartFile file) {
        Assert.notNull(file,"form parameter name  of file must be file!!!");
        return attachmentService.uploadAttachment(file);
    }

    /**
     * 删除附件 ...
     * @param fileUrl 文件 url
     */
    @DeleteMapping("{id}")
    public void deleteAttachment(Integer id) {
        attachmentService.deleteAttachment(id);
    }


    /**
     * 查看 所有存在的附件信息 ...
     * @param attachmentParam 附件查询参数
     * @param pageable 分页参数 ...
     * @return
     */
    @GetMapping("list")
    public Page<AttachmentVo> getAllAttachmentInfoByPage(AttachmentParam attachmentParam, Pageable pageable) {
        return attachmentService.getAllAttachmentInfoByPage(attachmentParam,pageable);
    }
}
