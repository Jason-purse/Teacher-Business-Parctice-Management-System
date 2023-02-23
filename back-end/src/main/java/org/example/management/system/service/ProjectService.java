package org.example.management.system.service;

import com.jianyue.lightning.boot.starter.util.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.example.management.system.model.entity.Project;
import org.example.management.system.model.param.ProjectParam;
import org.example.management.system.repository.ProjectRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public void createProject(ProjectParam param) {
        Project project = BeanUtils.transformFrom(param, Project.class);
        assert project != null;

        // 不需要做重复性判断
        projectRepository.save(project);
    }

    public void updateProject(ProjectParam param) {
        Optional<Project> project = projectRepository.findById(param.getId());
        if(project.isEmpty()) {
            throw new IllegalArgumentException("当前项目不存在，无法修改!!!");
        }
        Project target = project.get();
        // 不做任何复杂的判断 ..
        BeanUtils.updateProperties(project,target);
        projectRepository.save(target);
    }

    public void deleteProject(ProjectParam param) {
        // // TODO: 2023/2/22 删除条件待定
    }

    public void deleteProjectById(Integer id) {
        projectRepository.deleteById(id);
    }

    public List<Project> findProjects(ProjectParam param, Pageable pageable) {
        Project project = BeanUtils.transformFrom(param, Project.class);
        assert project != null;
        return projectRepository.findAll(Example.of(project),pageable).getContent();
    }

    public Project findById(Integer id) {
        Optional<Project> project = projectRepository.findById(id);
        if(project.isEmpty()) {
            throw new IllegalArgumentException("项目不存在,请检查!!!");
        }
        return project.get();
    }
}
