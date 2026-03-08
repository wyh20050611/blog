package com.example.rearend.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 评论表
 * </p>
 *
 * @author author
 * @since 2024-08-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("evaluate")
@ApiModel(value = "Evaluate对象", description = "评论表")
public class Evaluate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "文章id")
    private Long writingId;

    @ApiModelProperty(value = "用户名")
    private String name;
    @ApiModelProperty(value = "用户头像")
    private String image;

    @ApiModelProperty(value = "被回复者id")
    private String toUserName;

    @ApiModelProperty(value = "被回复评论id")
    private Integer toEvaluateId;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


}
