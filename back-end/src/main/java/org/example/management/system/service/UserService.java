package org.example.management.system.service;

//import com.generatera.authorization.application.server.form.login.config.components.LightningUserDetailService;
//import com.generatera.security.authorization.server.specification.LightningUserPrincipal;

import com.generatera.authorization.application.server.form.login.config.components.LightningUserDetailService;
import com.generatera.security.authorization.server.specification.LightningUserPrincipal;
import com.jianyue.lightning.boot.starter.util.BeanUtils;
import com.jianyue.lightning.boot.starter.util.ElvisUtil;
import com.jianyue.lightning.boot.starter.util.OptionalFlux;
import com.jianyue.lightning.boot.starter.util.StreamUtil;
import com.jianyue.lightning.boot.starter.util.lambda.LambdaUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.entity.Attendance;
import org.example.management.system.model.entity.Dict;
import org.example.management.system.model.entity.RoleRRU;
import org.example.management.system.model.entity.User;
import org.example.management.system.model.param.UserParam;
import org.example.management.system.model.security.SimpleUserPrincipal;
import org.example.management.system.model.vo.UserVo;
import org.example.management.system.repository.UserRepository;
import org.example.management.system.utils.DateTimeUtils;
import org.example.management.system.utils.EscapeUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.jianyue.lightning.boot.starter.util.OptionalFlux.of;

/**
 * 管理系统的 user details service
 */
@Service
@RequiredArgsConstructor
public class UserService implements LightningUserDetailService {

    private final UserRepository userRepository;

    private final RoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return SimpleUserPrincipal.unAuthenticated(
                userRepository.findOne(
                                Example.of(
                                        User.builder()
                                                .email(email)
                                                .build()
                                ))
                        .orElseThrow(() -> new BadCredentialsException("用户名或者密码错误!!!")),
                Collections.emptyList()
        );
    }

    public void createUser(UserParam userParam) {
        // 基于邮箱唯一限定 ...
        Optional<User> one = userRepository.findOne(
                Example.of(
                        User.builder()
                                .email(userParam.getEmail())
                                .build()
                )
        );
        Assert.isTrue(one.isEmpty(), "当前用户已经注册,请不要重复注册 !!!");
        User user = BeanUtils.transformFrom(userParam, User.class);
        assert user != null;
        LocalDateTime now = LocalDateTime.now();
        user.setCreateAt(DateTimeUtils.getTimeOfDay(now));
        user.setCreateTimeStr(DateTimeUtils.getTimeStr(now));

        if(StringUtils.hasText(userParam.getPassword())) {
            user.setPassword("{noop}" + userParam.getPassword().trim());
        }
        userRepository.save(user);
    }

    public void deleteUserById(Integer id) {
        userRepository.deleteById(id);
    }

    public UserVo getUserDetails(Integer id) {
        Optional<User> user = userRepository.findById(id);
        Assert.isTrue(user.isPresent(), "没有此用户 !!!");
        UserVo userVo = BeanUtils.transformFrom(user.get(), UserVo.class);
        assert userVo != null;
        return userVo;
    }

    @Override
    public LightningUserPrincipal mapAuthenticatedUser(LightningUserPrincipal userPrincipal) {
        if (userPrincipal instanceof SimpleUserPrincipal principal) {
            // 给定一个认证的不可变的用户信息 !!!
            return SimpleUserPrincipal.authenticated(principal.getUser(), principal.getAuthorities());
        }

        return userPrincipal;
    }

    public Page<UserVo> getAllUserDetailsByPage(UserParam userParam, Pageable pageable) {

        // 根据名称, 邮箱 / 注册时间 / 来查询
        Page<User> all = userRepository
                .findAll(new ComplexSpecification(userParam.getUsername(), userParam.getEmail(), userParam.getStartTimeAt(), userParam.getEndTimeAt()),
                        pageable);

        Map<Integer, List<Dict>> roles = roleService.getUserRolesByIds(all.map(User::getId).stream().distinct().toList());
        return of(all.getContent().size() > 0 ? all.getContent() : null)
                .map(StreamUtil.listMap(BeanUtils.transformTo(UserVo.class,vo -> {
                    List<Dict> rolesdict = roles.get(vo.getId());
                    vo.setRoles(ElvisUtil.acquireNotNullList_Empty(rolesdict));
                })))
                .orElse(Collections.emptyList())
                .map(ele -> {
                    return new PageImpl<>(
                            ele,
                            all.getPageable(), all.getTotalElements()
                    );
                })
                .getResult();
    }

    public Page<UserVo> getAllUserDetailsForAudit(Integer auditPhaseId, UserParam userParam, Pageable pageable) {
        if (ElvisUtil.stringElvis(userParam.getUsername(), null) != null ||
                userParam.getStartTimeAt() != null || userParam.getEndTimeAt() != null) {

            // 先查询然后查询角色表 ...
            List<User> all = userRepository.findAll(new ComplexSpecification(userParam.getUsername(), userParam.getEmail(), userParam.getStartTimeAt(), userParam.getEndTimeAt()));
            if (all.size() == 0) {
                return Page.empty();
            }
            List<Integer> userIds = all.stream().map(User::getId).toList();
            Page<RoleRRU> roleRRUS = roleService.getUserIdForAuditPhase(auditPhaseId, userIds, pageable);
            return getUserVos(pageable, roleRRUS,all);
        } else {
            Page<RoleRRU> roleRRUS = roleService.getUserIdForAuditPhase(auditPhaseId, pageable);
            if (roleRRUS.getNumberOfElements() == 0) {
                // 表示没有
                return new PageImpl<>(Collections.emptyList(), pageable, roleRRUS.getTotalElements());
            } else {
                return getUserVos(pageable, roleRRUS,null);
            }
        }
    }

    private Page<UserVo> getUserVos(Pageable pageable, Page<RoleRRU> roleRRUS,List<User> users) {
        List<RoleRRU> content = roleRRUS.getContent();
        List<Integer> userIds = content.stream().map(RoleRRU::getUserId).distinct().toList();
        if(users == null) {
            users = userRepository.findAllById(userIds);
        }
        Map<Integer,List<Dict>> rolesMap = roleService.getUserRolesByIds(userIds);


        return OptionalFlux.of(users)
                .map(StreamUtil.listMap(BeanUtils.transformTo(UserVo.class,vo -> {
                    List<Dict> roles = rolesMap.get(vo.getId());
                    vo.setRoles(ElvisUtil.acquireNotNullList_Empty(roles));
                })))
                .map(values -> new PageImpl<>(values, pageable, roleRRUS.getTotalElements()))
                .getResult();
    }

    public void updateCurrentInfo(UserParam userParam, User user) {
        String username = user.getUsername();
        String email = user.getEmail();
        BeanUtils.updateProperties(userParam, user);
        user.setEmail(email);
        user.setUsername(username);
        userRepository.save(user);
    }

    public UserVo getUserInfoById(Integer id) {
        Optional<User> user = userRepository.findById(id);
        Assert.isTrue(user.isPresent(),"当前用户不存在 !!!");
        UserVo userVo = BeanUtils.transformFrom(user.get(), UserVo.class);
        assert userVo != null;
        List<Dict> roles = roleService.getUserRoles(id);
        userVo.setRoles(ElvisUtil.acquireNotNullList_Empty(roles));
        return userVo;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class ComplexSpecification implements Specification<User> {

        private String username;

        private String email;

        private Long startTimeAt;

        private Long endTimeAt;

        @Override
        public Predicate toPredicate(@NotNull Root<User> root, @NotNull CriteriaQuery<?> query, @NotNull CriteriaBuilder criteriaBuilder) {
            return
                    of(ElvisUtil.stringElvis(email, null))
                            .map(emailEle -> criteriaBuilder.equal(
                                            root.get(LambdaUtils.getPropertyNameForLambda(User::getEmail)),
                                            emailEle.trim()
                                    )
                            )
                            .orElse(
                                    of(ElvisUtil.stringElvis(username, null))
                                            .map(userName -> {
                                                Path<String> path = root.get(LambdaUtils.getPropertyNameForLambda(Attendance::getUsername));
                                                return criteriaBuilder.like(path, EscapeUtil.escapeExprSpecialWord(userName.trim()).concat("%"));
                                            })
                                            .<Predicate>getResult()
                            )
                            .combine(
                                    of(startTimeAt)
                                            .<Predicate>map(
                                                    start -> {
                                                        Path<Long> creatAt = root.get(LambdaUtils.getPropertyNameForLambda(Attendance::getCreateAt));
                                                        return of(endTimeAt)
                                                                .map(end -> criteriaBuilder.between(creatAt, start, end))
                                                                .orElse(() -> criteriaBuilder.greaterThanOrEqualTo(creatAt, start))
                                                                .getResult();
                                                    }
                                            )
                                            .orElse(
                                                    () ->
                                                            of(endTimeAt)
                                                                    .map(
                                                                            end -> {
                                                                                Path<Long> creatAt = root.get(LambdaUtils.getPropertyNameForLambda(Attendance::getCreateAt));
                                                                                return criteriaBuilder.lessThanOrEqualTo(creatAt, endTimeAt);
                                                                            }
                                                                    )
                                                                    .getResult()


                                            ),
                                    criteriaBuilder::and
                            )
                            .getResult();
        }
    }
}
