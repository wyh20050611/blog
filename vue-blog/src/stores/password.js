import { defineStore } from "pinia";
import { ref } from "vue";

export const usePasswordStore = defineStore('password', () => {
    //1、定义密码
    const password = ref('');
    //2、定义放入密码的方法
    const setPassword = (newPassword) => {
        password.value = newPassword;
    }
    //3、定义异常密码的方法
    const clearPassword = () => {
        password.value = '';
    }
    //4、放回
    return { password, setPassword, clearPassword };
}, {
    //数据持久化
    persist: true
})