package org.example.management.system.model.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectParam {

    private Integer id;

    private String name;

    /**
     * project description
     */
    private String description;

    /**
     * 申请报告文件地址
     */
    private String requestReportUrl;

    /**
     * request status
     */
    private String rrStatus;

    private String rrUserId;

    private String rrUsername;

    private String rrAuditUserId;

    private String rrAuditUserName;

    private String rrAuditFailureDescription;

    /**
     * 阶段性报告文件地址
     */
    private String phaseReportUrl;

    private String prStatus;

    private String prUserId;

    private String prUsername;

    private String prAuditUserId;

    private String prAuditUserName;

    private String prAuditFailureDescription;

    /**
     * 总结报告文件地址
     */
    private String summarizeReportUrl;

    /**
     * 同上面的其他状态,总共有3个
     *
     * auditor_audit / leader_audit / enterprise_audit
     *
     * 都包含了审核中 和审核完毕的状态
     */
    private String srStatus;

    private String srAuditFailureDescription;

    private String srUsername;

    private String srUserId;


    private String srAuditUserId;

    private String srAuditUserName;

    /**
     * 项目状态
     * started - running - finish
     * 启动 / 进行中 / 完成
     */
    private String status;
}
