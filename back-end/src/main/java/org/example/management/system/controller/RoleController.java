package org.example.management.system.controller;

import lombok.RequiredArgsConstructor;
import org.example.management.system.model.param.RoleWithAuditPhaseParam;
import org.example.management.system.model.param.UserWithRoleParam;
import org.example.management.system.service.RoleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/role/v1")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;


    @PostMapping("rru/update")
    public void roleWithUserRRUpdate(@RequestBody UserWithRoleParam param) {
        roleService.updateUserWithRole(param);
    }

    @PostMapping("rraudit/update")
    public void roleWithAuditPhaseRRUpdate(@RequestBody RoleWithAuditPhaseParam param) {
        roleService.updateRoleWithAuditPhase(param);
    }

}
