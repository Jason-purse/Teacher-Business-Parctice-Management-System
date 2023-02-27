package org.example.management.system.controller;

import com.generatera.security.authorization.server.specification.LightningUserContext;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.entity.Report;
import org.example.management.system.model.param.AuditParam;
import org.example.management.system.model.security.SimpleUserPrincipal;
import org.example.management.system.service.AuditService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit/v1")
@RequiredArgsConstructor
@Api
public class AuditController {

    private final AuditService auditService;

    /**
     * 审核通过 或者打回 ..
     */
    @PutMapping
    public void updateAudit(@RequestBody AuditParam auditParam) {
        LightningUserContext
                .get()
                .getUserPrincipal(SimpleUserPrincipal.class)
                .ifPresent(
                        user -> {
                            auditParam.setAuditUserId(user.getUser().getId());
                            auditParam.setAuditUserName(user.getUsername());
                        }
                );
        auditService.updateAudit(auditParam);
    }

    @PostMapping
    public void createAudit(@RequestBody AuditParam param) {
        auditService.createAudit(param);
    }

    @GetMapping
    public Page<Report> getAllReportsForAuditByPage(AuditParam param, Pageable pageable) {
        LightningUserContext.get()
                .getUserPrincipal(SimpleUserPrincipal.class)
                .ifPresent(user -> param.setAuditUserId(user.getUser().getId()));
        return auditService.getAllReportsForAuditByPage(param,pageable);
    }
}
