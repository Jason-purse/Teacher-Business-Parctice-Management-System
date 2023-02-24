package org.example.management.system.controller;

import lombok.RequiredArgsConstructor;
import org.example.management.system.model.param.AttendanceParam;
import org.example.management.system.model.vo.AttendanceVo;
import org.example.management.system.service.AttendanceService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author FLJ
 * @date 2023/2/24
 * @time 9:16
 * @Description 考勤打卡
 */
@RestController
@RequestMapping("/api/admin/attendance/v1")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * 获取考勤详情 !!!
     */
    @GetMapping("list")
    public List<AttendanceVo> getAllAttendancesByPage(
            AttendanceParam param,
            Pageable pageable) {
        return attendanceService.getAttendanceInfosByPage(param, pageable);
    }


    /**
     * 创建考勤 ...
     */
    @PostMapping
    public void createAttendance(@RequestBody AttendanceParam attendanceParam) {
        attendanceService.createAttendanceInfo(attendanceParam);
    }
}
