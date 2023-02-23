package org.example.management.system.service;

import com.generatera.security.authorization.server.specification.LightningUserContext;
import com.jianyue.lightning.boot.starter.util.ElvisUtil;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.entity.Dict;
import org.example.management.system.model.entity.Report;
import org.example.management.system.model.param.AuditParam;
import org.example.management.system.model.security.SimpleUserPrincipal;
import org.example.management.system.repository.ReportRepository;
import org.example.management.system.utils.DateTimeUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final ReportRepository reportRepository;

    private final DictService dictService;

    /**
     * 提交报告之后,自动变更为审核中 ..
     * @param auditParam 审核参数 ..
     */
    public void updateAudit(AuditParam auditParam) {
        Optional<Report> reportContainer = reportRepository.findById(auditParam.getReportId());
        Assert.isTrue(reportContainer.isPresent(),"当前报告不存在 !!!");
        Report report = reportContainer.get();
        report.setFailureReason(ElvisUtil.stringElvis(auditParam.getFailureReason(),""));
        LightningUserContext
                .get()
                .getUserPrincipal(SimpleUserPrincipal.class)
                .ifPresent(
                        user -> {
                            report.setAuditUserId(user.getUser().getId());
                            report.setAuditUserName(user.getUsername());
                        }
                );

        // 失败之后,可以修改 !!!
        if(auditParam.getFailureFlag()) {
            report.setCanModify(true);
            // 审核失败 !!
            Dict item = dictService.getDictByItemType("audit_failure");
            // 审核失败 !!!
            report.setStatus(item.getId());
        }
        else {
            // 表示成功 !!! 进入下一个阶段
            Dict item = dictService.getDictItemById(report.getAuditPhase());
            boolean nextPhase = false;
            if(item.getSupportFlow() != null && item.getSupportFlow()) {
                if(item.getNextDataType() != null) {
                    // 表示还有下一个阶段
                    report.setAuditPhase(item.getNextDataType());
                    // 状态不需要设置,然后直接进入下一个阶段的进行中
                    nextPhase = true;
                }
            }

            // 表示完成 ..
            if(!nextPhase) {
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
}
