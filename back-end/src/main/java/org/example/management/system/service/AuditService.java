package org.example.management.system.service;

import lombok.RequiredArgsConstructor;
import org.example.management.system.repository.ProjectRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final ProjectRepository projectRepository;


}
