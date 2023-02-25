package org.example.management.system.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.entity.Report;
import org.example.management.system.model.param.ReportParam;
import org.example.management.system.service.ReportService;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/report/v1")
@RequiredArgsConstructor
@Api
public class ReportController {

    private final ReportService reportService;
    @PostMapping
    public void createReport(ReportParam reportParam) {
        reportService.createReport(reportParam);
    }

    @GetMapping("list/{projectid}")
    public List<Report> getReportList(@PathVariable("projectid") Integer projectId) {
        return reportService.getReportList(projectId);
    }
}
