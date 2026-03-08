import {ref} from 'vue';
import {defineStore} from 'pinia';

export const useWritingIdStore = defineStore('writingId', () => {
    //定义文章id
    const writingId = ref('');
    //定义放入文章id的方法
    const setWritingId = (newWritingId) => {
        writingId.value = newWritingId;
    }
    //定义清楚文章id的方法
    const clearWritingId = () => {
        writingId.value = '';
    }
    return {writingId, setWritingId, clearWritingId};
}, {
    //持久化存储数据
    persist: true
})