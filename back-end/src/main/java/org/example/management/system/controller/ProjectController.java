package org.example.management.system.controller;

import lombok.RequiredArgsConstructor;
import org.example.management.system.model.entity.Project;
import org.example.management.system.model.param.ProjectParam;
import org.example.management.system.service.ProjectService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/project/v1")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public void createProject(@RequestBody ProjectParam param) {
        projectService.createProject(param);
    }

    @PutMapping
    public void updateProject(@RequestBody ProjectParam param) {
        projectService.updateProject(param);
    }

    @GetMapping("{id}")
    public Project findProjectById(@PathVariable("id") Integer id) {
        return projectService.findById(id);
    }

    @GetMapping("list")
    public List<Project> findProjects(ProjectParam param, Pageable pageable) {
        return projectService.findProjects(param,pageable);
    }

    @DeleteMapping("{id}")
    public void deleteProjectById(@PathVariable("id") Integer id) {
        projectService.deleteProjectById(id);
    }
}
