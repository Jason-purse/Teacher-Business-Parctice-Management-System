package org.example.management.system.repository;

import org.example.management.system.model.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReportRepository extends JpaRepository<Report,Integer> , JpaSpecificationExecutor<Report> {

    Integer deleteAllByProjectId(Integer projectId);
}
