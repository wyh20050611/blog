//定制请求
//导入axios和ElMessage
import axios from "axios";
import {ElMessage} from "element-plus";

//定义一个公共前缀
const baseURL = '/api';
const instance = axios.create({baseURL});

//添加请求拦截器
import {useTokenStore} from '@/stores/token.js'

instance.interceptors.request.use(
    (config) => {
        //请求前的回调函数
        //添加token
        const tokenStore = useTokenStore();
        //判断有没有token
        if (tokenStore.token) {
            config.headers.Authorization = tokenStore.token;
        }
        return config;
    },
    (err) => {
        //请求错误的回调
        Promise.reject(err);
    }
)

//添加响应拦截器
import router from '@/router';
import {useUserIdStore} from "@/stores/userId.js";
import {useImageStore} from "@/stores/userImage.js";
import {usePasswordStore} from "@/stores/password.js";


instance.interceptors.response.use(
    result => {
        //成功的回调
        if (result.data.code === 0) {
            return result.data;
        }
        //失败的回调
        ElMessage.error(result.data.msg ? result.data.msg : '服务异常');
        return Promise.reject(result.data);
    },
    err => {
        //判断响应状态码,如果为401，则为登录，跳转到登录页码
        if (err.response.status === 401) {
            router.push('/homePage');
            const userIdStore = useUserIdStore();
            const imageStore = useImageStore();
            const passwordStore = usePasswordStore();
            const tokenStore = useTokenStore();
            tokenStore.clearToken();
            userIdStore.clearUserId();
            imageStore.clearImage();
            passwordStore.clearPassword();
            setTimeout(()=>{
                window.location.reload();
            },500);
            ElMessage.error('登录已超时，请先完成登录');
        } else {
            ElMessage.error('服务异常');
        }
        return Promise.reject(err);//异步的状态转化成失败的状态
    }
)

export default instance;