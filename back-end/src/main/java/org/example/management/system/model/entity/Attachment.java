package org.example.management.system.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "attachment")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attachment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fileName;

    private String fileUrl;

    private String identifier;

    private String createTimeStr;

    private Integer mediaType;

    private Long createAt;

    private Long updateAt;

    private String updateTimeStr;
}
