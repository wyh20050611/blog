package com.example.rearend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.rearend.entity.Label;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.rearend.entity.Writing;
import com.example.rearend.result.Result;
import com.example.rearend.vo.LabelVO;

import java.util.HashSet;
import java.util.List;

/**
 * <p>
 * 标签表 服务类
 * </p>
 *
 * @author author
 * @since 2024-08-07
 */
public interface ILabelService extends IService<Label> {

    /**
     *
     * @param id
     * @return
     */
    List<String> selectByWritingId(Long id);

    /**
     * 查询所有的标签
     * @return List
     */
    HashSet<LabelVO> selectAll();

    /**
     * 根据标签名查询文章id
     * @param labelName 标签名
     * @return List
     */
    List<Writing> selectByLabelName(String labelName);

    /**
     * 根据标签名分页查询文章
     * @param labelName 标签名
     * @param page 当前页码
     * @param size 每页大小
     * @return 分页结果
     */
    Result<Page<Writing>> selectByLabelNameWithPage(String labelName, int page, int size);


}
