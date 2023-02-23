package org.example.management.system.test.ApplicationTests;

import org.example.management.system.ManagementSystemApplication;
import org.example.management.system.model.entity.Dict;
import org.example.management.system.repository.DictRepository;
import org.example.management.system.utils.DateTimeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;

@SpringBootTest(classes = ManagementSystemApplication.class)
public class ApplicationTests {

    @Autowired
    private DictRepository dictRepository;


    @Test
    @Commit
    public void initDictData() {
        // 删除所有
        dictRepository.deleteAll();
        LocalDateTime now = LocalDateTime.now();
        Long createAt = now.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        String timeStr = DateTimeUtils.getTimeStr(now);

        List<Dict> dicts = new LinkedList<>();
        Dict gender = Dict.builder()
                .id(1)
                .itemType("gender")
                .itemValue("性别")
                .parentDataType(0)
                .createAt(createAt)
                .createTimeStr(timeStr)
                .build();

        dicts.add(gender);

        dicts.add(
                Dict.builder()
                        .id(2)
                        .itemType("man")
                        .itemValue("男")
                        .parentDataType(1)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(3)
                        .itemType("woman")
                        .itemValue("女")
                        .parentDataType(1)
                        .createTimeStr(timeStr)
                        .createAt(createAt)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(4)
                        .itemType("role")
                        .itemValue("角色")
                        .parentDataType(0)
                        .createTimeStr(timeStr)
                        .createAt(createAt)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(5)
                        .itemType("auditor")
                        .itemValue("审核员")
                        .parentDataType(4)
                        .createTimeStr(timeStr)
                        .createAt(createAt)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(6)
                        .itemType("teacher")
                        .itemValue("老师")
                        .parentDataType(4)
                        .createTimeStr(timeStr)
                        .createAt(createAt)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(7)
                        .itemType("leader")
                        .itemValue("学校领导")
                        .parentDataType(4)
                        .createTimeStr(timeStr)
                        .createAt(createAt)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(8)
                        .itemType("enterprise_principal")
                        .itemValue("企业负责人")
                        .parentDataType(4)
                        .createTimeStr(timeStr)
                        .createAt(createAt)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(9)
                        .itemType("project_status")
                        .itemValue("项目状态")
                        .parentDataType(0)
                        .createTimeStr(timeStr)
                        .createAt(createAt)
                        .build()
        );


        dicts.add(
                Dict.builder()
                        .id(10)
                        .itemType("not_started")
                        .itemValue("未开始")
                        .parentDataType(9)
                        .createTimeStr(timeStr)
                        .createAt(createAt)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(11)
                        .itemType("running")
                        .itemValue("进行中")
                        .parentDataType(9)
                        .createTimeStr(timeStr)
                        .createAt(createAt)
                        .build()
        );


        dicts.add(
                Dict.builder()
                        .id(12)
                        .itemType("running")
                        .itemValue("进行中")
                        .parentDataType(9)
                        .createTimeStr(timeStr)
                        .createAt(createAt)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(13)
                        .itemType("finished")
                        .itemValue("已完成")
                        .parentDataType(9)
                        .createTimeStr(timeStr)
                        .createAt(createAt)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(14)
                        .itemType("report_status")
                        .itemValue("报告状态")
                        .parentDataType(0)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(15)
                        .itemType("auditing")
                        .itemValue("审核中")
                        .parentDataType(14)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(16)
                        .itemType("audit_success")
                        .itemValue("审核成功")
                        .parentDataType(14)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(17)
                        .itemType("audit_failure")
                        .itemValue("审核失败")
                        .parentDataType(14)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(18)
                        .itemType("audit_phase")
                        .itemValue("审核阶段")
                        .parentDataType(0)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );
        dicts.add(
                Dict.builder()
                        .id(19)
                        .itemType("auditor_audit_phase")
                        .itemValue("审核员审核")
                        .parentDataType(18)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );
        dicts.add(
                Dict.builder()
                        .id(20)
                        .itemType("leader_audit_phase")
                        .itemValue("领导审核")
                        .parentDataType(18)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(21)
                        .itemType("enterprise_audit_phase")
                        .itemValue("企业负责人审核")
                        .parentDataType(18)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );


        dictRepository.saveAll(dicts);
    }

}
