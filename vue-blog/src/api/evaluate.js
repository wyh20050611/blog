import request from "@/utils/request.js";



//查询关于下评论信息
export const selectAboutEvaluateCountService = () => {
    return request.get('/admin/evaluate/about');
}

// 查询评论总数
export const selectEvaluateTotalCountService = () => {
    return request.get('/admin/evaluate/count');
}