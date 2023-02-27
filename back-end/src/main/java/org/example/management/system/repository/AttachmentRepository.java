package org.example.management.system.repository;

import org.example.management.system.model.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AttachmentRepository extends JpaRepository<Attachment,Integer>, JpaSpecificationExecutor<Attachment> {
}
