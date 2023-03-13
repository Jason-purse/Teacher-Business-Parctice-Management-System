package org.example.management.system.service;

import com.generatera.security.authorization.server.specification.LightningUserContext;
import com.jianyue.lightning.boot.starter.util.BeanUtils;
import com.jianyue.lightning.boot.starter.util.ElvisUtil;
import com.jianyue.lightning.boot.starter.util.OptionalFlux;
import com.jianyue.lightning.boot.starter.util.lambda.LambdaUtils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.constant.DictConstant;
import org.example.management.system.model.entity.Dict;
import org.example.management.system.model.entity.Project;
import org.example.management.system.model.entity.User;
import org.example.management.system.model.param.ProjectParam;
import org.example.management.system.model.security.SimpleUserPrincipal;
import org.example.management.system.repository.ProjectRepository;
import org.example.management.system.utils.DateTimeUtils;
import org.example.management.system.utils.EscapeUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final DictService dictService;

    private final ReportService reportService;

    private final RoleService roleService;

    public void createProject(ProjectParam param) {
        Project project = BeanUtils.transformFrom(param, Project.class);
        assert project != null;
        Dict item = dictService.getFirstDataItemInFlow(DictConstant.PROJECT_STATUS);
        project.setStatus(item.getId());

        LocalDateTime now = LocalDateTime.now();
        project.setCreateAt(DateTimeUtils.getTimeOfDay(now));
        project.setCreateTimeStr(DateTimeUtils.getTimeStr(now));
        // 可以删除空项目
        project.setFinished(true);
        // 设置项目状态
        // 不需要做重复性判断
        projectRepository.save(project);
    }

    public void updateProject(ProjectParam param) {
        Optional<Project> project = projectRepository.findById(param.getId());
        if (project.isEmpty()) {
            throw new IllegalArgumentException("当前项目不存在，无法修改!!!");
        }
        Project target = project.get();
        // 不做任何复杂的判断 ..
        BeanUtils.updateProperties(project, target);
        projectRepository.save(target);
    }

    public void deleteProject(ProjectParam param) {
        // // TODO: 2023/2/22 删除条件待定
    }

    @Transactional
    public void deleteProjectById(Integer id, Boolean force) {
        Optional<Project> project = projectRepository.findById(id);
        project.ifPresent(ele -> {
            if (!force) {
                Assert.isTrue(ele.getFinished(), "当前项目正在进行中,无法删除 !!!");
            }
            projectRepository.deleteById(id);
            // 删除关联的项目
            reportService.deleteReportsByProjectId(ele.getId());
        });
    }

    public Page<Project> findProjects(ProjectParam param, Pageable pageable) {

        // 当前用户是否为管理员
        LightningUserContext.get()
                .getUserPrincipal(SimpleUserPrincipal.class)
                .ifPresent(ele -> {
                    List<Dict> userRoles = roleService.getUserRoles(ele.getUser().getId());
                    boolean status = true;
                    for (Dict userRole : userRoles) {
                        if (userRole.getItemType().equals(DictConstant.ROLE_ADMIN_TYPE)) {
                            status = false;
                            break;
                        }
                    }
                    if(status) {
                        // 设置只能查询自己
                        param.setUserId(ele.getUser().getId());
                    }

                });

        return projectRepository.findAll(new ProjectComplexSpecification(
                param.getUserId(),
                param.getName(), param.getUsername(), param.getStartTimeAt(),
                param.getEndTimeAt()
        ), pageable);
    }

    @AllArgsConstructor
    class ProjectComplexSpecification implements Specification<Project> {

        private Integer userId;
        private String name;

        private String username;


        private Long startTimeAt;

        private Long endTimeAt;

        @Override
        public Predicate toPredicate(@NotNull Root<Project> root, @NotNull CriteriaQuery<?> query, @NotNull CriteriaBuilder criteriaBuilder) {
            return OptionalFlux
                    .of(name)
                    .map(projectName -> criteriaBuilder.like(
                            root.get(LambdaUtils.getPropertyNameForLambda(Project::getName)),
                            EscapeUtil.escapeExprSpecialWord(projectName.trim()).concat("%"))
                    )
                    .combine(
                            OptionalFlux
                                    .of(userId)
                                    .map(id -> criteriaBuilder.equal(root.get(LambdaUtils.getPropertyNameForLambda(Project::getUserId)), id))
                                    .orElse(OptionalFlux.of(
                                                    ElvisUtil.stringElvis(username, null)
                                            )
                                            .map(userName -> criteriaBuilder
                                                    .like(
                                                            root.get(LambdaUtils.getPropertyNameForLambda(Project::getUsername)),
                                                            EscapeUtil.escapeExprSpecialWord(userName.trim()).concat("%")
                                                    ))
                                    )
                            ,
                            criteriaBuilder::and
                    )
                    .combine(
                            OptionalFlux.of(startTimeAt)
                                    .map(startTime ->
                                            OptionalFlux.of(endTimeAt)
                                                    .map(endTime -> criteriaBuilder.between(
                                                            root.get(LambdaUtils.getPropertyNameForLambda(Project::getCreateAt)),
                                                            startTime,
                                                            endTime
                                                    ))
                                                    .orElse(
                                                            criteriaBuilder.greaterThanOrEqualTo(
                                                                    root.get(LambdaUtils.getPropertyNameForLambda(Project::getCreateAt)),
                                                                    startTime
                                                            )
                                                    )
                                                    .<Predicate>getResult()
                                    )
                                    .orElse(
                                            OptionalFlux.of(endTimeAt)
                                                    .map(endTime -> criteriaBuilder.lessThanOrEqualTo(
                                                            root.get(LambdaUtils.getPropertyNameForLambda(Project::getCreateAt)),
                                                            endTime
                                                    ))
                                    ),
                            criteriaBuilder::and
                    )
                    .getResult();
        }
    }

    public Project findById(Integer id) {
        Optional<Project> project = projectRepository.findById(id);
        if (project.isEmpty()) {
            throw new IllegalArgumentException("项目不存在,请检查!!!");
        }
        return project.get();
    }
}
