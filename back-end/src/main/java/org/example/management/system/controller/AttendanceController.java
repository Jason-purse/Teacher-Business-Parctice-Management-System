package org.example.management.system.controller;

import com.generatera.security.authorization.server.specification.LightningUserContext;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.param.AttendanceParam;
import org.example.management.system.model.security.SimpleUserPrincipal;
import org.example.management.system.model.vo.AttendanceVo;
import org.example.management.system.service.AttendanceService;
import org.example.management.system.utils.PageUtil;
import org.springframework.data.domain.Page;
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
@Api
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * 获取考勤详情 !!!
     */
    @GetMapping("list")
    public Page<AttendanceVo> getAllAttendancesByPage(
            AttendanceParam param,
            Pageable pageable) {
        return attendanceService.getAttendanceInfosByPage(param, PageUtil.getPageable(pageable));
    }


    /**
     * 创建考勤 ...
     */
    @PostMapping
    public void createAttendance() {
        AttendanceParam attendanceParam = new AttendanceParam();
        LightningUserContext.get()
                        .getUserPrincipal(SimpleUserPrincipal.class)
                                .ifPresent(user -> {
                                    attendanceParam.setUserId(user.getUser().getId());
                                    attendanceParam.setUsername(user.getName());
                                });
        attendanceService.createAttendanceInfo(attendanceParam);
    }

    /**
     * 当前用户的今日考勤情况
     */
    @GetMapping("/currentuser/info")
    public AttendanceVo currentAttendanceInfo() {
        AttendanceParam attendanceParam = new AttendanceParam();


        LightningUserContext.get()
                    .getUserPrincipal(SimpleUserPrincipal.class)
                    .ifPresent(ele -> {
                        attendanceParam.setUserId(ele.getUser().getId());
                    });

        return attendanceService.getCurrentUserToDayAttendanceInfo(attendanceParam.getUserId());
    }
}
