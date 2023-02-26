package org.example.management.system.model.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceParam {

    /**
     * 打卡人id
     */
    private Integer userId;

    /**
     * 打卡人名称
     */
    private String username;


    /**
     * 时间周期查询条件
     */
    private Long startTimeAt;


    private Long endTimeAt;

}
