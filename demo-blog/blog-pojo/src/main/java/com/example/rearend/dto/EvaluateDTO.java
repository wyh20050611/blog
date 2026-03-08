package com.example.rearend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 34932
 */
@ApiModel(description = "评论数据")
@Data
public class EvaluateDTO {

    @ApiModelProperty("文章id")
    private Long writingId;
    @ApiModelProperty("用户名")
    private String name;
    @ApiModelProperty("被回复者的id")
    private String toUserName;
    @ApiModelProperty("评论内容")
    private String content;
    @ApiModelProperty(value = "被回复评论id")
    private Integer toEvaluateId;

}
