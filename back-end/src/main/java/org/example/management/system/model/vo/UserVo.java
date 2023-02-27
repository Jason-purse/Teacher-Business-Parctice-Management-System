package org.example.management.system.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    private String description;

    /**
     * 头像地址 ...
     */
    private String avatarUrl;


    private String email;

    private String phone;

    private String nickname;

    private Integer gex;

    private String birthDay;


    private String createTimeStr;


    private Long createAt;

}
