package org.example.management.system.service;

import com.generatera.security.authorization.server.specification.components.token.format.plain.UuidUtil;
import com.jianyue.lightning.boot.starter.util.BeanUtils;
import com.jianyue.lightning.boot.starter.util.ElvisUtil;
import com.jianyue.lightning.boot.starter.util.OptionalFlux;
import com.jianyue.lightning.boot.starter.util.lambda.LambdaUtils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.constant.DictConstant;
import org.example.management.system.model.entity.Attachment;
import org.example.management.system.model.entity.Dict;
import org.example.management.system.model.param.AttachmentParam;
import org.example.management.system.model.vo.AttachmentVo;
import org.example.management.system.repository.AttachmentRepository;
import org.example.management.system.utils.DateTimeUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 资源服务
 */
@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final FileService fileService;

    private final AttachmentRepository attachmentRepository;

    private final DictService dictService;

    public AttachmentVo uploadAttachment(MultipartFile file) {
        List<Dict> items = dictService.getDictItemsBy(DictConstant.MEDIA_FORMAT);
        Assert.isTrue(items.size() > 0,"当前系统不支持文件上传 !!!");
        String contentType = file.getContentType();
        contentType = ElvisUtil.stringElvis(contentType,ElvisUtil.stringElvis(file.getOriginalFilename(),file.getName()).substring(file.getOriginalFilename().lastIndexOf(".") + 1));
        String finalContentType = contentType;
        Optional<Dict> dict = items.stream().filter(ele -> ele.getItemType().equals(finalContentType.trim())).findFirst();
        Assert.isTrue(dict.isPresent(),"当前系统不支持[" + file.getContentType() + "]类型文件上传,请检查 !!!");
        try {
            LocalDateTime now = LocalDateTime.now();
            Attachment attachment = Attachment
                    .builder()
                    .fileName(file.getOriginalFilename())
                    .identifier(UuidUtil.nextId())
                    .mediaType(dict.get().getId())
                    .fileUrl(
                            fileService.saveFile(file.getInputStream(),
                                    "attachment", getFileName(file.getOriginalFilename()))
                    )
                    .createAt(DateTimeUtils.getTimeOfDay(now))
                    .createTimeStr(DateTimeUtils.getTimeStr(now))
                    .build();

            AttachmentVo attachmentVo = BeanUtils.transformFrom(attachment, AttachmentVo.class);

            // 直接保存 !!!
            attachmentRepository.save(attachment);
            assert  attachmentVo != null;
            return attachmentVo;
        }catch (Exception e) {
            throw new IllegalStateException("无法保存此附件,原因是无系统权限或者系统目录配置不正确或者其他原因 !!!");
        }
    }

    private String getFileName(String fileName) {
        String id = UuidUtil.nextId();
        long time = new Date().getTime();
        return id + "-" + time + "-" + fileName;
    }

    public void deleteAttachment(Integer id) {
        attachmentRepository.findById(id).ifPresent(file -> {
            try {
                fileService.deleteFile("attachment",file.getFileUrl());
            }catch (Exception e){
                // pass
            } finally{
                attachmentRepository.deleteById(id);
            }

        });
    }

    public Page<AttachmentVo> getAllAttachmentInfoByPage(AttachmentParam attachmentParam, Pageable pageable) {
        ComplexAttachmentSpecification specification = new ComplexAttachmentSpecification(attachmentParam.getFileName(), attachmentParam.getMediaType(), attachmentParam.getStartTimeAt(),
                attachmentParam.getEndTimeAt());

        Page<Attachment> all = attachmentRepository.findAll(specification, pageable);
        if(all.hasContent()) {
            return new PageImpl<>(all.getContent().stream().map(BeanUtils.transformFrom(AttachmentVo.class)).toList(),all.getPageable(),all.getTotalElements());
        }
        return new PageImpl<>(Collections.emptyList(),pageable,all.getTotalElements());
    }

    @AllArgsConstructor
    class ComplexAttachmentSpecification implements Specification<Attachment> {

        private String fileName;

        private Integer mediaType;

        private Long startTimeAt;

        private Long endTimeAt;


        @Override
        public Predicate toPredicate(@NotNull Root<Attachment> root, @NotNull CriteriaQuery<?> query, @NotNull CriteriaBuilder criteriaBuilder) {

            return OptionalFlux
                    .of(mediaType)
                    .map(type -> criteriaBuilder.equal(root.get(LambdaUtils.getPropertyNameForLambda(Attachment::getMediaType)),type))
                    .combine(
                            OptionalFlux.of(ElvisUtil.stringElvis(fileName,null))
                                    .map(name -> criteriaBuilder.like(root.get(LambdaUtils.getPropertyNameForLambda(Attachment::getFileName)),name))
                            ,
                            criteriaBuilder::and
                    )
                    .combine(
                            OptionalFlux.of(startTimeAt)
                                    .map(startTime -> {
                                        Path<Long> path = root.get(LambdaUtils.getPropertyNameForLambda(Attachment::getCreateAt));
                                        return OptionalFlux.of(endTimeAt)
                                                .map(endTime -> criteriaBuilder.between(path,startTime,endTime))
                                                .orElse(criteriaBuilder.greaterThanOrEqualTo(path,startTime))
                                                .<Predicate>getResult();
                                    })
                                    .orElse(
                                            OptionalFlux.of(endTimeAt)
                                                    .map(endTime -> criteriaBuilder.lessThanOrEqualTo(root.get(LambdaUtils.getPropertyNameForLambda(Attachment::getCreateAt)),endTime))
                                                    .<Predicate>getResult()
                                    )
                            ,
                            criteriaBuilder::and
                    )
                    .getResult();

        }
    }
}
