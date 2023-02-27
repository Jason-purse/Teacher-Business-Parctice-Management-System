package org.example.management.system.controller;

import com.generatera.security.authorization.server.specification.LightningUserContext;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.entity.Report;
import org.example.management.system.model.entity.User;
import org.example.management.system.model.param.ReportParam;
import org.example.management.system.model.security.SimpleUserPrincipal;
import org.example.management.system.model.vo.ReportVo;
import org.example.management.system.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/report/v1")
@RequiredArgsConstructor
@Api
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public void createReport(@RequestBody ReportParam reportParam) {
        LightningUserContext.get()
                .getUserPrincipal(SimpleUserPrincipal.class)
                .ifPresent(userPrincipal -> {
                    User user = userPrincipal.getUser();
                    reportParam.setSubmitUserId(user.getId());
                    reportParam.setSubmitUserName(user.getUsername());
                });

        // 设置状态为
        reportService.createReport(reportParam);
    }

    @GetMapping("list/{projectid}")
    public List<ReportVo> getReportList(@PathVariable("projectid") Integer projectId) {
        return reportService.getReportList(projectId);
    }

    @PutMapping
    public void updateReport(@RequestBody ReportParam reportParam) {
        LightningUserContext.get()
                        .getUserPrincipal(SimpleUserPrincipal.class)
                                .ifPresent(userPrincipal -> {
                                    User user = userPrincipal.getUser();
                                    reportParam.setSubmitUserId(user.getId());
                                    reportParam.setSubmitUserName(user.getUsername());
                                });
        reportService.updateReport(reportParam);
    }

    @DeleteMapping("{id}")
    public void deleteReport(@PathVariable("id") Integer id) {
        reportService.deleteReport(id);
    }
}
