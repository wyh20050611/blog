package com.example.rearend.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.rearend.entity.Evaluate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author 34932
 */
@ApiModel(description = "评论分层次数据")
@Data
public class EvaluatePageDTO {

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

    @ApiModelProperty("该评论下所属评论")
    private List<Evaluate> evaluates;
}
