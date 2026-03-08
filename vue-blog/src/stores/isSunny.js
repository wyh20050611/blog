import { defineStore } from "pinia";
import { ref } from "vue";

export const useIsSunnyStore = defineStore('isSunny', () => {
    //1、定义状态
    const isSunny = ref('');
    //2、定义放入状态的方法
    const setSunny = (newIsSunny) => {
        isSunny.value = newIsSunny;
    }
    //3、定义清楚状态的方法
    const clearSunny = () => {
        isSunny.value = '';
    }
    //4、放回
    return { isSunny, setSunny, clearSunny };
}, {
    //数据持久化
    persist: true
})