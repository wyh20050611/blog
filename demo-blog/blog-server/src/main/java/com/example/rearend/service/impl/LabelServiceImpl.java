package com.example.rearend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.rearend.entity.Label;
import com.example.rearend.entity.Writing;
import com.example.rearend.mapper.LabelMapper;
import com.example.rearend.mapper.WritingMapper;
import com.example.rearend.result.Result;
import com.example.rearend.service.ILabelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.rearend.vo.LabelVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 标签表 服务实现类
 * </p>
 *
 * @author author
 * @since 2024-08-07
 */
@Service
@RequiredArgsConstructor
public class LabelServiceImpl extends ServiceImpl<LabelMapper, Label> implements ILabelService {

    private final LabelMapper labelMapper;

    private final WritingMapper writingMapper;

    @Override
    public List<String> selectByWritingId(Long id) {
        QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<Label> wrapper = labelQueryWrapper.lambda().eq(Label::getWritingId, id);
        return labelMapper.selectList(wrapper).stream().map(Label::getLabelName).collect(Collectors.toList());
    }

    /**
     * 查询所有的标签
     *
     * @return List
     */
    @Override
    public HashSet<LabelVO> selectAll() {
        HashSet<LabelVO> label = new HashSet<>();
        List<Label> labelList = labelMapper.selectList(new QueryWrapper<>());
        for (Label label1 : labelList) {
            QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
            Integer i = labelMapper.selectCount(labelQueryWrapper.lambda().eq(Label::getLabelName, label1.getLabelName()));
            LabelVO labelVO = new LabelVO();
            labelVO.setLabelName(label1.getLabelName());
            labelVO.setCount(i);
            label.add(labelVO);
        }
        return label;
    }

    /**
     * 根据标签名查询文章id
     *
     * @param labelName 标签名
     * @return List
     */
    @Override
    public List<Writing> selectByLabelName(String labelName) {
        QueryWrapper<Label> wrapper = new QueryWrapper<>();
        List<Label> labelList = labelMapper.selectList(wrapper.lambda().eq(Label::getLabelName, labelName));

        List<Long> writingIds = labelList.stream()
                .map(Label::getWritingId)
                .collect(Collectors.toList());

        QueryWrapper<Writing> writingWrapper = new QueryWrapper<>();
        writingWrapper.in("id", writingIds)
                .orderByDesc("create_time");

        return writingMapper.selectList(writingWrapper);
    }


    @Override
    public Result<Page<Writing>> selectByLabelNameWithPage(String labelName, int page, int size) {
        // 创建分页对象
        Page<Writing> writingPage = new Page<>(page, size);

        // 查询标签
        QueryWrapper<Label> labelWrapper = new QueryWrapper<>();
        labelWrapper.lambda().eq(Label::getLabelName, labelName);
        List<Label> labelList = labelMapper.selectList(labelWrapper);

        if (labelList.isEmpty()) {
            return Result.success(writingPage);
        }

        // 获取文章ID列表
        List<Long> writingIds = labelList.stream()
                .map(Label::getWritingId)
                .collect(Collectors.toList());

        // 分页查询文章
        QueryWrapper<Writing> writingWrapper = new QueryWrapper<>();
        writingWrapper.lambda()
                .in(Writing::getId, writingIds)
                .orderByDesc(Writing::getCreateTime);

        Page<Writing> resultPage = writingMapper.selectPage(writingPage, writingWrapper);

        return Result.success(resultPage);
    }


}