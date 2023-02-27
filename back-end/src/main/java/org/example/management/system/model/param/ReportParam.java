package org.example.management.system.model.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportParam {

    private Integer id;

    private Integer projectId;


    private String description;

    /**
     * 指定附件的id
     */
    private Integer reportUrlId;


    private Integer reportType;

    private Integer reportFormat;

    private String reportName;

    private Integer submitUserId;

    private String submitUserName;

    private Integer auditUserId;

    private String auditUserName;

    private String failureReason;

    private String status;


    private Boolean restore;

}
