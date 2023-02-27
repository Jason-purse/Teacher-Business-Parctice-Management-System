package org.example.management.system.repository;

import org.example.management.system.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProjectRepository extends JpaRepository<Project,Integer>, JpaSpecificationExecutor<Project> {
}
