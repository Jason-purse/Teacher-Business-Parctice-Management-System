package org.example.management.system.service;

import com.generatera.security.authorization.server.specification.LightningUserContext;
import com.jianyue.lightning.boot.starter.util.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.constant.DictConstant;
import org.example.management.system.model.entity.Attachment;
import org.example.management.system.model.entity.Dict;
import org.example.management.system.model.entity.Project;
import org.example.management.system.model.entity.Report;
import org.example.management.system.model.param.ReportParam;
import org.example.management.system.model.security.SimpleUserPrincipal;
import org.example.management.system.model.vo.ReportVo;
import org.example.management.system.repository.ProjectRepository;
import org.example.management.system.repository.ReportRepository;
import org.example.management.system.utils.DateTimeUtils;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    private final DictService dictService;

    private final ProjectRepository projectRepository;

    private final AttachmentService attachmentService;

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
        if (item.getPreviousDataTypeID() != null && item.getPreviousDataTypeID() != -1) {
            Optional<Report> one = reportRepository.findOne(
                    Example.of(
                            Report
                                    .builder()
                                    .reportType(item.getPreviousDataTypeID())
                                    .projectId(reportParam.getProjectId())
                                    .build()
                    )
            );

            // 判断结束,允许创建 ...
            Assert.isTrue(one.isPresent() && one.get().getFinished() != null && one.get().getFinished(), "无法创建报告类型为 " + item.getItemValue() + "的报告,因为" + (one.isPresent() ? "前置报告正在审核中 !!!" : "前置报告没有提交并审核 !!!"));
        }

        // 并且更新项目状态为不可修改 ..
        Optional<Project> project = projectRepository.findById(reportParam.getProjectId());
        Assert.isTrue(project.isPresent(), "无法提交报告，因为无对应项目,请检查 !!!");

        Report value = BeanUtils.transformFrom(reportParam, Report.class);
        assert value != null;
        LocalDateTime now = LocalDateTime.now();
        value.setCreateAt(now.toInstant(ZoneOffset.of("+8")).toEpochMilli());
        value.setCreateTimeStr(DateTimeUtils.getTimeStr(now));
        value.setUpdateAt(value.getCreateAt());
        value.setUpdateTimeStr(value.getCreateTimeStr());
        // 设置状态
        value.setStatus(dictService.getFirstAuditStatus().getId());
        value.setFinished(false);
        // 失败状态可以修改 ...
        value.setCanModify(true);
        reportRepository.save(value);

        Project projectValue = project.get();
        // 设置进入下一个阶段 ...
        Integer status = projectValue.getStatus();
        Dict dict = dictService.getDictItemById(status);

        // 只有第一次才需要改变状态
        Dict flow = dictService.getFirstDataItemInFlow(DictConstant.PROJECT_STATUS);
        if(status.equals(flow.getId())) {
            Dict nextStatus = dictService.getDictItemById(dict.getNextDataTypeID());
            // 表示无法被删除,且进入进行中
            projectValue.setFinished(false);
            projectValue.setStatus(nextStatus.getId());
        }
        projectRepository.save(projectValue);
    }

    public void updateReport(ReportParam reportParam) {
        Optional<Report> reportContainer = reportRepository.findById(reportParam.getId());
        Assert.isTrue(reportContainer.isPresent(), "当前报告不存在!!!");
        Report report = reportContainer.get();
        Assert.isTrue(report.getCanModify() == null || report.getCanModify(), "当前报告处于审核中,无法修改!!!");
        BeanUtils.updateProperties(reportParam, report);
        if(reportParam.getRestore() != null && reportParam.getRestore()) {
            report.setStatus(dictService.getFirstAuditStatus().getId());
            report.setAuditUserId(null);
            report.setAuditUserName(null);
            report.setFailureFlag(null);
        }
        reportRepository.save(report);
    }


    public List<ReportVo> getReportList(Integer projectId) {
        List<Report> all = reportRepository.findAll(Example.of(
                Report.builder()
                        .projectId(projectId)
                        .build()
        ));
        List<Integer> list = all.stream().map(Report::getReportUrlId).distinct().toList();
        List<Attachment> attachments = attachmentService.getAllAttachmentInfosByIds(list);
        Stream<ReportVo> stream = all.stream().map(BeanUtils.transformFrom(ReportVo.class));
        if(attachments.size() > 0) {
            Map<Integer, String> collect = attachments.stream().collect(Collectors.toMap(Attachment::getId, Attachment::getFileUrl));
            stream = stream.peek(ele -> {
                String fileUrl = collect.get(ele.getId());
                ele.setReportUrlStr(fileUrl);
            });
        }
        return stream.toList();
    }

    public void deleteReport(Integer id) {
        Optional<Report> report = reportRepository.findById(id);
        report.ifPresent(ele -> {
            Assert.isTrue(ele.getCanModify() != null && ele.getCanModify(), "报告正在审核中,无法删除 !!!");
            reportRepository.deleteById(id);

            long count = reportRepository.count(Example.of(Report.builder().projectId(ele.getProjectId()).build()));
            if (count == 0) {
                // 修改项目状态
                Optional<Project> project = projectRepository.findById(ele.getProjectId());
                project.ifPresent(value -> {
                    // 设置可以修改
                    value.setFinished(true);
                    // 直接变成未开始 ..
                    value.setStatus(dictService.getFirstDataItemInFlow(DictConstant.PROJECT_STATUS).getId());
                    projectRepository.save(value);
                });
            }

        });
    }

    public void deleteReportsByProjectId(Integer projectId) {
        reportRepository.deleteAllByProjectId(projectId);
    }
}
