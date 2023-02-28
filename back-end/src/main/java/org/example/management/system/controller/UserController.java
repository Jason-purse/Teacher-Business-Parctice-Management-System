package org.example.management.system.controller;

import com.generatera.security.authorization.server.specification.LightningUserContext;
import com.jianyue.lightning.boot.starter.util.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.entity.User;
import org.example.management.system.model.param.UserParam;
import org.example.management.system.model.security.SimpleUserPrincipal;
import org.example.management.system.model.vo.UserVo;
import org.example.management.system.service.UserService;
import org.example.management.system.utils.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/user/v1")
@RequiredArgsConstructor
@Api
public class UserController {


    private final UserService userService;

    /**
     * 删除用户信息
     *
     * @param id 用户id
     */
    @ApiOperation("根据id删除用户信息")
    @DeleteMapping("delete/{id}")
    public void deleteUser(@PathVariable("id") Integer id) {
        userService.deleteUserById(id);
    }

    /**
     * 通过用户id获取用户详情信息
     *
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
     *
     * @param userParam 查询参数
     * @param pageable  分页参数 {page,size}请求查询参数, page 初始值第一页为0 ...
     * @return 用户列表信息
     */
    @ApiOperation(value = "获取用户列表且分页", notes = "用户名称左like, 邮箱精确匹配, 注册时间(区间匹配,通过提交startTimeAt / endTimeAt 处理),三者皆可空")
    @GetMapping("list")
    public Page<UserVo> getAllUserDetails(UserParam userParam, @ApiParam("分页参数") Pageable pageable) {
        return userService.getAllUserDetailsByPage(userParam, PageUtil.getPageable(pageable));
    }

    /**
     * 获取所有用户数据并组装为审核 ..
     * 根据审核阶段来查询那些用户可以被选择 ..
     */
    @GetMapping("list/audit")
    public Page<UserVo> getAllUserDetailsForAudit(Integer auditPhaseId,UserParam userParam,Pageable pageable) {
        return userService.getAllUserDetailsForAudit(auditPhaseId,userParam, PageUtil.getPageable(pageable));
    }

    /**
     * 当前用户信息
     */
    @GetMapping("current/userinfo")
    public UserVo getCurrentUserInfo() {
        User user = LightningUserContext.get().getUserPrincipal(SimpleUserPrincipal.class)
                .orElseThrow(() -> new IllegalStateException("当前不存在用户信息 !!!"))
                .getUser();
        UserVo userVo = userService.getUserInfoById(user.getId());
        assert userVo != null;
        return userVo;
    }

    @PutMapping("current/userinfo")
    public void updateCurrentInfo(@RequestBody UserParam userParam) {
        User user = LightningUserContext.get()
                .getUserPrincipal(SimpleUserPrincipal.class)
                .orElseThrow(() -> new IllegalStateException("当前不存在用户信息 !!!"))
                .getUser();

        userService.updateCurrentInfo(userParam,user);
    }
}
