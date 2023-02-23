package org.example.management.system.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 项目表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "project")
@Entity
public class Project implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    /**
     * project description
     */
    private String description;

    /**
     * 项目状态
     * started - running - finish
     * 启动 / 进行中 / 完成
     */
    private String status;


    private String createTimeStr;


    private Long createAt;

    private String updateTimeStr;

    private Long updateAt;

}
