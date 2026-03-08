package com.example.rearend.service.impl;

import com.example.rearend.entity.Star;
import com.example.rearend.mapper.StarMapper;
import com.example.rearend.service.IStarService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 点赞表 服务实现类
 * </p>
 *
 * @author author
 * @since 2024-08-25
 */
@Service
public class StarServiceImpl extends ServiceImpl<StarMapper, Star> implements IStarService {

}
