package com.example.rearend.context;

/**
 * 基于ThreadLocal封装工具类，用户保存、获取和移除当前登录用户id
 */
public class BaseContext {

    public static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    /**
     * 设置用户id的方法
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取用户id的方法
     * @return 用户id
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }

    /**
     * 移除用户id
     */
    public static void removeCurrentId(){
        threadLocal.remove();
    }
}
