import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css' //引入Element-plus的图标
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'  //pinia持久化数据
import 'element-plus/dist/index.css' //中文包
import 'viewerjs/dist/viewer.css'
import Particles from "vue3-particles";
import highlight from 'highlight.js';
import Viewer from 'v-viewer';
import 'viewerjs/dist/viewer.css';
import VueLazyload from 'vue-lazyload';
import '@fortawesome/fontawesome-free/css/all.css'

const app = createApp(App)
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)
app.use(router)
app.use(ElementPlus)
app.use(Particles)
app.use(Viewer)
app.use(highlight)
app.use(Viewer, {
    defaultOptions: {
        inline: false, //默认值：false。启用内联模式。
        button: true, //在查看器的右上角显示按钮。
        navbar: true, //指定导航栏的可见性。
        title: true, //指定标题的可见性和内容。
        toolbar: true, //指定工具栏及其按钮的可见性和布局。
        tooltip: true, //放大或缩小时显示带有图像比率（百分比）的工具提示。
        movable: true, //启用以移动图像。
        zoomable: true, //启用以缩放图像
        rotatable: true, //启用以旋转图像
        scalable: true, //启用以缩放图像。
        transition: true, //为某些特殊元素启用CSS3转换。
        fullscreen: true, //启用以在播放时请求全屏。
        keyboard: true, //启用键盘支持。
        // url: 'src',  //默认值：'src'。定义获取原始图像URL以供查看的位置。
    },
});
app.use(VueLazyload, {
    preLoad: 1.3,
    error: 'path/to/error-image.png',
    loading: 'path/to/loading-image.gif',
    attempt: 1,
});
app.mount('#app')
