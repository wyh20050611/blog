import request from "@/utils/request.js";

//  查询网站访问量
export const selectVisitorCountService=()=>{
    return request.get('/admin/statistics/visitors');
}

// 查询网站运行天数
export const selectRunDaysService=()=>{
    return request.get('/admin/statistics/website');
}