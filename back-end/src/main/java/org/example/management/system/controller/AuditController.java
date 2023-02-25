package org.example.management.system.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.param.AuditParam;
import org.example.management.system.service.AuditService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/audit/v1")
@RequiredArgsConstructor
@Api
public class AuditController {

    private final AuditService auditService;
    @PutMapping
    public void updateAudit(AuditParam auditParam) {
        auditService.updateAudit(auditParam);
    }
}