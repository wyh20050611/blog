package com.example.rearend.query;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javassist.runtime.Inner;
import lombok.Data;

@Data
@ApiModel(description = "通用分页查询条件")
public class PageQuery {
    @ApiModelProperty("页码")
    private Integer pageNo;
    @ApiModelProperty("每页展示数")
    private Integer pageSize=10;
    @ApiModelProperty("排序字段")
    private String sortBy;
    @ApiModelProperty("是否升序")
    private Boolean isAsc;

    public <T> Page<T> toMpPage(Integer pageNo,OrderItem...items){
        //1、构建查询条件
        Page<T> page = Page.of(pageNo,pageSize);
        if(StrUtil.isNotBlank(sortBy)){
            page.addOrder(new OrderItem(sortBy,isAsc));
        }else{
            page.addOrder(items);
        }
        return page;
    }

    public <T> Page<T> toMpPage(String defalutSortBy,Boolean defalutAsc,Integer pageNo){
        return toMpPage(pageNo,new OrderItem(defalutSortBy,defalutAsc));
    }

    public <T> Page<T> toMpPageDefaultSortByCreateTime(Integer pageNo){
        return toMpPage(pageNo,new OrderItem("create_time",false));
    }

    public <T> Page<T> toMpPageSortByCreateTime(Integer pageNo){
        return toMpPage(pageNo,new OrderItem("create_time",true));
    }

    public <T> Page<T> toMpPageDefaultSortByUpdateTime(Integer pageNo){
        return toMpPage(pageNo,new OrderItem("update_time",false));
    }
}
