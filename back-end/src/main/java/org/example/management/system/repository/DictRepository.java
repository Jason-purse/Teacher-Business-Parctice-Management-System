package org.example.management.system.repository;

import org.example.management.system.model.entity.Dict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DictRepository extends JpaRepository<Dict,Integer>, JpaSpecificationExecutor<Dict> {

}
