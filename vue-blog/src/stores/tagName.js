import { ref } from 'vue';
import { defineStore } from 'pinia';

export const useTagNameStore = defineStore('tagName', () => {
    //定义文章id
    const tagName = ref('');
    //定义放入文章id的方法
    const setTagName = (newTagName) => {
        tagName.value = newTagName;
    }
    //定义清楚文章id的方法
    const clearTagName = () => {
        tagName.value = '';
    }
    return { tagName, setTagName, clearTagName };
}, {
    //持久化存储数据
    persist: true
})