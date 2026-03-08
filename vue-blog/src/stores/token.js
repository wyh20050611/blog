import { ref } from "vue";
import { defineStore } from "pinia";

export const useTokenStore = defineStore('token', () => {
    //定义token
    const token = ref('');
    //定义放入token的方法
    const setToken = (newToken) => {
        token.value = newToken;
    }
    //定义删除token的方法
    const clearToken = () => {
        token.value = '';
    }
    //放回
    return { token, setToken, clearToken };
}, {
    //持久化存储数据
    persist:true
})