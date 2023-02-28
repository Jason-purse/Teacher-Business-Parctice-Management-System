package org.example.management.system.model.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportRRAuditPhaseParam {

    private Integer userId;
    private String username;

    private Integer reportId;

    private Integer auditPhaseId;
}
