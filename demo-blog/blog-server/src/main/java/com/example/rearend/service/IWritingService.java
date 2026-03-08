package com.example.rearend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.rearend.dto.WritingPageDTO;
import com.example.rearend.entity.Writing;
import com.example.rearend.result.PageResult;
import com.example.rearend.vo.HotWritingVO;

import java.util.List;

/**
 * <p>
 * 文章表 服务类
 * </p>
 *
 * @author author
 * @since 2024-08-07
 */
public interface IWritingService extends IService<Writing> {

    /**
     * 查询文章id
     * @param name 文章名
     * @return Long
     */
    Long selectId(String name);

    /**
     * 分页查询
     * @param writingPageDTO 分页查询数据
     * @return PageResult
     */
    PageResult selectPage(WritingPageDTO writingPageDTO);

    /**
     * 查询最热文章
     * @return List<HotWritingVO>
     */
    List<HotWritingVO> selectHotWriting();
}
