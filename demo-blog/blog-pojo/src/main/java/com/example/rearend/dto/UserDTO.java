package com.example.rearend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 前端传入后端的用户信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "前端传入后端的用户信息")
public class UserDTO implements Serializable {
    @ApiModelProperty("主键id")
    private Long id;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("用户密码")
    private String password;
    @ApiModelProperty(value = "头像", allowEmptyValue = true)
    private String image;
    @ApiModelProperty("在线状态")
    private Long status;
    @ApiModelProperty("qq邮箱")
    private String email;
    @ApiModelProperty("redis缓存的主键id")
    private String emailId;
    @ApiModelProperty("前端传入的验证码")
    private Integer foreEndCode;
}
