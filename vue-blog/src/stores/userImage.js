import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useImageStore = defineStore('image', () => {
    //定义头像信息
    const image = ref('');
    //定义方法
    const setImage = (newImage) => {
        image.value = newImage;
    }
    const clearImage = () => {
        image.value = '';
    }
    return { image, setImage, clearImage }
}, {
    persist: true
})