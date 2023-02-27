package org.example.management.system.model.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 附件参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttachmentParam {

    private String fileName;

    private String fileUrl;

    private String identifier;

    private String createTimeStr;

    private Integer mediaType;

    // 查询
    private Long startTimeAt;

    private Long endTimeAt;
}
