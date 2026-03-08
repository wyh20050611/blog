import { ref } from 'vue';
import { defineStore } from 'pinia';

export const useUserIdStore = defineStore('id', () => {
    //定义用户id 
    const id = ref('');
    //定义放入用户id的方法
    const setUserId = (newUserId) => {
        id.value = newUserId;
    }
    //定义清楚用户id的方法
    const clearUserId = () => {
        id.value = '';
    }
    return { id, setUserId, clearUserId };
}, {
    //持久化存储数据
    persist: true
})