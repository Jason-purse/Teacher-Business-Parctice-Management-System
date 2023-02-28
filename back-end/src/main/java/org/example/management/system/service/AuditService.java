package org.example.management.system.service;

import com.jianyue.lightning.boot.starter.util.BeanUtils;
import com.jianyue.lightning.boot.starter.util.ElvisUtil;
import com.jianyue.lightning.boot.starter.util.OptionalFlux;
import com.jianyue.lightning.boot.starter.util.lambda.LambdaUtils;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.constant.DictConstant;
import org.example.management.system.model.entity.*;
import org.example.management.system.model.param.AuditParam;
import org.example.management.system.model.vo.ReportVo;
import org.example.management.system.repository.ProjectRepository;
import org.example.management.system.repository.ReportRRAuditPhaseRepository;
import org.example.management.system.repository.ReportRepository;
import org.example.management.system.utils.DateTimeUtils;
import org.example.management.system.utils.EscapeUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.example.management.system.model.constant.DictConstant.*;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.startsWith;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final ReportRepository reportRepository;

    private final DictService dictService;

    private final ProjectRepository projectRepository;

    private final AttachmentService attachmentService;

    private final ReportRRAuditPhaseService reportRRAuditPhaseService;

    /**
     * 提交报告之后,自动变更为待审核 ..
     *
     * @param auditParam 审核参数 ..
     */
    public void updateAudit(AuditParam auditParam) {
        Optional<Report> reportContainer = reportRepository.findById(auditParam.getReportId());
        Assert.isTrue(reportContainer.isPresent(), "当前报告不存在 !!!");

        Dict statusItem = dictService.getDictByItemType("audit_success");
        Report report = reportContainer.get();
        Assert.isTrue(!statusItem.getId().equals(report.getStatus()), "当前报告已经审核完成,不能重复审核 !!!");
        Dict firstAuditStatus = dictService.getFirstAuditStatus();
        Assert.isTrue(!report.getStatus().equals(firstAuditStatus.getId())
                        && report.getAuditUserId() != null && report.getAuditUserId().equals(auditParam.getAuditUserId())

                , "无法审核此报告,此报告状态为" + firstAuditStatus.getItemValue());
        report.setFailureReason(ElvisUtil.stringElvis(auditParam.getFailureReason(), ""));
        // 设置失败标识
        report.setFailureFlag(auditParam.getFailureFlag());
        // 失败之后,可以修改 !!!
        if (auditParam.getFailureFlag()) {
            report.setCanModify(true);
            // 审核失败 !!
            Dict item = dictService.getDictByItemType(AUDIT_FINALLY_PHASE);
            Assert.isTrue(!item.getId().equals(report.getStatus()), "此报告已经被审核过,不能重复审核 !!!");
            // 审核失败 !!!
            report.setStatus(item.getId());
            report.setFailureReason(auditParam.getFailureReason());
            // 改变阶段到前一个 ..
            Dict dict = dictService.getDictItemById(report.getAuditPhase());
            if (dict.getPreviousDataTypeID() != null) {
                // 如果为第一个,则设置为false ..
                if(dict.getPreviousDataTypeID().equals(FIRST_ITEM_IDENTIFY)) {
                    report.setAuditPhase(null);
                }
                else {
                    Dict previousDict = dictService.getDictItemById(dict.getPreviousDataTypeID());
                    // 设置为前一个阶段 ..
                    report.setAuditPhase(previousDict.getId());
                }
            }
        } else {
            // 表示成功 !!! 进入下一个阶段
            Dict item = dictService.getDictItemById(report.getAuditPhase());
            report.setSuccessDescription(auditParam.getSuccessDescription());
            boolean nextPhase = false;
            if (item.getSupportFlow() != null && item.getSupportFlow()) {
                if (item.getNextDataTypeID() != null) {
                    // 表示还有下一个阶段
                    report.setAuditPhase(item.getNextDataTypeID());
                    Optional<ReportRRAuditPhase> rrData = reportRRAuditPhaseService.getReportRRAuditPhaseByReportId(item.getId(), item.getNextDataTypeID());
                   if(rrData.isPresent()) {
                       ReportRRAuditPhase reportRRAuditPhase = rrData.get();
                       report.setAuditUserId(reportRRAuditPhase.getUserId());
                       report.setAuditUserName(reportRRAuditPhase.getUserName());
                   }
                    // 从指派中选择
                    report.setStatus(firstAuditStatus.getId());
                    // 状态不需要设置,然后直接进入下一个阶段的进行中
                    nextPhase = true;
                }
            }

            // 表示完成 ..
            if (!nextPhase) {
                report.setStatus(statusItem.getId());
                // 置为空,表示已经完成 ...
                // 停留在 最后阶段
//                report.setAuditPhase(null);
                // 完成 ...
                report.setFinished(true);
                // 可以被删除 !!!
                report.setCanModify(true);

                // 判断是否为最后一个报告 ...
                Dict dict = dictService.getDictItemById(report.getReportType());
                if (dict.getNextDataTypeID() == null) {
                    // 表示最后一个报告 ...
                    Optional<Project> project = projectRepository.findById(report.getProjectId());
                    project.ifPresent(ele -> {
                        Dict running = dictService.getDictItemById(ele.getStatus());
                        Dict finish = dictService.getDictItemById(running.getNextDataTypeID());
                        ele.setStatus(finish.getId());
                        // 完成
                        ele.setFinished(true);
                    });
                }
            }
        }

        LocalDateTime now = LocalDateTime.now();
        report.setUpdateAt(now.toInstant(ZoneOffset.of("+8")).toEpochMilli());
        report.setUpdateTimeStr(DateTimeUtils.getTimeStr(now));

        // 更新
        reportRepository.save(report);
    }

    /**
     * 这个只会指派一次 ..
     */
    public void createAudit(AuditParam param) {
        Optional<Report> report = reportRepository.findById(param.getReportId());
        Assert.isTrue(report.isPresent(), "当前指派审核报告不存在,请检查 !!!");
        Dict firstAuditStatus = dictService.getFirstAuditStatus();
        report.ifPresent(ele -> {
            Assert.isTrue(ele.getAssignFlag() == null || !ele.getAssignFlag(), "无法指派审核报告,此报告已经完成指派 !!!");
            ele.setAuditUserId(param.getAuditUserId());
            ele.setAuditUserName(param.getAuditUserName());
            Integer nextDataTypeID = firstAuditStatus.getNextDataTypeID();
            // 审核状态 ..
            ele.setStatus(nextDataTypeID);
            // pending 状态
            ele.setFailureFlag(null);

            LocalDateTime now = LocalDateTime.now();
            List<ReportRRAuditPhase> reportRRAuditPhases = param.getAuditHandlers().stream().map(rr -> {
                        return ReportRRAuditPhase.builder()
                                .reportId(param.getReportId())
                                .auditPhaseId(rr.getAuditPhaseId())
                                .userName(rr.getUsername())
                                .userId(rr.getUserId())
                                .createAt(DateTimeUtils.getTimeOfDay(now))
                                .createTimeStr(DateTimeUtils.getTimeStr(now))
                                .build();
                    })
                    .toList();

            // 设置
            ele.setAuditUserName(reportRRAuditPhases.get(0).getUserName());
            ele.setAuditPhase(reportRRAuditPhases.get(0).getAuditPhaseId());
            // 然后开始 配置关系
            reportRRAuditPhaseService.createReportAndAuditPhaseRRInfo(reportRRAuditPhases);
            // 不能修改 !!!!
            ele.setCanModify(false);
            reportRepository.save(ele);
        });
    }

    public Page<ReportVo> getAllReportsForAuditByPage(AuditParam param, Pageable pageable) {
        AuditComplexSpecification auditComplexSpecification = new AuditComplexSpecification();
        auditComplexSpecification.setAuditParam(param);
        AtomicReference<Page<Project>> all = new AtomicReference<>();
        OptionalFlux
                .of(ElvisUtil.stringElvis(param.getProjectName(), null))
                .consume(projectName -> {
                    // 尝试分页, 因为多个报告对应一个项目 ...
                    all.set(projectRepository.findAll(
                            Example.of(
                            Project.builder()
                                    .name(projectName)
                                    .build()
                            , ExampleMatcher.matching().withMatcher(LambdaUtils.getPropertyNameForLambda(Project::getName), startsWith()))
                            ,pageable)
                    );
                    if (all.get().hasContent()) {
                        auditComplexSpecification.setProjectIds(all.get().getContent().stream().map(Project::getId).toList());
                    }
                });


        if (all.get() != null &&  !all.get().hasContent()) {
            // 不用查了,直接返回空 ..
            // 因为连项目都没有,肯定没有报告 ... 直接返回 empty ..
            return Page.empty();
        }

        Page<Report> page = reportRepository.findAll(auditComplexSpecification, pageable);

        if (page.getContent().size() > 0) {
            List<Project> projects = null;
            if(all.get() != null) {
                projects = all.get().getContent();
            }
            else {
                // 需要查询
                List<Integer> projectIds = page.getContent().stream().map(Report::getProjectId).distinct().toList();
                // 查询完毕 ..
                projects = projectRepository.findAllById(projectIds);
            }
            Map<Integer, String> map = projects.stream().collect(Collectors.toMap(Project::getId, Project::getName));
            List<Integer> list = page.getContent().stream().map(Report::getReportUrlId).distinct().toList();
            List<Attachment> attachments = attachmentService.getAllAttachmentInfosByIds(list);
            Map<Integer, Attachment> collect = attachments.stream().collect(Collectors.toMap(Attachment::getId, Function.identity()));
            // 设置文件格式 ...
            List<Dict> dict = dictService.getDictItemsBy(DictConstant.REPORT_FORMAT);
            Map<String, Integer> mediaTypeMaps = dict.stream().collect(Collectors.toMap(Dict::getItemType, Dict::getId));
            List<ReportVo> reports
                    = page.getContent()
                    .stream()
                    .map(BeanUtils.transformFrom(ReportVo.class))
                    .peek(ele -> {
                        String projectName = map.get(ele.getProjectId());
                        ele.setProjectName(projectName);

                        // 设置 媒体类型
                        if(attachments.size() > 0) {
                            Attachment attachment = collect.get(ele.getReportUrlId());
                            if(attachment != null) {
                                ele.setReportUrlStr(attachment.getFileUrl());
                                ele.setMediaType(attachment.getMediaType());
                                Dict mediaType = dictService.getDictItemById(attachment.getMediaType());
                                if(mediaType.getItemType().contains("pdf")) {
                                    Integer value = mediaTypeMaps.get(DictConstant.PDF_REPORT_FORMAT);
                                    Assert.notNull(value,"字典数据缺失,无法找到报告格式数据项 !!!");
                                    // 设置报告格式 ...
                                    ele.setReportFormat(value);
                                }
                                else {
                                    // word
                                    Integer value = mediaTypeMaps.get(DictConstant.DOCX_REPORT_FORMAT);
                                    Assert.notNull(value,"字典数据缺失,无法找到报告格式数据项 !!!");
                                    // 设置报告格式 ...
                                    ele.setReportFormat(value);
                                }
                            }
                        }
                    }).toList();
            return new PageImpl<>(reports, pageable, page.getTotalElements());
        }
        return new PageImpl<>(Collections.emptyList(),page.getPageable(),page.getTotalElements());
    }

    class AuditComplexSpecification implements Specification<Report> {
        private AuditParam auditParam;

        private List<Integer> projectIds;

        public void setAuditParam(AuditParam auditParam) {
            this.auditParam = auditParam;
        }

        public void setProjectIds(List<Integer> projectIds) {
            this.projectIds = projectIds;
        }

        @Override
        public Predicate toPredicate(@NotNull Root<Report> root, @NotNull CriteriaQuery<?> query, @NotNull CriteriaBuilder criteriaBuilder) {
            return OptionalFlux.of(
                            criteriaBuilder.equal(root.get(LambdaUtils.getPropertyNameForLambda(Report::getStatus)),
                                    dictService.getDictByItemType(AUDITING).getId())
                    )
                    .combine(
                            OptionalFlux
                                    .of(auditParam.getAuditUserId())
                                    .map(userId -> criteriaBuilder.equal(root.get(LambdaUtils.getPropertyNameForLambda(Report::getAuditUserId)), userId))
                                    .combine(
                                            OptionalFlux.of(projectIds)
                                                    .map(ele -> root.get(LambdaUtils.getPropertyNameForLambda(Report::getProjectId))
                                                            .in(projectIds))
                                                    .combine(
                                                            OptionalFlux
                                                                    .of(ElvisUtil.stringElvis(auditParam.getReportName(), null))
                                                                    .map(ele -> criteriaBuilder.like(root.get(LambdaUtils.getPropertyNameForLambda(Report::getReportName)), EscapeUtil.escapeExprSpecialWord(ele.trim()).concat("%")))
                                                            ,
                                                            criteriaBuilder::and
                                                    )
                                                    .combine(
                                                            OptionalFlux
                                                                    .of(ElvisUtil.stringElvis(auditParam.getSubmitUserName(), null))
                                                                    .map(ele -> criteriaBuilder.like(root.get(LambdaUtils.getPropertyNameForLambda(Report::getSubmitUserName)), EscapeUtil.escapeExprSpecialWord(ele.trim()).concat("%")))
                                                            ,
                                                            criteriaBuilder::and
                                                    )
                                                    .combine(
                                                            OptionalFlux.of(
                                                                    auditParam.getAuditPhase()
                                                            ).map(ele -> criteriaBuilder.equal(root.get(LambdaUtils.getPropertyNameForLambda(Report::getAuditPhase)), ele))
                                                            ,
                                                            criteriaBuilder::and
                                                    )
                                                    .combine(
                                                            OptionalFlux
                                                                    .of(auditParam.getStartTimeAt())
                                                                    .<Predicate>map(startTime -> {
                                                                        Path<Long> objectPath = root.get(LambdaUtils.getPropertyNameForLambda(Report::getCreateAt));
                                                                        return OptionalFlux
                                                                                .of(auditParam.getEndTimeAt())
                                                                                .map(endTime -> criteriaBuilder.between(
                                                                                        objectPath,
                                                                                        startTime, endTime)
                                                                                )
                                                                                .orElse(criteriaBuilder.greaterThanOrEqualTo(objectPath, startTime))
                                                                                .<Predicate>getResult();
                                                                    })
                                                                    .orElse(
                                                                            OptionalFlux.of(auditParam.getEndTimeAt())
                                                                                    .<Predicate>map(endTime -> criteriaBuilder.lessThanOrEqualTo(root.get(LambdaUtils.getPropertyNameForLambda(Report::getCreateAt)), endTime))
                                                                    ),
                                                            criteriaBuilder::and
                                                    ),
                                            criteriaBuilder::and
                                    ),
                            criteriaBuilder::and
                    )
                    .getResult();
        }
    }
}
