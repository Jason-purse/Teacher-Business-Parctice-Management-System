package org.example.management.system.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author FLJ
 * @date 2023/2/24
 * @time 11:12
 * @Description 考勤vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceVo {

    private Integer id;

    private Integer userId;

    private String username;

    private String createTimeStr;

    private Long createAt;

}
