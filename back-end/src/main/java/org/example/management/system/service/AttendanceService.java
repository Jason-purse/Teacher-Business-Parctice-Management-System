package org.example.management.system.service;

import com.jianyue.lightning.boot.starter.util.BeanUtils;
import com.jianyue.lightning.boot.starter.util.OptionalFlux;
import com.jianyue.lightning.boot.starter.util.StreamUtil;
import com.jianyue.lightning.boot.starter.util.lambda.LambdaUtils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.entity.Attendance;
import org.example.management.system.model.param.AttendanceParam;
import org.example.management.system.model.vo.AttendanceVo;
import org.example.management.system.repository.AttendanceRepository;
import org.example.management.system.utils.DateTimeUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * @author FLJ
 * @date 2023/2/24
 * @time 9:18
 * @Description 考勤服务
 */
@Service
@RequiredArgsConstructor
public class AttendanceService {


    private final AttendanceRepository attendanceRepository;

    /**
     * 获取打卡详情 !!!
     *
     * @param attendanceParam 查询参数 !!!
     * @return 打开详情 !!!
     */
    public AttendanceVo getAttendanceDetail(AttendanceParam attendanceParam) {
        Optional<Attendance> attendance = attendanceRepository
                .findOne(Example.of(
                        Attendance.builder()
                                .userId(attendanceParam.getUserId())
                                .createAt(DateTimeUtils.getStartTimeOfDay())
                                .build()
                ));
        Assert.isTrue(attendance.isPresent(), "今日未打卡,请打卡 !!!");

        AttendanceVo attendanceVo = BeanUtils.transformFrom(attendance.get(), AttendanceVo.class);
        assert attendanceVo != null;
        return attendanceVo;
    }


    /**
     * 打卡
     */
    public void createAttendanceInfo(AttendanceParam attendanceParam) {
        Optional<Attendance> attendance = attendanceRepository
                .findOne(Example.of(
                        Attendance.builder()
                                .userId(attendanceParam.getUserId())
                                .createAt(DateTimeUtils.getStartTimeOfDay())
                                .build()
                ));
        Assert.isTrue(attendance.isEmpty(), "今日已打开,请勿重复打卡 !!!");
        Attendance attendance1 = BeanUtils.transformFrom(attendanceParam, Attendance.class);
        assert attendance1 != null;
        long startTimeOfDay = DateTimeUtils.getStartTimeOfDay();
        LocalDateTime now = LocalDateTime.now();
        attendance1.setSignAt(DateTimeUtils.getTimeOfDay(now));
        attendance1.setSignTimeStr(DateTimeUtils.getTimeStr(now));
        attendance1.setCreateAt(startTimeOfDay);
        attendance1.setCreateTimeStr(DateTimeUtils.getTimeStr(LocalDate.now().atStartOfDay()));
        attendanceRepository.save(attendance1);
    }

    /**
     * 查询条件,复合查询,且分页
     */
    public Page<AttendanceVo> getAttendanceInfosByPage(AttendanceParam attendanceParam, Pageable pageable) {

        Page<Attendance> all = attendanceRepository
                .findAll(
                        new ComplexSpecification(
                                attendanceParam.getUsername(),
                                attendanceParam.getStartTimeAt(),
                                attendanceParam.getEndTimeAt()),
                        pageable
                );


        return OptionalFlux
                .of(all.getContent())
                .map(StreamUtil.listMap(BeanUtils.transformFrom(AttendanceVo.class)))
                .orElse(Collections.emptyList())
                .map(ele -> new PageImpl<>(ele, all.getPageable(), all.getTotalElements()))
                .getResult();
    }

    public AttendanceVo getCurrentUserToDayAttendanceInfo(Integer userId) {
        Optional<Attendance> one = attendanceRepository.findOne(Example.of(
                Attendance.builder()
                        .userId(userId)
                        .createAt(DateTimeUtils.getTimeOfDay(LocalDate.now().atStartOfDay()))
                        .build()
        ));
        return one.map(BeanUtils.transformFrom(AttendanceVo.class)).orElse(null);
    }


    @AllArgsConstructor
    class ComplexSpecification implements Specification<Attendance> {

        private String username;

        private Long startTimeAt;

        private Long endTimeAt;


        @Override
        public Predicate toPredicate(@NotNull Root<Attendance> root, @NotNull CriteriaQuery<?> query, @NotNull CriteriaBuilder criteriaBuilder) {

            return OptionalFlux.of(username)
                    .map(userName -> {
                        Path<String> path = root.get(LambdaUtils.getPropertyNameForLambda(Attendance::getUsername));
                        return criteriaBuilder.like(path, username);
                    })
                    .combine(
                            OptionalFlux
                                    .of(startTimeAt)
                                    .<Predicate>map(
                                            start -> {
                                                Path<Long> creatAt = root.get(LambdaUtils.getPropertyNameForLambda(Attendance::getCreateAt));
                                                return OptionalFlux
                                                        .of(endTimeAt)
                                                        .map(end -> criteriaBuilder.between(creatAt, start, end))
                                                        .orElse(() -> criteriaBuilder.greaterThanOrEqualTo(creatAt, start))
                                                        .getResult();
                                            }
                                    )
                                    .orElse(
                                            () ->
                                                    OptionalFlux
                                                            .of(endTimeAt)
                                                            .map(
                                                                    end -> {
                                                                        Path<Long> creatAt = root.get(LambdaUtils.getPropertyNameForLambda(Attendance::getCreateAt));
                                                                        return criteriaBuilder.lessThanOrEqualTo(creatAt, endTimeAt);
                                                                    }
                                                            )
                                                            .getResult()


                                    ),
                            criteriaBuilder::and
                    ).getResult();
        }
    }

}
