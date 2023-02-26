package org.example.management.system.model.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditParam {

    private Integer  reportId;

    private String reportName;

    private Integer auditUserId;

    private String auditUserName;

   // ------------------ 真正进行审核的需要 -----------------
    private String failureReason;
    /**
     * 成功描述
     */
    private String successDescription;

    /**
     * 是否失败
     */
    private Boolean failureFlag;

    private String projectName;

    private String submitUserName;

    private Integer auditPhase;


    private Long startTimeAt;

    private Long endTimeAt;

}
