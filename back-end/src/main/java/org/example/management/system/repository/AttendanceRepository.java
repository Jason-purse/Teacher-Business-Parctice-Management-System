package org.example.management.system.repository;

import org.example.management.system.model.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer>, JpaSpecificationExecutor<Attendance> {

    /**
     * 进行分页支持
     */
    Page<Attendance> findAllByUsernameStartingWithAndCreateAtBetween(String userName, long startTimeAt, long endTimeAt, Pageable pageable);

    Page<Attendance> findAllByCreateAtBetween(long startTimeAt, long endTimeAt, Pageable pageable);
}
