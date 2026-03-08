package com.example.rearend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 34932
 */
@Data
@ApiModel(description = "用户登录信息")
public class LoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("密码")
    private String password;

}
