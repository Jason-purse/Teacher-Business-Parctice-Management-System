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

    private String username;

    private Integer userId;

    private Long startTimeAt;

    private Long endTimeAt;

    /**
     * 项目状态
     * started - running - finish
     * 启动 / 进行中 / 完成
     */
    private String status;
}
