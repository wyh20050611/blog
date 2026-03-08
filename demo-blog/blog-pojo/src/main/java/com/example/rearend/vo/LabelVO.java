package com.example.rearend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author 34932
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "返回前端的集合")
@Builder
public class LabelVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("标签名")
    private String labelName;
    @ApiModelProperty("数量")
    private Integer count;

}
