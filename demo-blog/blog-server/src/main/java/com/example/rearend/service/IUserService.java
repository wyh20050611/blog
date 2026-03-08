package com.example.rearend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.rearend.dto.LoginDTO;
import com.example.rearend.dto.UserDTO;
import com.example.rearend.entity.User;
import com.example.rearend.vo.LoginVO;

/**
 * @author 34932
 */
public interface IUserService extends IService<User> {

    /**
     * 用户登录接口
     *
     * @param loginDTO 用户登录信息
     * @return LoginVO
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 新增用户
     *
     * @param userDTO 用户信息
     */
    boolean saveUser(UserDTO userDTO);

    /**
     * 根据邮箱重置用户名和密码
     *
     * @param userDTO 新的用户信息
     */
    void updateByEmail(UserDTO userDTO);
}
