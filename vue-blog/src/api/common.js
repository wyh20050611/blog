import request from "@/utils/request.js";

//图片上传接口
export const uploadImageService=(file)=>{
    return request.post('/admin/common/upload',file,{headers:{'Content-Type': 'multipart/form-data'}});
}