package org.example.management.system.service;

import com.jianyue.lightning.boot.starter.util.ElvisUtil;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.entity.Dict;
import org.example.management.system.repository.DictRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictService {

    private final DictRepository dictRepository;

    public List<Dict> getDictItemsBy(Integer parentDataType) {
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
                                        .parentDataType(-1)
                                        .build()
                        )
        )
        );
    }

    public Page<Dict> getAllDictDataTypesByPage(Pageable pageable) {
        return dictRepository
                .findAll(
                Example.of(
                        Dict
                                .builder()
                                .parentDataType(-1)
                                .build()
                ),
                pageable
        );
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

    public Dict getFirstDataItemInFlow(Integer parentDataType) {
        return dictRepository
                .findOne(
                Example.of(
                        Dict.builder()
                                .parentDataType(parentDataType)
                                .supportFlow(true)
                                .previousDataType(-1)
                                .build()
                )
        ).orElseThrow(() -> new IllegalStateException("父数据类型为"+ parentDataType +",当前数据项并不存在于流中,系统错误!!!"));
    }

    public Dict getFirstAuditStatus() {
        return getFirstDataItemInFlow(getDictByItemType("report_status").getId());
    }
}
