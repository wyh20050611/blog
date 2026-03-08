<script setup>
import {
  ChatDotRound,
  Link
} from '@element-plus/icons-vue'
import { useWritingIdStore } from "@/stores/writingId.js";
import { selectByWritingIdService } from "@/api/writing.js";
import Vditor from 'vditor'
import 'vditor/dist/index.css'
import { ref, onMounted, watch, onUnmounted, computed } from "vue";
import { ElMessage } from "element-plus";
import { useUserIdStore } from "@/stores/userId.js";
import { addStarService, decreaseStarService } from "@/api/star.js";
import { MdEditor } from "md-editor-v3";
import 'md-editor-v3/lib/style.css';
import { saveFileService } from "@/api/writing.js";
import { uploadImageService } from "@/api/common.js";
import '@fortawesome/fontawesome-free/css/all.css'
// 引入 clipboard.js
import ClipboardJS from 'clipboard';

const writingIdStore = useWritingIdStore();
const loading = ref(true)
const mdContent = ref("");
//评论总数量
const total = ref(0);
const starCount = ref(0);
const records = ref();
// 引入目录组件
import PostToc from '@/components/PostToc.vue'

// 目录组件引用
const postTocRef = ref(null)

// 添加移动端检测
const isMobile = ref(window.innerWidth < 768);

// 监听窗口大小变化
window.addEventListener('resize', () => {
  isMobile.value = window.innerWidth < 768;
});

const renderMarkdown = async (md) => {
  const previewElement = document.getElementById("preview");

  loading.value = true;

  try {
    await Vditor.preview(document.getElementById("preview"), md, {
      hljs: { style: "github" },
      cdn: window.location.origin,
      markdown: {
        toc: false,
        mark: true,
        footnotes: true,
        autoSpace: true,
      },
      math: {
        engine: "KaTeX",
        inlineDigit: true,
      },
      hljs: {
        style: "github",
        lineNumber: true,
      },
      anchor: 2,
      lang: "zh_CN",
      theme: {
        current: "light",
      },
      transform: (html) => {
        return html.replace(
          /<img\s+[^>]*src="([^"]+)"\s*alt="([^"]*)"[^>]*>/g,
          (match, src, altText) => {
            // 如果是相对路径，拼接为完整路径
            let newSrc = src.startsWith('http') ? src : `./${src}`;

            // 添加居中样式和题注
            const imgWithCaption = `
        <div style="text-align: center;">
          <img src="${newSrc}" class="center-image" alt="${altText}">
          <p class="caption">${altText}</p>
        </div>
      `;
            return imgWithCaption;
          }
        );
      },
    });
  } catch (error) {
    console.error("Vditor preview error:", error);
  } finally {
    loading.value = false;
  }
};

// 添加目录可见性控制
const tocVisible = ref(false)

// 在渲染完成后检查目录内容
const checkTocVisibility = () => {
  setTimeout(() => {
    const contentElement = document.querySelector('#preview')
    if (contentElement) {
      const headings = contentElement.querySelectorAll('h1, h2, h3')
      tocVisible.value = headings.length > 0
    }
  }, 500)
}

// 在 selectWriting 方法中调用检查
const selectWriting = async () => {
  loading.value = true;
  try {
    let result = await selectByWritingIdService(writingIdStore.writingId);
    starCount.value = result.data.star;
    mdContent.value = result.data.content;
    await renderMarkdown(mdContent.value);
    checkTocVisibility(); // 添加这行
  } catch (error) {
    console.error("Error fetching writing:", error);
  } finally {
    loading.value = false;
  }
}

//根据文章id查询文章下评论
const selectCount = async () => {

}
selectCount();

const isStar = ref(false);

const starData = ref({
  "deviceId": '',
  "writingId": ''
})

// 在文件顶部导入相关模块后添加
import { v4 as uuidv4 } from 'uuid';

import { selectStatusService } from "@/api/star.js";

// 在其他 ref 声明后添加
const deviceId = ref('');
// 修改 selectStarStatus 方法
const selectStarStatus = async () => {
  // 确保 deviceId 和 writingId 都存在
  if (!deviceId.value || !writingIdStore.writingId) {
    console.warn('设备ID或文章ID缺失，无法查询点赞状态');
    return;
  }

  try {
    let result = await selectStatusService(deviceId.value, writingIdStore.writingId);
    if (result.data === 1) {
      isStar.value = true;
    } else {
      isStar.value = false;
    }
  } catch (error) {
    console.error('查询点赞状态失败:', error);
    // 可以选择设置默认状态
    isStar.value = false;
  }
}

// 修改 addStar 方法
// 修改 addStar 方法
const addStar = async () => {
  // 参数验证
  if (!deviceId.value || !writingIdStore.writingId) {
    ElMessage.error("参数不完整，无法点赞");
    console.warn('点赞参数缺失: deviceId=', deviceId.value, 'writingId=', writingIdStore.writingId);
    return;
  }

  try {
    starData.value.writingId = writingIdStore.writingId;
    starData.value.deviceId = deviceId.value;
    await addStarService(starData.value);
    // 刷新点赞状态
    await selectStarStatus();
    starCount.value++;
  } catch (error) {
    ElMessage.error("点赞失败，请稍后再试");
    console.error("点赞失败:", error);
  }
}

// 修改 decreaseStar 方法
const decreaseStar = async () => {
  // 参数验证
  if (!deviceId.value || !writingIdStore.writingId) {
    ElMessage.error("参数不完整，无法取消点赞");
    console.warn('取消点赞参数缺失: deviceId=', deviceId.value, 'writingId=', writingIdStore.writingId);
    return;
  }

  try {
    starData.value.writingId = writingIdStore.writingId;
    starData.value.deviceId = deviceId.value;
    await decreaseStarService(starData.value);
    // 刷新点赞状态
    await selectStarStatus();
    starCount.value--;
  } catch (error) {
    ElMessage.error("取消点赞失败，请稍后再试");
    console.error("取消点赞失败:", error);
  }
}

const updateWriting = ref(false);

import 'md-editor-v3/lib/style.css';

const writingData = ref({
  content: '',
  name: '',
  types: new Array(5)
})

//文档上传方法
const codeSave = async () => {
  if (writingData.value.content === '' || writingData.value.name === '') {
    ElMessage.error("标题、内容或标签为空");
    return;
  }
  let result = await saveFileService(writingData.value, writingIdStore.writingId);
  if (result.data.msg === '文章上传失败') {
    ElMessage.error("文章上传失败");
    return;
  }
  clearData();
  ElMessage.success("文档上传并保存成功");
}

//图片上传方法
const onUploadImg = async (files, callback) => {
  const res = await Promise.all(
    Array.from(files).map((file) => {
      return new Promise(async (rev) => {
        const form = new FormData();
        form.append('file', file);
        await uploadImageService(form).then((res) => {
          rev(res)
        })
      });
    })
  );
  callback(res.map((item) => item.data));
}

//清除数据方法
const clearData = () => {
  writingData.value = {
    content: '',
    name: '',
    types: []
  }
}

const drawerSize = ref('50%');

// 初始化 drawerSize
const updateDrawerSize = () => {
  drawerSize.value = window.innerWidth < 700 ? '100%' : '50%';
};

// 监听窗口大小变化
window.addEventListener('resize', updateDrawerSize);

// 在组件卸载时移除事件监听器
onUnmounted(() => {
  window.removeEventListener('resize', updateDrawerSize);
});

import { useRoute } from 'vue-router'
const route = useRoute()

watch(
  () => route.query.id,
  (newId) => {
    if (newId) {
      writingIdStore.setWritingId(newId);
      selectWriting();
      selectCount();
      // 确保 deviceId 已经生成后再查询点赞状态
      if (deviceId.value) {
        selectStarStatus();
      } else {
        // 如果 deviceId 还未生成，延迟执行
        setTimeout(() => {
          if (deviceId.value) {
            selectStarStatus();
          }
        }, 200);
      }
    }
  },
  { immediate: true }
)

// 添加滚动监听来控制目录位置
const tocTop = ref(90)
const handleScroll = () => {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop
  // 当滚动超过一定距离时，调整目录位置
  if (scrollTop > 100) {
    tocTop.value = 50
  } else {
    tocTop.value = 90
  }
}

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  window.removeEventListener('resize', updateDrawerSize);
})

// 添加综合指纹生成方法
const generateComprehensiveFingerprint = () => {
  // 收集多种设备信息
  const components = [
    { key: 'user_agent', value: navigator.userAgent },
    { key: 'language', value: navigator.language },
    { key: 'color_depth', value: screen.colorDepth },
    { key: 'pixel_ratio', value: window.devicePixelRatio },
    { key: 'screen_resolution', value: `${screen.width}x${screen.height}` },
    { key: 'timezone_offset', value: new Date().getTimezoneOffset() },
    { key: 'session_storage', value: typeof window.sessionStorage !== 'undefined' },
    { key: 'local_storage', value: typeof window.localStorage !== 'undefined' },
    { key: 'indexed_db', value: typeof window.indexedDB !== 'undefined' },
    { key: 'cpu_class', value: navigator.cpuClass || 'unknown' },
    { key: 'platform', value: navigator.platform },
    { key: 'do_not_track', value: navigator.doNotTrack },
    { key: 'plugins', value: Array.from(navigator.plugins, p => p.name).join(',') }
  ];

  // 生成指纹字符串
  const fingerprintString = components
    .map(component => `${component.key}=${component.value}`)
    .join(';;');

  // 简单哈希函数
  let hash = 0;
  for (let i = 0; i < fingerprintString.length; i++) {
    const char = fingerprintString.charCodeAt(i);
    hash = ((hash << 5) - hash) + char;
    hash = hash & hash;
  }

  return Math.abs(hash).toString(36);
};

// 修改 onMounted 中的设备ID生成逻辑
onMounted(() => {
  // 尝试从 localStorage 获取 deviceId
  let storedDeviceId = localStorage.getItem('deviceId');

  if (!storedDeviceId) {
    // 生成综合指纹
    const fingerprint = generateComprehensiveFingerprint();
    storedDeviceId = 'device_' + fingerprint;
    localStorage.setItem('deviceId', storedDeviceId);
  }

  deviceId.value = storedDeviceId;

  // 处理路由参数
  if (route.query.id) {
    writingIdStore.setWritingId(route.query.id);
  }

  // 初始化各种功能
  selectWriting();
  updateDrawerSize();

  // 添加滚动监听
  window.addEventListener('scroll', handleScroll);

  // 初始化移动端检测
  isMobile.value = window.innerWidth < 768;
});
</script>

<template>
  <br>
  <div class="all_class">
    <div class="md_class" v-if="updateWriting">
      <el-input clearable placeholder="文章标题" v-model="writingData.name"></el-input>
      <el-input clearable placeholder="文章所属标签" v-model="writingData.types[0]" class="label_class"></el-input>
      <el-input clearable placeholder="文章所属标签" v-model="writingData.types[1]" class="label_class"></el-input>
      <el-input clearable placeholder="文章所属标签" v-model="writingData.types[2]" class="label_class"></el-input>
      <el-input clearable placeholder="文章所属标签" v-model="writingData.types[3]" class="label_class"></el-input>
      <el-input clearable placeholder="文章所属标签" v-model="writingData.types[4]" class="label_class"></el-input>
      <MdEditor toolbarsExclude="['github']" :showCodeRowNumber='true' v-model="writingData.content"
        @onUploadImg="onUploadImg" @onSave="codeSave">
      </MdEditor>
    </div>
    <div v-else class="article-layout">
      <!-- 文章内容区域 -->
      <div class="article-content">
        <el-card v-loading="loading" element-loading-text="正在加载中，请耐心等待..." class="el-card-preview">
          <div id="preview" class="preview-content"></div>
        </el-card>
      </div>

      <!-- 修改目录容器，只在非移动端显示 -->
      <div class="toc-fixed-container" v-if="tocVisible && !isMobile" :style="{ top: tocTop + 'px' }">
        <PostToc ref="postTocRef" content-selector="#preview" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.all_class {
  width: 100%;
  max-width: 1600px;
  margin: 0 auto;
  padding: 0 20px;
  box-sizing: border-box;
}

.article-layout {
  display: flex;
  gap: 40px;
  align-items: flex-start;
  position: relative;
}

.article-content {
  flex: 1;
  min-width: 0;
  max-width: calc(100% - 210px);
  box-sizing: border-box;
}

/* 目录固定容器 - 使用固定定位 */
.toc-fixed-container {
  width: 370px;
  position: fixed;
  right: 30px;
  max-height: calc(100vh - 120px);
  overflow-x: hidden;
  overflow-y: auto;
  z-index: 100;
  transition: top 0.3s ease;
}

/* 设置默认高度 */
.el-card-preview {
  min-height: 200px;
  width: 100%;
  box-sizing: border-box;
  background: #ffffff;
}

/* 预览内容区域 */
.preview-content {
  width: 100%;
  line-height: 1.6;
}

/* 响应式调整 */
@media (max-width: 1600px) {
  .all_class {
    max-width: 1400px;
  }

  .article-content {
    max-width: calc(100% - 280px);
  }
}

@media (max-width: 1400px) {
  .all_class {
    max-width: 1200px;
  }

  .toc-fixed-container {
    right: 20px;
  }

  .article-content {
    max-width: calc(100% - 280px);
  }
}

@media (max-width: 1280px) {
  .all_class {
    max-width: 1100px;
  }

  .toc-fixed-container {
    right: 15px;
    width: 240px;
  }

  .article-content {
    max-width: calc(100% - 260px);
  }
}

/* 平板设备 */
@media (max-width: 1024px) {
  .article-layout {
    flex-direction: column;
    gap: 20px;
  }

  .article-content {
    max-width: 100%;
    padding: 0;
  }

  .toc-fixed-container {
    position: relative;
    top: 0 !important;
    right: 0;
    width: 100%;
    max-height: none;
    order: -1;
    padding: 0;
  }
}

/* 移动端样式 - 重点优化 */
@media (max-width: 767px) {
  .all_class {
    padding: 0 8px;
    /* 减少两侧内边距，增加内容区域宽度 */
    max-width: 100%;
    margin: 0 auto;
  }

  .article-layout {
    gap: 10px;
    width: 100%;
  }

  .article-content {
    max-width: 100%;
    width: 100%;
    padding: 0;
  }

  .toc-fixed-container {
    display: none;
  }

  .el-card-preview {
    padding: 12px 8px;
    /* 减少卡片内边距 */
    margin: 0;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    width: 100%;
    min-height: auto;
  }

  /* 确保预览内容本身有适当的内边距和更好的阅读体验 */
  .preview-content {
    padding: 0 4px;
    font-size: 16px;
    /* 增加基础字体大小 */
    line-height: 1.7;
    /* 增加行高提升可读性 */
  }
}

/* 小屏幕手机 */
@media (max-width: 480px) {
  .all_class {
    padding: 0 6px;
  }

  .el-card-preview {
    padding: 10px 6px;
    border-radius: 6px;
  }

  .preview-content {
    padding: 0 2px;
    font-size: 15px;
  }
}

/* 超小屏幕设备 */
@media (max-width: 360px) {
  .all_class {
    padding: 0 4px;
  }

  .el-card-preview {
    padding: 8px 4px;
  }

  .preview-content {
    font-size: 14px;
    line-height: 1.65;
  }
}

/* 其他样式保持不变 */
.md_class {
  position: relative;
  width: 100%;
}

@media screen and (max-width: 1000px) {
  .md_class {
    position: relative;
    width: 100%;
  }
}

.label_class {
  width: 14vw;
}

@media screen and (max-width: 1000px) {
  .label_class {
    width: 17.5vw;
  }
}

.el-aside {
  width: 3vw;
}

@media screen and (max-width: 750px) {
  .el-aside {
    width: 12vw;
  }
}

@media screen and (min-width: 750px) and (max-width: 1000px) {
  .el-aside {
    width: 6vw;
  }
}

@media screen and (min-width: 1000px) and (max-width: 1350px) {
  .el-aside {
    width: 7vw;
  }
}

@media screen and (min-width: 1350px) {
  .el-aside {
    width: 7vw;
  }
}

.input_class {
  background: #ffffff;
  border: 1px solid #3f3f3f;
}

/* 确保所有元素都使用 border-box 模型 */
* {
  box-sizing: border-box;
}
</style>

<style>
/* 全局样式，用于优化移动端 Markdown 内容显示 */
@media (max-width: 767px) {
  #preview {
    font-size: 16px;
    line-height: 1.7;
    max-width: 100%;
    overflow-x: hidden;
  }

  #preview * {
    max-width: 100%;
    word-wrap: break-word;
  }

  /* 优化代码块显示 */
  #preview pre {
    margin: 12px 0;
    padding: 12px;
    border-radius: 6px;
    overflow-x: auto;
    font-size: 14px;
  }

  /* 优化表格显示 */
  #preview table {
    font-size: 14px;
    display: block;
    overflow-x: auto;
  }

  /* 优化图片显示 */
  #preview img {
    max-width: 100%;
    height: auto;
    display: block;
    margin: 0 auto;
  }

  /* 优化标题显示 */
  #preview h1 {
    font-size: 1.5em;
    margin: 20px 0 12px 0;
  }

  #preview h2 {
    font-size: 1.3em;
    margin: 18px 0 10px 0;
  }

  #preview h3 {
    font-size: 1.2em;
    margin: 16px 0 8px 0;
  }

  /* 优化段落和列表 */
  #preview p {
    margin: 12px 0;
    text-align: justify;
  }

  #preview ul,
  #preview ol {
    padding-left: 20px;
    margin: 12px 0;
  }

  #preview li {
    margin: 6px 0;
  }

  /* 优化引用块 */
  #preview blockquote {
    margin: 12px 0;
    padding: 10px 12px;
    border-left: 4px solid #ddd;
    background: #f9f9f9;
  }

  /* 优化代码内联 */
  #preview code:not(pre code) {
    padding: 2px 4px;
    font-size: 0.9em;
    border-radius: 3px;
    background: #f5f5f5;
  }
}

@media (max-width: 480px) {
  #preview pre {
    padding: 10px;
    font-size: 13px;
  }

  #preview h1 {
    font-size: 1.4em;
  }

  #preview h2 {
    font-size: 1.25em;
  }
}
</style>