package com.example.rearend.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "返回前端的集合")
@Builder
public class VisitorStatisticsVO {

    private Long totalUniqueVisitors;
    private Long totalVisits;

}
