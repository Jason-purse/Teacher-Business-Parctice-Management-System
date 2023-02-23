package org.example.management.system.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name= "people")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String password;

    private String description;

    private String email;

    private String phone;

    private String nickname;

    private String gex;

    private String birthDay;


    private String createTimeStr;


    private Long createAt;

    private String updateTimeStr;

    private Long updateAt;

}
