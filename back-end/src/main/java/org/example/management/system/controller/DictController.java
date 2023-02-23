package org.example.management.system.controller;

import lombok.RequiredArgsConstructor;
import org.example.management.system.model.entity.Dict;
import org.example.management.system.service.DictService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dict/v1")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;


    @GetMapping("{id}")
    public Dict getDictById(@PathVariable("id") Integer id) {
        return dictService.getDictItemById(id);
    }

    @GetMapping("list")
    public List<Dict> getAllDictDataTypes() {
        return dictService.getAllDictDataTypes();
    }

    @GetMapping("list/page")
    public Page<Dict> getAllDictDataTypesByPage(Pageable pageable) {
        return dictService.getAllDictDataTypesByPage(pageable);
    }

    @GetMapping("by/datatype")
    public List<Dict> getAllDictItemsByParentType(Integer parentDataType) {
        return dictService.getDictItemsBy(parentDataType);
    }
}
