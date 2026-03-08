package com.example.rearend.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult implements Serializable {

    //总记录数
    public Long total;
    //当前页集合
    public List records;
    /*//标签集合
    public List labels;*/
}
