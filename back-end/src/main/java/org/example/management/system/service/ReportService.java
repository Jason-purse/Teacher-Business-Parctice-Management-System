package org.example.management.system.service;

import com.generatera.security.authorization.server.specification.LightningUserContext;
import com.jianyue.lightning.boot.starter.util.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.entity.Dict;
import org.example.management.system.model.entity.Report;
import org.example.management.system.model.param.ReportParam;
import org.example.management.system.model.security.SimpleUserPrincipal;
import org.example.management.system.repository.ReportRepository;
import org.example.management.system.utils.DateTimeUtils;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    private final DictService dictService;

    public void createReport(ReportParam reportParam) {
        Optional<Report> reportContainer = reportRepository.findOne(Example.of(
                Report
                        .builder()
                        .projectId(reportParam.getProjectId())
                        .reportType(reportParam.getReportType())
                        .build()
        ));

        Assert.isTrue(reportContainer.isEmpty(), "当前项目对应的" + reportParam.getReportType() + "的报告已经存在,请不要重复提交 !!!");

        // 判断它没有没有前置报告
        // 也就是前置报告的审核是否完成 ..
        Dict item = dictService.getDictItemById(reportParam.getReportType());

        // 表示 第一项(没事)
        if (item.getPreviousDataType() != null && item.getPreviousDataType() != -1) {
            Optional<Report> one = reportRepository.findOne(
                    Example.of(
                            Report
                                    .builder()
                                    .reportType(item.getPreviousDataType())
                                    .projectId(reportParam.getProjectId())
                                    .build()
                    )
            );

            // 判断结束,允许创建 ...
            Assert.isTrue(one.isPresent() && one.get().getFinished() != null && one.get().getFinished(), "无法创建报告类型为 " + item.getItemValue() + "的报告,因为前置报告不存在或者正在审核中 !!!");
        }

        Report value = BeanUtils.transformFrom(reportParam, Report.class);
        assert value != null;
        LocalDateTime now = LocalDateTime.now();
        value.setCreateAt(now.toInstant(ZoneOffset.of("+8")).toEpochMilli());
        value.setCreateTimeStr(DateTimeUtils.getTimeStr(now));
        value.setUpdateAt(value.getCreateAt());
        value.setUpdateTimeStr(value.getCreateTimeStr());

        LightningUserContext
                .get()
                .getUserPrincipal(SimpleUserPrincipal.class)
                .ifPresent(simpleUserPrincipal -> {
                    value.setSubmitUserId(simpleUserPrincipal.getUser().getId());
                    value.setSubmitUserName(simpleUserPrincipal.getUsername());
                });


        // 设置状态
        value.setStatus(dictService.getFirstAuditStatus().getId());
        value.setFinished(false);
        // 失败状态可以修改 ...
        value.setCanModify(false);
        reportRepository.save(value);
    }

    public void updateReport(ReportParam reportParam) {
        Optional<Report> reportContainer = reportRepository.findById(reportParam.getId());
        Assert.isTrue(reportContainer.isPresent(), "当前报告不存在!!!");
        Report report = reportContainer.get();
        Assert.isTrue(report.getCanModify() == null || report.getCanModify(), "当前报告处于审核中,无法修改!!!");
        BeanUtils.updateProperties(reportParam, reportParam);
        reportRepository.save(report);
    }


    public List<Report> getReportList(Integer projectId) {
        return reportRepository.findAll(Example.of(
                Report.builder()
                        .projectId(projectId)
                        .build()
        ));
    }
}
