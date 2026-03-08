// src/api/star.js
import request from "@/utils/request.js";

// 查询点赞状态
export const selectStatusService = (deviceId, writingId) => {
    return request.get('/admin/star?deviceId=' + deviceId + '&writingId=' + writingId);
}

// 点赞
export const addStarService = (starData) => {
    return request.post('/admin/star/add', starData);
}

// 取消点赞
export const decreaseStarService = (starData) => {
    return request.put('/admin/star/delete', starData);
}

// 查询点赞总数
export const selectStarCountService = (writingId) => {
    return request.get('/admin/star/count');
}