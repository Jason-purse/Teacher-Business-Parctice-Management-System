package org.example.management.system.service;

import com.jianyue.lightning.boot.starter.util.ElvisUtil;
import com.jianyue.lightning.boot.starter.util.OptionalFlux;
import com.jianyue.lightning.boot.starter.util.lambda.LambdaUtils;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.constant.DictConstant;
import org.example.management.system.model.entity.Dict;
import org.example.management.system.model.entity.Project;
import org.example.management.system.model.entity.Report;
import org.example.management.system.model.param.AuditParam;
import org.example.management.system.repository.ProjectRepository;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.example.management.system.model.constant.DictConstant.AUDIT_FINALLY_PHASE;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.startsWith;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final ReportRepository reportRepository;

    private final DictService dictService;

    private final ProjectRepository projectRepository;

    /**
     * 提交报告之后,自动变更为待审核 ..
     *
     * @param auditParam 审核参数 ..
     */
    public void updateAudit(AuditParam auditParam) {
        Optional<Report> reportContainer = reportRepository.findById(auditParam.getReportId());
        Assert.isTrue(reportContainer.isPresent(), "当前报告不存在 !!!");
        Report report = reportContainer.get();
        report.setFailureReason(ElvisUtil.stringElvis(auditParam.getFailureReason(), ""));
        // 失败之后,可以修改 !!!
        if (auditParam.getFailureFlag()) {
            report.setCanModify(true);
            // 审核失败 !!
            Dict item = dictService.getDictByItemType(AUDIT_FINALLY_PHASE);
            // 审核失败 !!!
            report.setStatus(item.getId());
        } else {
            // 表示成功 !!! 进入下一个阶段
            Dict item = dictService.getDictItemById(report.getAuditPhase());
            boolean nextPhase = false;
            if (item.getSupportFlow() != null && item.getSupportFlow()) {
                if (item.getNextDataTypeID() != null) {
                    // 表示还有下一个阶段
                    report.setAuditPhase(item.getNextDataTypeID());
                    // 状态不需要设置,然后直接进入下一个阶段的进行中
                    nextPhase = true;
                }
            }

            // 表示完成 ..
            if (!nextPhase) {
                Dict statusItem = dictService.getDictByItemType("audit_success");
                report.setStatus(statusItem.getId());
                // 完成 ...
                report.setFinished(true);
            }
        }

        LocalDateTime now = LocalDateTime.now();
        report.setUpdateAt(now.toInstant(ZoneOffset.of("+8")).toEpochMilli());
        report.setUpdateTimeStr(DateTimeUtils.getTimeStr(now));

        // 更新
        reportRepository.save(report);
    }

    public void createAudit(AuditParam param) {
        Optional<Report> report = reportRepository.findById(param.getReportId());
        Assert.isTrue(report.isPresent(), "当前指派审核报告不存在,请检查 !!!");
        Dict firstAuditStatus = dictService.getFirstAuditStatus();
        report.ifPresent(ele -> {
            Assert.isTrue(firstAuditStatus.getId().equals(ele.getStatus()), "无法指派审核报告,此报告正在审核中 !!!");
            ele.setAuditUserId(param.getAuditUserId());
            ele.setAuditUserName(param.getAuditUserName());
            Integer nextDataTypeID = firstAuditStatus.getNextDataTypeID();
            // 进入下一个阶段 ..
            ele.setStatus(nextDataTypeID);
            // 进入审核阶段的第一个阶段
            ele.setAuditPhase(dictService.getFirstDataItemInFlow(DictConstant.AUDIT_PHASE).getId());
            // 不能修改 !!!!
            ele.setCanModify(false);
            reportRepository.save(ele);
        });
    }

    public Page<Report> getAllReportsForAuditByPage(AuditParam param, Pageable pageable) {
        AuditComplexSpecification auditComplexSpecification = new AuditComplexSpecification();
        auditComplexSpecification.setAuditParam(param);

        AtomicReference<List<Project>> all = new AtomicReference<>();
        OptionalFlux
                .of(ElvisUtil.stringElvis(param.getProjectName(), null))
                .consume(projectName -> {
                    all.set(projectRepository.findAll(Example.of(
                            Project.builder()
                                    .name(projectName)
                                    .build()
                            , ExampleMatcher.matching().withMatcher(LambdaUtils.getPropertyNameForLambda(Project::getName), startsWith()))));
                    if (all.get().size() > 0) {
                        auditComplexSpecification.setProjectIds(all.get().stream().map(Project::getId).toList());
                    }
                });


        Page<Report> page = reportRepository.findAll(auditComplexSpecification, pageable);
        if (all.get() == null) {
            if (page.getContent().size() > 0) {
                List<Integer> list = page.getContent().stream().map(Report::getProjectId).distinct().toList();
                all.set(projectRepository.findAllById(list));
            }
        }

        if (page.getContent().size() > 0) {
            List<Project> projects = all.get();
            Map<Integer, String> map = projects.stream().collect(Collectors.toMap(Project::getId, Project::getName));
            List<Report> reports
                    = page.getContent()
                    .stream()
                    .peek(ele -> {
                        String projectName = map.get(ele.getProjectId());
                        ele.setProjectName(projectName);
                    }).toList();
            return new PageImpl<>(reports, pageable, page.getTotalElements());
        }
        return page;
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
            return OptionalFlux
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
                                                    .map(startTime -> {
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
                                                                    .map(endTime -> criteriaBuilder.lessThanOrEqualTo(root.get(LambdaUtils.getPropertyNameForLambda(Report::getCreateAt)), endTime))
                                                    ),
                                            criteriaBuilder::and
                                    ),
                            criteriaBuilder::and
                    )
                    .getResult();
        }
    }
}
