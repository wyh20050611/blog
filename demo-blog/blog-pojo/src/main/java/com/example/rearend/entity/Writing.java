package com.example.rearend.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.rearend.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 34932
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "文章表")
public class Writing implements Serializable{

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Long id;
    @ApiModelProperty("文章名")
    private String title;
    @ApiModelProperty("文章内容")
    private String content;
    @ApiModelProperty("点赞量")
    private Long star;
    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @ApiModelProperty("修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    @ApiModelProperty("标签名集合")
    private String labelName;
    @ApiModelProperty("文章摘要")
    private String summary;
    // 新增字段：记录文章被访问的次数
    @ApiModelProperty("访问次数")
    private Integer viewCount;
}
