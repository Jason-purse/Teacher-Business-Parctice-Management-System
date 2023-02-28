package org.example.management.system.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "report")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Report implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer projectId;

    private String projectName;


    private String description;


    private Integer reportUrlId;

    /**
     * 报告的类型,一般是id
     */
    private Integer reportType;

    private Integer reportFormat;

    private String reportName;


    private Integer submitUserId;

    private String submitUserName;


    private Boolean assignFlag;

    /**
     * 当前审核人 ...
     */
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
