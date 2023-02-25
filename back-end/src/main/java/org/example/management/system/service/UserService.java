package org.example.management.system.service;

//import com.generatera.authorization.application.server.form.login.config.components.LightningUserDetailService;
//import com.generatera.security.authorization.server.specification.LightningUserPrincipal;
import com.generatera.authorization.application.server.form.login.config.components.LightningUserDetailService;
import com.generatera.security.authorization.server.specification.LightningUserPrincipal;
import com.jianyue.lightning.boot.starter.util.BeanUtils;
import com.jianyue.lightning.boot.starter.util.OptionalFlux;
import com.jianyue.lightning.boot.starter.util.StreamUtil;
import com.jianyue.lightning.boot.starter.util.lambda.LambdaUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.entity.Attendance;
import org.example.management.system.model.entity.User;
import org.example.management.system.model.param.UserParam;
import org.example.management.system.model.security.SimpleUserPrincipal;
import org.example.management.system.model.vo.UserVo;
import org.example.management.system.repository.UserRepository;
import org.example.management.system.utils.DateTimeUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.jianyue.lightning.boot.starter.util.OptionalFlux.of;

/**
 * 管理系统的 user details service
 */
@Service
@RequiredArgsConstructor
public class UserService implements LightningUserDetailService {

    private final UserRepository userRepository;
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
        Assert.isTrue(one.isEmpty(),"当前用户已经注册,请不要重复注册 !!!");
        User user = BeanUtils.transformFrom(userParam, User.class);
        assert  user != null;
        LocalDateTime now = LocalDateTime.now();
        user.setCreateAt(DateTimeUtils.getTimeOfDay(now));
        user.setCreateTimeStr(DateTimeUtils.getTimeStr(now));
        userRepository.save(user);
    }

    public void deleteUserById(Integer id) {
        userRepository.deleteById(id);
    }

    public UserVo getUserDetails(Integer id) {
        Optional<User> user = userRepository.findById(id);
        Assert.isTrue(user.isPresent(),"没有此用户 !!!");
        UserVo userVo = BeanUtils.transformFrom(user.get(), UserVo.class);
        assert userVo != null;
        return userVo;
    }

    @Override
    public LightningUserPrincipal mapAuthenticatedUser(LightningUserPrincipal userPrincipal) {
        SimpleUserPrincipal principal = (SimpleUserPrincipal) userPrincipal;

        // 给定一个认证的不可变的用户信息 !!!
        return SimpleUserPrincipal.authenticated(principal.getUser(),principal.getAuthorities());
    }

    public Page<UserVo> getAllUserDetailsByPage(UserParam userParam, Pageable pageable) {

        // 根据名称, 邮箱 / 注册时间 / 来查询
        Page<User> all = userRepository
                .findAll(new ComplexSpecification(userParam.getUsername(), userParam.getEmail(), userParam.getStartTimeAt(), userParam.getEndTimeAt()),
                        pageable);

        return of(all.getContent().size() > 0 ? all.getContent() : null)
                .map(StreamUtil.listMap(BeanUtils.transformFrom(UserVo.class)))
                .orElse(Collections.emptyList())
                .map(ele -> {
                    return new PageImpl<>(
                            ele,
                            all.getPageable(), all.getTotalElements()
                    );
                })
                .getResult();
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
            of(email)
                .map(emailEle -> criteriaBuilder.equal(
                           root.get(LambdaUtils.getPropertyNameForLambda(User::getEmail)),
                           email
                   )
                )
                .orElse(
                    of(username)
                        .map(userName -> {
                            Path<String> path = root.get(LambdaUtils.getPropertyNameForLambda(Attendance::getUsername));
                            return criteriaBuilder.like(path, username);
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