package org.example.management.system.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 角色
 */
@Table(name = "role")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleRRU implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    private Integer roleId;

    private String createTimeStr;


    private Long createAt;

    private String updateTimeStr;

    private Long updateAt;
}
