package org.example.management.system.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author FLJ
 * @date 2023/2/28
 * @time 13:59
 * @Description 增加排序规则
 */
public class PageUtil {

    public static Pageable getPageable(Pageable pageable) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by("createAt").descending());
    }
}
