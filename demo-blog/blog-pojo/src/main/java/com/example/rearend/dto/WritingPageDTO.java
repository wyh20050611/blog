package com.example.rearend.dto;

import com.example.rearend.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author 34932
 */
@Data
@ApiModel("文章分页查询传递参数")
@Builder
public class WritingPageDTO extends PageQuery {
    @ApiModelProperty("页码")
    private Integer page;
    @ApiModelProperty("文章标题名")
    private String titleName;
}
