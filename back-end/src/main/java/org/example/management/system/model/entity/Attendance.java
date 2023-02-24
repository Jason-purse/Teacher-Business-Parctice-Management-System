package org.example.management.system.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author FLJ
 * @date 2023/2/24
 * @time 9:22
 * @Description 考勤表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "attendance")
@Entity
public class Attendance implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    private String username;

    private String createTimeStr;

    private Long createAt;

    private String updateTimeStr;

    private Long updateAt;

}
