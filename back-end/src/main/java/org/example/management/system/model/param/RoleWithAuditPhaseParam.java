package org.example.management.system.model.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleWithAuditPhaseParam {

    private Integer roleId;

    private Integer auditPhaseDictId;
}
