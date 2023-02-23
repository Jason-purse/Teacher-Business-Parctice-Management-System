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
                .supportFlow(false)
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
                        .supportFlow(false)
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
                        .supportFlow(false)
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
                        .supportFlow(false)
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
                        .supportFlow(false)
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
                        .supportFlow(false)
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
                        .supportFlow(false)
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
                        .supportFlow(false)
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
                        .supportFlow(false)
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
                        .supportFlow(true)
                        .nextDataType(11)
                        .previousDataType(-1)
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
                        .supportFlow(true)
                        .previousDataType(10)
                        .nextDataType(12)
                        .createTimeStr(timeStr)
                        .createAt(createAt)
                        .build()
        );



        dicts.add(
                Dict.builder()
                        .id(12)
                        .itemType("finished")
                        .itemValue("已完成")
                        .parentDataType(9)
                        .supportFlow(false)
                        .createTimeStr(timeStr)
                        .createAt(createAt)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(13)
                        .itemType("report_status")
                        .itemValue("报告状态")
                        .parentDataType(0)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(14)
                        .itemType("auditing")
                        .itemValue("审核中")
                        .parentDataType(13)
                        .supportFlow(true)
                        .nextDataType(15)
                        .previousDataType(-1)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(15)
                        .itemType("audit_success")
                        .itemValue("审核成功")
                        .parentDataType(13)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(16)
                        .itemType("audit_failure")
                        .itemValue("审核失败")
                        .parentDataType(13)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(17)
                        .itemType("audit_phase")
                        .itemValue("审核阶段")
                        .parentDataType(0)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );
        dicts.add(
                Dict.builder()
                        .id(18)
                        .itemType("auditor_audit_phase")
                        .itemValue("审核员审核")
                        .parentDataType(17)
                        .supportFlow(true)
                        .nextDataType(19)
                        .previousDataType(-1)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );
        dicts.add(
                Dict.builder()
                        .id(19)
                        .itemType("leader_audit_phase")
                        .itemValue("领导审核")
                        .parentDataType(17)
                        .supportFlow(true)
                        .nextDataType(20)
                        .previousDataType(18)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(20)
                        .itemType("enterprise_audit_phase")
                        .itemValue("企业负责人审核")
                        .parentDataType(17)
                        .supportFlow(true)
                        .previousDataType(19)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(21)
                        .itemType("report_type")
                        .itemValue("报告类型")
                        .parentDataType(0)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(22)
                        .itemType("request_report")
                        .itemValue("申请报告")
                        .parentDataType(21)
                        .supportFlow(true)
                        .nextDataType(23)
                        .previousDataType(-1)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(23)
                        .itemType("phase_report")
                        .itemValue("阶段性报告")
                        .parentDataType(21)
                        .supportFlow(true)
                        .nextDataType(24)
                        .previousDataType(22)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(24)
                        .itemType("summarize_report")
                        .itemValue("总结报告")
                        .parentDataType(21)
                        .supportFlow(true)
                        .previousDataType(23)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(25)
                        .itemType("report_type")
                        .itemValue("报告类型")
                        .parentDataType(0)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(26)
                        .itemType("word_report_type")
                        .itemValue("word文档")
                        .parentDataType(25)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(27)
                        .itemType("pdf_report_type")
                        .itemValue("pdf文档")
                        .parentDataType(25)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dictRepository.saveAll(dicts);
    }

}
