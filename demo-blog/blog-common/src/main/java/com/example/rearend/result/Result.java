package com.example.rearend.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 后端统一放回结果
 * @param <T>
 */
@Data
public class Result<T> implements Serializable {

    //0为成功，1和其他数字为失败
    private Integer code;

    //失败信息
    private String msg;

    //数据
    private T data;

    //成功方法
    public static <T> Result<T> success(){
        Result<T> result = new Result<>();
        result.code=0;
        return result;
    }

    public static <T> Result<T> success(T Object){
        Result<T> result = new Result<>();
        result.code=0;
        result.data=Object;
        return result;
    }

    //失败方法
    public static <T> Result<T> error(String msg){
        Result<T> result = new Result<>();
        result.code=1;
        result.msg=msg;
        return result;
    }
}
