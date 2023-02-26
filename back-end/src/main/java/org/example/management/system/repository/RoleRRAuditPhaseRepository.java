package org.example.management.system.repository;

import org.example.management.system.model.entity.RoleRRAuditPhase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRRAuditPhaseRepository extends JpaRepository<RoleRRAuditPhase,Integer> {
    Integer deleteAllByAuditPhaseDictId(Integer auditPhaseId);
}
