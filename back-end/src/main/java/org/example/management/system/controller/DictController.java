package org.example.management.system.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.entity.Dict;
import org.example.management.system.model.param.DictParam;
import org.example.management.system.service.DictService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dict/v1")
@RequiredArgsConstructor
@Api
public class DictController {

    private final DictService dictService;

    @PostMapping("init/all")
    public void initAllDicts() {
        dictService.initAllDicts();
    }

    @GetMapping("{id}")
    public Dict getDictById(@PathVariable("id") Integer id) {
        return dictService.getDictItemById(id);
    }

    @GetMapping("list")
    public List<Dict> getAllDictDataTypes() {
        return dictService.getAllDictDataTypes();
    }

    @GetMapping("list/page")
    public Page<Dict> getAllDictDataTypesByPage(DictParam param, Pageable pageable) {
        return dictService.getAllDictDataTypesByPage(param,pageable);
    }

    @GetMapping("by/datatype/{itemtype}")
    public List<Dict> getAllDictItemsByParentType(@PathVariable("itemtype") String parentDataType) {
        return dictService.getDictItemsBy(parentDataType);
    }

}
