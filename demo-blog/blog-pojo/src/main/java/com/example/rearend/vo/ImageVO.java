package com.example.rearend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 34932
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "返回前端的图片信息")
@Builder
public class ImageVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("图片")
    private String image;
    @ApiModelProperty("创建时间")
    private Date timestamp;
}
