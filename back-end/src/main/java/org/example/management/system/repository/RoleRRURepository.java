package org.example.management.system.repository;

import org.example.management.system.model.entity.RoleRRU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoleRRURepository extends JpaRepository<RoleRRU,Integer>, JpaSpecificationExecutor<RoleRRU> {
    Integer deleteAllByUserId(Integer userId);
}
