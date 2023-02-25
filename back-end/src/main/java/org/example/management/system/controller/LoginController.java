package org.example.management.system.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.param.UserParam;
import org.example.management.system.model.vo.UserVo;
import org.example.management.system.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * @author FLJ
 * @date 2023/2/24
 * @time 9:17
 * @Description 用户考勤打卡等相关操作
 */
@RestController
@RequestMapping("/api/login/v1")
@RequiredArgsConstructor
@Api
public class LoginController {

    private final UserService userService;

    /**
     * 注册用户
     * <p>
     * 此接口唯一性约束,邮箱全局唯一 !!!
     *
     * @param userParam 用户注册参数
     */
    @ApiOperation(value = "注册接口", notes = "此接口唯一性约束,邮箱全局唯一 !!!,且不需要提交 startTimeAt / endTimeAt参数!!!")
    @PostMapping("register")
    public void registerUser(
            @RequestBody
            UserParam userParam
    ) {
        userService.createUser(userParam);
    }
}
