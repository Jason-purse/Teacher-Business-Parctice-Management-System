package org.example.management.system.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author FLJ
 * @date 2023/2/28
 * @time 15:48
 * @Description 记录 报告所具有的所有阶段的 指派的对应人 ..
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "report_rr_audit_phase")
public class ReportRRAuditPhase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer reportId;

    private Integer auditPhaseId;

    private Integer userId;

    private String userName;

    private Long createAt;

    private String createTimeStr;
}
