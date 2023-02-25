package org.example.management.system.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.param.UserParam;
import org.example.management.system.model.vo.UserVo;
import org.example.management.system.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author FLJ
 * @date 2023/2/24
 * @time 9:17
 * @Description 用户考勤打卡等相关操作
 */
@RestController
@RequestMapping("/api/user/v1")
@RequiredArgsConstructor
@Api
public class UserController {

    private final UserService userService;

    /**
     * 注册用户
     *
     * 此接口唯一性约束,邮箱全局唯一 !!!
     * @param userParam 用户注册参数
     */
    @ApiOperation(value = "注册接口",notes = "此接口唯一性约束,邮箱全局唯一 !!!,且不需要提交 startTimeAt / endTimeAt参数!!!")
    @PostMapping("register")
    public void registerUser(
            @RequestBody
            UserParam userParam
    ) {
        userService.createUser(userParam);
    }

    /**
     * 删除用户信息
     * @param id 用户id
     */
    @ApiOperation("根据id删除用户信息")
    @DeleteMapping("delete/{id}")
    public void deleteUser(@PathVariable("id") Integer id) {
        userService.deleteUserById(id);
    }

    /**
     * 通过用户id获取用户详情信息
     * @param id 用户id
     * @return 用户详情信息
     */
    @ApiOperation("根据id获取用户详情信息")
    @GetMapping("get/{id}")
    public UserVo getUserDetails(@PathVariable("id") Integer id) {
        return userService.getUserDetails(id);
    }

    /**
     * 获取用户列表且分页,支持 用户名称左like, 邮箱精确匹配, 注册时间(区间匹配)
     * @param userParam 查询参数
     * @param pageable 分页参数 {page,size}请求查询参数, page 初始值第一页为0 ...
     * @return 用户列表信息
     */
    @ApiOperation(value = "获取用户列表且分页",notes = "用户名称左like, 邮箱精确匹配, 注册时间(区间匹配,通过提交startTimeAt / endTimeAt 处理),三者皆可空")
    @GetMapping("list")
    public Page<UserVo> getAllUserDetails(UserParam userParam, Pageable pageable) {
        return userService.getAllUserDetailsByPage(userParam,pageable);
    }
}
