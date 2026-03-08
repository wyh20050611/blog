import request from "@/utils/request.js";

//添加文章
export const saveFileService = (param,writingId) => {
    return request.post('/admin/writing/save/'+writingId, param);
}

//分页查询
export const pageService = (page) => {
    return request.get('/admin/writing/page?page=' + page);
}

//根据id查询文章
export const selectByWritingIdService=(id)=>{
    return request.get('/admin/writing/'+id);
}

//根据文章标题查询文章
export const selectByTitleService=(title)=>{
    return request.get('/admin/writing/title?title='+title);
}

//获取最热文章
export const selectHotWritingService=(title)=>{
    return request.get('/admin/writing/hot');
}

// 查询所有文章总数
export const selectWritingCountService=()=>{
    return request.get('/admin/writing/count');
}

