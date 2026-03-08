package com.example.rearend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 34932
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "前端传给后端的点赞数据")
public class StarDTO {

    @ApiModelProperty("用户id")
    private String deviceId;
    @ApiModelProperty("文章id")
    private Long writingId;

}
