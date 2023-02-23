package org.example.management.system.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 字典
 */
@Table(name = "dict")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Dict implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String itemType;

    /**
     * 等于 0 表示顶级字典项
     */
    private Integer parentDataType;

    private String itemValue;

    private String createTimeStr;


    private Long createAt;

    private String updateTimeStr;

    private Long updateAt;

}
