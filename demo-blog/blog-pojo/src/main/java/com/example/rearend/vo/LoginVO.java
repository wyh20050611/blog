package com.example.rearend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 34932
 */
@Data
@ApiModel(description = "返回前端的登录信息")
@Builder
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户id")
    private Long id;
    @ApiModelProperty("jwt令牌")
    private String token;
}
