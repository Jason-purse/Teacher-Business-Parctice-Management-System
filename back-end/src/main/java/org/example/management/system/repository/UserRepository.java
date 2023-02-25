package org.example.management.system.repository;

import org.example.management.system.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User,Integer> , JpaSpecificationExecutor<User> {
}
