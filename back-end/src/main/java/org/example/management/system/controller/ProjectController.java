package org.example.management.system.controller;

import com.jianyue.lightning.result.Result;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.entity.Project;
import org.example.management.system.model.param.ProjectParam;
import org.example.management.system.service.ProjectService;
import org.example.management.system.utils.ResultExt;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin/project/v1")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    public Result<?> createProject(@RequestBody  ProjectParam param) {
        projectService.createProject(param);
        return ResultExt.success();
    }

}
