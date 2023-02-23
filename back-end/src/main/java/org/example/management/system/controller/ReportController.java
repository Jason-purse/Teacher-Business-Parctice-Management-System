package org.example.management.system.controller;

import lombok.RequiredArgsConstructor;
import org.example.management.system.model.param.ReportParam;
import org.example.management.system.service.ReportService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/report/v1")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    @PostMapping
    public void createReport(ReportParam reportParam) {
        reportService.createReport(reportParam);
    }
}
