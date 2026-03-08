package com.example.rearend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "最热文章数据")
@Data
public class HotWritingVO {

    @ApiModelProperty("主键")
    private Long id;
    @ApiModelProperty("文章名")
    private String title;
    @ApiModelProperty("点赞量")
    private Long star;

}
