package org.example.management.system.service;

import lombok.RequiredArgsConstructor;
import org.example.management.system.model.entity.ReportRRAuditPhase;
import org.example.management.system.repository.ReportRRAuditPhaseRepository;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportRRAuditPhaseService {

    private final ReportRRAuditPhaseRepository reportRRAuditPhaseRepository;

    public void createReportAndAuditPhaseRRInfo(List<ReportRRAuditPhase> reportRRAuditPhases) {
        this.reportRRAuditPhaseRepository.saveAll(reportRRAuditPhases);
    }


    public List<ReportRRAuditPhase> getAllReportRRAuditPhasesByReportId(Integer reportId) {
        return this.reportRRAuditPhaseRepository.findAll(Example.of(ReportRRAuditPhase.builder()
                .reportId(reportId).build()));
    }

    public Optional<ReportRRAuditPhase> getReportRRAuditPhaseByReportId(Integer reportId, Integer auditPhaseId) {
        return this.reportRRAuditPhaseRepository.findOne(
                Example.of(ReportRRAuditPhase.builder()
                        .reportId(reportId)
                        .auditPhaseId(auditPhaseId)
                        .build())
        );
    }
}
