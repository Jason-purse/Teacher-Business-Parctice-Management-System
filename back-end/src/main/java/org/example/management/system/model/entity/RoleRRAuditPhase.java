package org.example.management.system.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 角色和审核阶段的映射
 */
@Entity
@Table(name = "role_r_audit_phase")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleRRAuditPhase implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer roleId;

    private Integer auditPhaseDictId;

    private Long createAt;

    private String createTimeStr;

    private Long updateAt;

    private String updateTimeStr;
}
