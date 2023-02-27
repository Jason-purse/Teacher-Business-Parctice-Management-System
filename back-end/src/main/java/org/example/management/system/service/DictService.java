package org.example.management.system.service;

import com.jianyue.lightning.boot.starter.util.ElvisUtil;
import com.jianyue.lightning.boot.starter.util.OptionalFlux;
import com.jianyue.lightning.boot.starter.util.lambda.LambdaUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.constant.DictConstant;
import org.example.management.system.model.entity.Dict;
import org.example.management.system.model.param.DictParam;
import org.example.management.system.repository.DictRepository;
import org.example.management.system.utils.DateTimeUtils;
import org.example.management.system.utils.EscapeUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.example.management.system.model.constant.DictConstant.*;

@Service
@RequiredArgsConstructor
public class DictService {

    private final DictRepository dictRepository;

    public List<Dict> getDictItemsBy(String parentDataType) {
        return ElvisUtil.acquireNotNullList_Empty(
                dictRepository.findAll(Example.of(
                        Dict
                                .builder()
                                .parentDataType(parentDataType)
                                .build()
                )));
    }

    public Dict getDictItemById(Integer id) {
        return dictRepository.findById(id).orElseThrow(() -> new IllegalStateException("不存在 id = " + id + "的数据项 !!!"));
    }

    public List<Dict> getAllDictDataTypes() {
        return ElvisUtil.acquireNotNullList_Empty(
                dictRepository.findAll(
                        Example.of(
                                Dict
                                        .builder()
                                        .parentDataType(ROOT)
                                        .build()
                        )
                )
        );
    }

    public Page<Dict> getAllDictDataTypesByPage(DictParam param, Pageable pageable) {
        return dictRepository
                .findAll(new DictComplexSpecification(param.getItemType(), param.getItemValue()), pageable);
    }

    @AllArgsConstructor
    class DictComplexSpecification implements Specification<Dict> {
        private String itemType;

        private String itemValue;

        @Override
        public Predicate toPredicate(@NotNull Root<Dict> root, @NotNull CriteriaQuery<?> query, @NotNull CriteriaBuilder criteriaBuilder) {
            OptionalFlux<Predicate> other =
                    OptionalFlux
                            .of(ElvisUtil.stringElvis(itemType, null))
                            .map(ele -> {
                                Path<String> path = root.get(LambdaUtils.getPropertyNameForLambda(Dict::getItemType));
                                return criteriaBuilder.like(path, EscapeUtil.escapeExprSpecialWord(ele).concat("%"));
                            })
                            .combine(
                                    OptionalFlux
                                            .of(
                                                    ElvisUtil.stringElvis(itemValue, null))
                                            .map(
                                                    ele -> criteriaBuilder.like(root.get(
                                                            LambdaUtils.getPropertyNameForLambda(Dict::getItemValue)),
                                                            EscapeUtil.escapeExprSpecialWord(ele).concat("%"))),
                                    criteriaBuilder::and);

            return OptionalFlux
                    .of(criteriaBuilder.equal(root.get(LambdaUtils.getPropertyNameForLambda(Dict::getParentDataType)), ROOT))
                    .combine(
                            other,
                            criteriaBuilder::and)
                    .getResult();
        }
    }

    public Dict getDictByItemType(String itemType) {
        return dictRepository
                .findOne(
                        Example.of(
                                Dict.builder()
                                        .itemType(itemType)
                                        .build()
                        )
                ).orElseThrow(() -> new IllegalStateException("没有itemType = " + itemType + "的对应数据项"));
    }

    public Dict getFirstDataItemInFlow(String parentDataType) {
        return dictRepository
                .findOne(
                        Example.of(
                                Dict.builder()
                                        .parentDataType(parentDataType)
                                        .supportFlow(true)
                                        .previousDataTypeID(FIRST_ITEM_IDENTIFY)
                                        .build()
                        )
                ).orElseThrow(() -> new IllegalStateException("父数据类型为" + parentDataType + ",当前数据项并不存在于流中,系统错误!!!"));
    }

    public Dict getFirstAuditStatus() {
        return getFirstDataItemInFlow(REPORT_STATUS);
    }

    public void initAllDicts() {
        // 删除所有
        dictRepository.deleteAll();
        LocalDateTime now = LocalDateTime.now();
        Long createAt = now.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        String timeStr = DateTimeUtils.getTimeStr(now);

        List<Dict> dicts = new LinkedList<>();
        Dict gender = Dict.builder()
                .id(1)
                .itemType(GENDER)
                .itemValue("性别")
                .parentDataType(ROOT)
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
                        .parentDataType(DictConstant.GENDER)
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
                        .parentDataType(DictConstant.GENDER)
                        .supportFlow(false)
                        .createTimeStr(timeStr)
                        .createAt(createAt)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(4)
                        .itemType(ROLE)
                        .itemValue("角色")
                        .parentDataType(ROOT)
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
                        .parentDataType(ROLE)
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
                        .parentDataType(ROLE)
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
                        .parentDataType(ROLE)
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
                        .parentDataType(ROLE)
                        .supportFlow(false)
                        .createTimeStr(timeStr)
                        .createAt(createAt)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(9)
                        .itemType(PROJECT_STATUS)
                        .itemValue("项目状态")
                        .parentDataType(ROOT)
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
                        .parentDataType(PROJECT_STATUS)
                        .supportFlow(true)
                        .nextDataTypeID(11)
                        .previousDataTypeID(FIRST_ITEM_IDENTIFY)
                        .createTimeStr(timeStr)
                        .createAt(createAt)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(11)
                        .itemType("running")
                        .itemValue("进行中")
                        .parentDataType(PROJECT_STATUS)
                        .supportFlow(true)
                        .previousDataTypeID(10)
                        .nextDataTypeID(12)
                        .createTimeStr(timeStr)
                        .createAt(createAt)
                        .build()
        );


        dicts.add(
                Dict.builder()
                        .id(12)
                        .itemType("finished")
                        .itemValue("已完成")
                        .parentDataType(PROJECT_STATUS)
                        .supportFlow(false)
                        .createTimeStr(timeStr)
                        .createAt(createAt)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(13)
                        .itemType(REPORT_STATUS)
                        .itemValue("报告状态")
                        .parentDataType(ROOT)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(14)
                        .itemType("un_audit")
                        .itemValue("待指派审核")
                        .parentDataType(REPORT_STATUS)
                        .supportFlow(true)
                        .nextDataTypeID(15)
                        .previousDataTypeID(FIRST_ITEM_IDENTIFY)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(15)
                        .itemType("auditing")
                        .itemValue("审核中")
                        .parentDataType(REPORT_STATUS)
                        .supportFlow(true)
                        .nextDataTypeID(16)
                        .previousDataTypeID(14)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(16)
                        .itemType("audit_success")
                        .itemValue("审核成功")
                        .parentDataType(REPORT_STATUS)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(17)
                        .itemType("audit_failure")
                        .itemValue("审核失败")
                        .parentDataType(REPORT_STATUS)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(18)
                        .itemType(AUDIT_PHASE)
                        .itemValue("审核阶段")
                        .parentDataType(ROOT)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );
        dicts.add(
                Dict.builder()
                        .id(19)
                        .itemType("auditor_audit_phase")
                        .itemValue("审核员审核")
                        .parentDataType(AUDIT_PHASE)
                        .supportFlow(true)
                        .nextDataTypeID(20)
                        .previousDataTypeID(FIRST_ITEM_IDENTIFY)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );
        dicts.add(
                Dict.builder()
                        .id(20)
                        .itemType("leader_audit_phase")
                        .itemValue("领导审核")
                        .parentDataType(AUDIT_PHASE)
                        .supportFlow(true)
                        .nextDataTypeID(21)
                        .previousDataTypeID(19)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(21)
                        .itemType("enterprise_audit_phase")
                        .itemValue("企业负责人审核")
                        .parentDataType(AUDIT_PHASE)
                        .supportFlow(true)
                        .previousDataTypeID(20)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(22)
                        .itemType(REPORT_TYPE)
                        .itemValue("报告类型")
                        .parentDataType(ROOT)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(23)
                        .itemType("request_report")
                        .itemValue("申请报告")
                        .parentDataType(REPORT_TYPE)
                        .supportFlow(true)
                        .nextDataTypeID(24)
                        .previousDataTypeID(FIRST_ITEM_IDENTIFY)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(24)
                        .itemType("phase_report")
                        .itemValue("阶段性报告")
                        .parentDataType(REPORT_TYPE)
                        .supportFlow(true)
                        .nextDataTypeID(25)
                        .previousDataTypeID(23)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(25)
                        .itemType("summarize_report")
                        .itemValue("总结报告")
                        .parentDataType(REPORT_TYPE)
                        .supportFlow(true)
                        .previousDataTypeID(24)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(26)
                        .itemType(REPORT_FORMAT)
                        .itemValue("报告类型")
                        .parentDataType(ROOT)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(27)
                        .itemType("word_report_type")
                        .itemValue("word文档")
                        .parentDataType(REPORT_FORMAT)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(28)
                        .itemType("pdf_report_type")
                        .itemValue("pdf文档")
                        .parentDataType(REPORT_FORMAT)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(29)
                        .itemType(ATTENDANCE_STATUS)
                        .itemValue("考勤状态")
                        .parentDataType(ROOT)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(30)
                        .itemType("attendanced")
                        .itemValue("已考勤")
                        .parentDataType(ATTENDANCE_STATUS)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(31)
                        .itemType("un_attendanced")
                        .itemValue("未考勤")
                        .parentDataType(ATTENDANCE_STATUS)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(32)
                        .itemType(MEDIA_FORMAT)
                        .itemValue("文件类型")
                        .parentDataType(ROOT)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(33)
                        .itemType("text/*")
                        .itemValue("文本")
                        .parentDataType(MEDIA_FORMAT)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );
        dicts.add(
                Dict.builder()
                        .id(34)
                        .itemType("application/pdf")
                        .itemValue("pdf")
                        .parentDataType(MEDIA_FORMAT)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(35)
                        .itemType("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                        .itemValue("docx")
                        .parentDataType(MEDIA_FORMAT)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(36)
                        .itemType("image/gif")
                        .itemValue("gif")
                        .parentDataType(MEDIA_FORMAT)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(37)
                        .itemType("image/jpeg")
                        .itemValue("jpeg")
                        .parentDataType(MEDIA_FORMAT)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dicts.add(
                Dict.builder()
                        .id(38)
                        .itemType("image/png")
                        .itemValue("png")
                        .parentDataType(MEDIA_FORMAT)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );


        dicts.add(
                Dict.builder()
                        .id(39)
                        .itemType("application/msword")
                        .itemValue("doc")
                        .parentDataType(MEDIA_FORMAT)
                        .supportFlow(false)
                        .createAt(createAt)
                        .createTimeStr(timeStr)
                        .build()
        );

        dictRepository.saveAll(dicts);
    }
}
