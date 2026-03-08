package com.example.rearend.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.rearend.dto.WritingPageDTO;
import com.example.rearend.entity.Writing;
import com.example.rearend.mapper.WritingMapper;
import com.example.rearend.result.PageResult;
import com.example.rearend.service.IWritingService;
import com.example.rearend.vo.HotWritingVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 文章表 服务实现类
 * </p>
 *
 * @author author
 * @since 2024-08-07
 */
@Service
@RequiredArgsConstructor
public class WritingServiceImpl extends ServiceImpl<WritingMapper, Writing> implements IWritingService {

    /**
     * 查询文章id
     *
     * @param name 文章名
     * @return Long
     */
    @Override
    public Long selectId(String name) {
        Writing writing = lambdaQuery().eq(Writing::getTitle, name).one();
        if (writing == null) {
            return 0L;
        }
        return writing.getId();
    }

    /**
     * 分页查询
     *
     * @param writingPageDTO 分页查询数据
     * @return PageResult
     */
    @Override
    public PageResult selectPage(WritingPageDTO writingPageDTO) {
        Integer pageNo = writingPageDTO.getPage();
        String titleName = writingPageDTO.getTitleName();
        Page<Writing> page = writingPageDTO.toMpPageDefaultSortByCreateTime(pageNo);
        Page<Writing> page1 = lambdaQuery()
                .like(titleName != null, Writing::getTitle, titleName)
                .page(page);
        return new PageResult(page1.getTotal(), page1.getRecords());
    }

    /**
     * 查询最热文章,按文章点赞量查询,查询点赞量最多的5篇文章,如果文章点赞量一样多，按创建时间晚的返回
     *
     * @return List<HotWritingVO>
     */
    @Override
    public List<HotWritingVO> selectHotWriting() {
        return lambdaQuery()
                .orderByDesc(Writing::getViewCount)
                .orderByDesc(Writing::getCreateTime)
                .last("LIMIT 5")
                .list()
                .stream()
                .map(writing -> {
                    HotWritingVO vo = new HotWritingVO();
                    BeanUtils.copyProperties(writing, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }
}
