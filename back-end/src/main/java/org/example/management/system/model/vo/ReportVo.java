package org.example.management.system.model.vo;

import lombok.Data;

@Data
public class ReportVo {

    private Integer id;

    private Integer projectId;

    private String projectName;


    private String description;


    private Integer reportUrlId;

    /**
     * 报告url 地址
     */
    private String reportUrlStr;

    /**
     * media type
     */
    private Integer mediaType;

    private Boolean assignFlag;

    /**
     * 报告的类型,一般是id
     */
    private Integer reportType;

    private Integer reportFormat;

    private String reportName;

    private Integer submitUserId;

    private String submitUserName;

    private Integer auditUserId;

    private String auditUserName;

    private String failureReason;

    private Boolean failureFlag;
    private String successDescription;

    /**
     * 审核阶段
     */
    private Integer auditPhase;

    /**
     * 审核状态
     */
    private Integer status;

    private Boolean canModify;

    private Boolean finished;

    private String createTimeStr;


    private Long createAt;

    private String updateTimeStr;

    private Long updateAt;
}
