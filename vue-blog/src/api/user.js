import request from "@/utils/request.js";

//登录请求
export const loginService=(loginData)=>{
    return request.post('/admin/user/login',loginData);
}

//注册
export const registerService=(registerData)=>{
    return request.post('/admin/user/save',registerData);
}

//重置用户信息
export const resetUserInformation=(newUserDTO)=>{
    return request.post('/admin/user/reset',newUserDTO);
}

//退出登录
export const logoutService=(id)=>{
    return request.put('/admin/user/logout/'+id);
}

//根据用户id查询信息
export const selectByUserIdService=(id)=>{
    return request.get('/admin/user/'+id);
}

//根据id修改用户信息接口
export const updateByIdService = (userData) => {
    return request.put('/admin/user/update', userData);
}