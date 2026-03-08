import request from "@/utils/request.js";

//根据文章id查询标签
export const selectByWritingIdService = (id) => {
    return request.get('/admin/label/' + id);
}

//查询所有的标签
export const selectALLService = () => {
    return request.get('/admin/label');
}

// 根据标签名分页查询文章
export const selectByLabelNameService = (labelName, page = 1, size = 10) => {
    return request.get(`/admin/label/labelName/page?labelName=${labelName}&page=${page}&size=${size}`);
}