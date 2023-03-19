package org.example.management.system.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttachmentVo {

    private Integer id;

    private String fileName;

    private String fileUrl;

    private String identifier;

    private String createTimeStr;

    private Long createAt;

    private Integer mediaType;

    private String projectName;

    private String reportName;

    /**
     * 报告关联数量
     */
    private Integer reportRRCount;

}
