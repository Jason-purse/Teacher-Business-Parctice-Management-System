package org.example.management.system.service;

import com.jianyue.lightning.boot.starter.util.lambda.LambdaUtils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.constant.DictConstant;
import org.example.management.system.model.entity.Dict;
import org.example.management.system.model.entity.RoleRRAuditPhase;
import org.example.management.system.model.entity.RoleRRU;
import org.example.management.system.model.param.RoleWithAuditPhaseParam;
import org.example.management.system.model.param.UserWithRoleParam;
import org.example.management.system.repository.RoleRRAuditPhaseRepository;
import org.example.management.system.repository.RoleRRURepository;
import org.example.management.system.utils.DateTimeUtils;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRRURepository rruRepository;

    private final RoleRRAuditPhaseRepository rrAuditPhaseRepository;

    private final DictService dictService;

    @Transactional
    public void updateUserWithRole(UserWithRoleParam param) {

        // 先删后增
        rruRepository.deleteAllByUserId(param.getUserId());

        LocalDateTime now = LocalDateTime.now();
        long timeOfDay = DateTimeUtils.getTimeOfDay(now);
        String timeStr = DateTimeUtils.getTimeStr(now);

        List<RoleRRU> rrus = param.getRoleIds().stream().map(ele -> {
            return RoleRRU.builder()
                    .roleId(ele)
                    .userId(param.getUserId())
                    .createAt(timeOfDay)
                    .createTimeStr(timeStr)
                    .updateAt(timeOfDay)
                    .updateTimeStr(timeStr)
                    .build();
        }).toList();
        rruRepository.saveAll(rrus);
    }

    @Transactional
    public void updateRoleWithAuditPhase(RoleWithAuditPhaseParam param) {
        LocalDateTime now = LocalDateTime.now();
        long timeOfDay = DateTimeUtils.getTimeOfDay(now);
        String timeStr = DateTimeUtils.getTimeStr(now);
        rrAuditPhaseRepository.deleteAllByAuditPhaseDictId(param.getAuditPhaseDictId());
        rrAuditPhaseRepository.save(RoleRRAuditPhase.builder()
                .auditPhaseDictId(param.getAuditPhaseDictId())
                .createAt(timeOfDay)
                .createTimeStr(timeStr)
                .roleId(param.getRoleId())
                .build());
    }

    public Page<RoleRRU> getUserIdForAuditPhase(Integer auditPhaseId, Pageable pageable) {
        Optional<RoleRRAuditPhase> one = rrAuditPhaseRepository.findOne(Example.of(
                RoleRRAuditPhase.builder()
                        .auditPhaseDictId(auditPhaseId)
                        .build()
        ));

        Assert.isTrue(one.isPresent(), "不存在对应审核阶段的角色, 请正确配置审核阶段与角色的关系");
        RoleRRAuditPhase roleRRAuditPhase = one.get();

        return rruRepository.findAll(Example.of(
                RoleRRU.builder()
                        .roleId(roleRRAuditPhase.getRoleId())
                        .build()
        ), pageable);
    }

    public Page<RoleRRU> getUserIdForAuditPhase(Integer auditPhaseId, List<Integer> userIds, Pageable pageable) {
        Optional<RoleRRAuditPhase> one = rrAuditPhaseRepository.findOne(Example.of(
                RoleRRAuditPhase.builder()
                        .auditPhaseDictId(auditPhaseId)
                        .build()
        ));

        Assert.isTrue(one.isPresent(), "不存在对应审核阶段的角色, 请正确配置审核阶段与角色的关系");
        RoleRRAuditPhase roleRRAuditPhase = one.get();

        return rruRepository.findAll(new ForUserComplexSpecification(userIds, roleRRAuditPhase.getRoleId()), pageable);
    }

    public List<Dict> getUserRoles(Integer userId) {
        List<RoleRRU> all = rruRepository.findAll(Example.of(RoleRRU.builder()
                .userId(userId)
                .build()));
        if (all.size() > 0) {
            List<Integer> roleIds = all.stream().map(RoleRRU::getRoleId).toList();
            List<Dict> roles = dictService.getDictItemsBy(DictConstant.ROLE);
            return roles.stream().filter(ele -> roleIds.contains(ele.getId()))
                    .toList();
        }
        return Collections.emptyList();
    }

    public Map<Integer, List<Dict>> getUserRolesByIds(List<Integer> ids) {
        List<RoleRRU> all = rruRepository.findAll(new ForUserIdsSpecification(ids));
        if (all.size() > 0) {
            List<Dict> roles = dictService.getDictItemsBy(DictConstant.ROLE);
            Map<Integer, Dict> roledictMap = roles.stream().collect(Collectors.toMap(Dict::getId, Function.identity()));
            Map<Integer, List<RoleRRU>> userWithRoleMap = all.stream().collect(Collectors.groupingBy(RoleRRU::getUserId, Collectors.toList()));

            Map<Integer, List<Dict>> values = new HashMap<>();
            for (Integer id : ids) {
                List<RoleRRU> dicts = userWithRoleMap.get(id);
                if (dicts != null) {
                    dicts.forEach(ele -> {
                        //  意味着有角色
                        Dict role = roledictMap.get(ele.getRoleId());
                        values.computeIfAbsent(id, (key) -> {
                                    return new ArrayList<>();
                                })
                                .add(role);
                    });
                }
            }

            return values;
        }
        return Collections.emptyMap();
    }

    @AllArgsConstructor
    class ForUserComplexSpecification implements Specification<RoleRRU> {
        private final List<Integer> userIds;

        private final Integer roleId;

        @Override
        public Predicate toPredicate(@NotNull Root<RoleRRU> root, @NotNull CriteriaQuery<?> query, @NotNull CriteriaBuilder criteriaBuilder) {
            Predicate predicate = criteriaBuilder.equal(root.get(LambdaUtils.getPropertyNameForLambda(RoleRRU::getRoleId)), roleId);
            return criteriaBuilder.and(predicate,
                    root.get(LambdaUtils.getPropertyNameForLambda(RoleRRU::getUserId)).in(userIds));
        }
    }

    @AllArgsConstructor
    class ForUserIdsSpecification implements Specification<RoleRRU> {
        private final List<Integer> userIds;

        @Override
        public Predicate toPredicate(Root<RoleRRU> root, @NotNull CriteriaQuery<?> query, @NotNull CriteriaBuilder criteriaBuilder) {
            return root.get(LambdaUtils.getPropertyNameForLambda(RoleRRU::getUserId)).in(userIds);
        }
    }
}
