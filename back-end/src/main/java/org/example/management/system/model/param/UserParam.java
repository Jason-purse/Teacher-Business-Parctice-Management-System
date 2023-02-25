package org.example.management.system.model.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("用户参数实体")
public class UserParam {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("用户名称")
    private String username;

    @ApiModelProperty("用户密码")
    private String password;

    @ApiModelProperty("用户个人介绍")
    private String description;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("电话")
    private String phone;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("性别")
    private String gex;

    @ApiModelProperty("生日")
    private String birthDay;


    @ApiModelProperty("注册时间开始区间")
    private Long startTimeAt;

    @ApiModelProperty("注册时间结束区间")
    private Long endTimeAt;

}
