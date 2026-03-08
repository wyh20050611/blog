<!-- HomePage.vue -->
<script setup>
import {
  Calendar,
  Document,
  Star,
  ChatLineRound,
  FolderOpened
} from "@element-plus/icons-vue";
import { ref, watch, onMounted, computed } from "vue";
import { pageService } from "@/api/writing.js";
import router from "@/router/index.js";
import { useWritingIdStore } from "@/stores/writingId.js";
import { useRoute, useRouter } from 'vue-router';
import { v4 as uuidv4 } from 'uuid';
import { addStarService, decreaseStarService, selectStatusService } from "@/api/star.js";

const route = useRoute();
const router1 = useRouter();

// 分页数据模型
const page = ref(1);
const total = ref(0);
const pageSize = ref(10);

// 点赞相关数据 - 使用更安全的数据结构
const isStar = ref({});
const starCount = ref({});
const deviceId = ref('');

// 获取初始页面参数
const currentPage = ref(parseInt(route.query.page) || 1);

// 文章数据模型
const writingData = ref([]);

// 响应式屏幕检测
const isMobile = ref(window.innerWidth < 768);

// 监听窗口大小变化
window.addEventListener('resize', () => {
  isMobile.value = window.innerWidth < 768;
});

// 安全的属性访问辅助函数
const safeGet = (obj, key, defaultValue = null) => {
  return obj && obj[key] !== undefined ? obj[key] : defaultValue;
};

// 分页查询
const List = async () => {
  try {
    page.value = currentPage.value;
    const result = await pageService(page.value);

    // 检查响应数据是否存在
    if (!result || !result.data) {
      console.error('API响应数据异常:', result);
      writingData.value = [];
      total.value = 0;
      return;
    }

    // 添加页数验证逻辑
    const totalPages = Math.ceil(result.data.total / pageSize.value);
    if (totalPages > 0 && (page.value > totalPages || page.value < 1)) {
      currentPage.value = 1;
      page.value = 1;
      router1.push({
        query: { ...route.query, page: 1 }
      });
      const newResult = await pageService(page.value);
      processArticleData(newResult);
      return;
    }

    processArticleData(result);
  } catch (error) {
    console.error('获取文章列表失败:', error);
    writingData.value = [];
    total.value = 0;
  }
};

// 查询文章点赞状态
const selectStarStatus = async (writingId) => {
  if (!deviceId.value || !writingId) return;

  try {
    let result = await selectStatusService(deviceId.value, writingId);
    // 安全地设置点赞状态
    if (result && result.data !== undefined) {
      isStar.value[writingId] = result.data === 1;
    }
  } catch (error) {
    console.error('查询点赞状态失败:', error);
    isStar.value[writingId] = false;
  }
}

// 点赞方法
const addStar = async (writingId, currentStar) => {
  if (!deviceId.value || !writingId) return;

  try {
    const starData = {
      deviceId: deviceId.value,
      writingId: writingId
    };
    await addStarService(starData);
    isStar.value[writingId] = true;
    const currentCount = safeGet(starCount.value, writingId, currentStar);
    starCount.value[writingId] = currentCount + 1;
  } catch (error) {
    console.error("点赞失败:", error);
  }
}

// 取消点赞方法
const decreaseStar = async (writingId, currentStar) => {
  if (!deviceId.value || !writingId) return;

  try {
    const starData = {
      deviceId: deviceId.value,
      writingId: writingId
    };
    await decreaseStarService(starData);
    isStar.value[writingId] = false;
    const currentCount = safeGet(starCount.value, writingId, currentStar);
    starCount.value[writingId] = Math.max(0, currentCount - 1);
  } catch (error) {
    console.error("取消点赞失败:", error);
  }
}

// 处理点赞点击事件
const handleStarClick = async (writingId, currentStar = 0) => {
  if (!writingId) {
    console.error('writingId 为空');
    return;
  }

  // 确保对象属性存在
  if (isStar.value[writingId] === undefined) {
    isStar.value[writingId] = false;
  }

  if (starCount.value[writingId] === undefined) {
    starCount.value[writingId] = currentStar;
  }

  if (isStar.value[writingId]) {
    await decreaseStar(writingId, currentStar);
  } else {
    await addStar(writingId, currentStar);
  }
}

// 处理文章数据
const processArticleData = (result) => {
  // 检查数据是否存在
  if (!result || !result.data || !Array.isArray(result.data.records)) {
    console.error('文章数据格式异常:', result);
    writingData.value = [];
    return;
  }

  const processedRecords = [];

  for (let i = 0; i < result.data.records.length; i++) {
    const record = result.data.records[i];

    // 安全检查记录是否存在
    if (!record) continue;

    // 安全访问记录属性
    const recordId = safeGet(record, 'id');
    if (!recordId) {
      console.warn('文章记录缺少ID:', record);
      continue;
    }

    // 处理创建时间
    if (record.createTime) {
      try {
        const date = new Date(record.createTime);
        record.createTime = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
      } catch (error) {
        console.error('日期格式化失败:', error);
        record.createTime = '未知时间';
      }
    }

    // 处理点赞数
    record.star = safeGet(record, 'star', 0);

    // 处理标签
    if (record.labelName) {
      try {
        record.labelName = record.labelName
          .slice(1, record.labelName.length - 1)
          .split(', ')
          .map(tag => tag ? tag.trim() : '')
          .filter(tag => tag); // 过滤空标签
      } catch (error) {
        console.error('标签处理失败:', error);
        record.labelName = [];
      }
    } else {
      record.labelName = [];
    }

    // 初始化点赞数据
    starCount.value[recordId] = record.star;
    isStar.value[recordId] = false; // 默认设为false，后续查询会更新

    // 查询点赞状态
    selectStarStatus(recordId);

    processedRecords.push(record);
  }

  writingData.value = processedRecords;
  total.value = safeGet(result.data, 'total', 0);
};

// 初始化
onMounted(() => {
  // 如果 URL 中没有 page 参数，则添加 page=1
  if (!route.query.page) {
    router1.replace({
      query: { ...route.query, page: 1 }
    });
  }

  // 设备ID初始化
  let storedDeviceId = localStorage.getItem('deviceId');
  if (!storedDeviceId) {
    storedDeviceId = 'device_' + Math.random().toString(36).substr(2, 9);
    localStorage.setItem('deviceId', storedDeviceId);
  }
  deviceId.value = storedDeviceId;

  // 初始加载数据
  List();
});

// 进入文章
const writingIdStore = useWritingIdStore();
const LookWriting = (writingId) => {
  if (!writingId) {
    console.error('writingId 为空');
    return;
  }
  writingIdStore.clearWritingId();
  writingIdStore.setWritingId(writingId);
};

// 标签存储
import { useTagNameStore } from "@/stores/tagName";
const tagNameStore = useTagNameStore();
const setTagName = (label) => {
  if (label) {
    tagNameStore.setTagName(label);
  }
};

// 分页处理
const handleCurrentChange = (num) => {
  const totalPages = Math.ceil(total.value / pageSize.value);
  if (totalPages > 0 && (num > totalPages || num < 1)) {
    return;
  }

  currentPage.value = num;
  router1.push({
    query: { ...route.query, page: num }
  });
  List();
};

// 监听路由变化
watch(
  () => route.query,
  (newQuery) => {
    currentPage.value = parseInt(newQuery.page) || 1;
    List();
  }
);
</script>

<template>
  <div class="all_class">
    <div class="container">
      <div class="line"></div>
      <el-card class="contentClass">
        <div class="content_body">
          <!-- 添加空状态处理 -->
          <div v-if="writingData.length === 0" class="empty-state">
            暂无文章数据
          </div>

          <div v-else v-for="writing in writingData" :key="writing.id" class="article-item">
            <div class="article-content">
              <h3 class="article-title">
                <router-link class="dynamic-link" :to="`/lookWriting?id=${writing.id}`"
                  @click="LookWriting(writing.id)">
                  {{ writing.title || '无标题' }}
                </router-link>
              </h3>
              <p class="article-summary">{{ writing.summary || '暂无摘要' }}</p>

              <!-- 元信息区域 -->
              <div class="article-meta" :class="{ 'mobile-meta': isMobile }">
                <span class="meta-item">
                  <el-icon>
                    <Calendar />
                  </el-icon>
                  <span class="meta-text">发布于 {{ writing.createTime || '未知时间' }}</span>
                </span>

                <span class="meta-item">
                  <el-icon>
                    <Document />
                  </el-icon>
                  <span class="meta-text">{{ (writing.viewCount || 0) / 2 }} 阅读</span>
                </span>

                <span class="meta-item">
                  <el-icon :class="{ 'starred': isStar[writing.id] }">
                    <Star />
                  </el-icon>
                  <span @click="handleStarClick(writing.id, writing.star)" :class="{ 'starred': isStar[writing.id] }"
                    class="star-clickable meta-text">
                    {{ starCount[writing.id] || writing.star || 0 }} 点赞
                  </span>
                </span>
              </div>

              <!-- 标签区域 -->
              <div class="tags-container" :class="{ 'mobile-tags': isMobile }"
                v-if="writing.labelName && writing.labelName.length > 0">
                <span v-for="label in writing.labelName" :key="label" class="tag" :class="{ 'mobile-tag': isMobile }">
                  @{{ label }}
                </span>
              </div>
            </div>
          </div>

          <!-- 分页组件 -->
          <div class="pagination-custom" v-if="writingData.length > 0" :class="{ 'mobile-pagination': isMobile }">
            <button class="page-btn prev-btn" :class="{ active: page > 1 }" @click="handleCurrentChange(page - 1)"
              :disabled="page <= 1">
              上一页
            </button>
            <span class="page-info">第 {{ page }}/{{ Math.ceil(total / pageSize) || 1 }} 页</span>
            <button class="page-btn next-btn" @click="handleCurrentChange(page + 1)"
              :disabled="page >= Math.ceil(total / pageSize)">
              下一页
            </button>
          </div>
        </div>
        <el-backtop :right="isMobile ? 20 : 300" :bottom="isMobile ? 20 : 60" />
      </el-card>
    </div>
  </div>
</template>

<style scoped lang="less">
/* 添加空状态样式 */
.empty-state {
  text-align: center;
  padding: 2em;
  color: #666;
  font-size: 1.1em;
}

/* 其他样式保持不变 */
.all_class {
  width: 100%;
}

.container {
  color: #333;
  top: 1vw;
  text-align: center;
}

.contentClass {
  width: 100%;
  border-style: groove;
  border-color: #ddd;
  position: relative;

  .content_body {
    width: 100%;
    text-align: left;
    padding: 1em;
    position: relative;
  }

  .article-item {
    display: flex;
    gap: 1.5em;
    padding: 1em 0;
    border-bottom: 1px solid #eee;
    margin-bottom: 1em;
    transition: background-color 0.3s ease, box-shadow 0.3s ease;
    border-radius: 8px;
    overflow: hidden;
  }

  .article-item:hover {
    background-color: #f0f8ff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    border-bottom: 1px solid #d0e6f5;
  }

  .article-content {
    flex: 1;
    min-width: 0;
    padding-right: 2em;
  }

  .article-title {
    color: #0066cc;
    font-size: 1.4em;
    font-weight: bold;
    margin: 0 0 0.5em;
    transition: color 0.3s ease;
  }

  .article-title:hover {
    color: #004080;
  }

  .article-summary {
    color: #555;
    font-size: 1em;
    line-height: 1.5;
    margin: 0.5em 0;
    text-align: left;
    transition: color 0.3s ease;
  }

  .article-item:hover .article-summary {
    color: #333;
  }

  .article-meta {
    display: flex;
    gap: 1em;
    font-size: 0.8em;
    color: #666;
    margin: 0.5em 0;
  }

  .meta-item {
    display: flex;
    align-items: center;
    gap: 0.3em;
  }

  .tags-container {
    display: flex;
    justify-content: flex-start;
    gap: 0.5em;
    margin-top: 1em;
    font-size: 0.9em;
    color: #666;
    flex-wrap: wrap;
  }

  .tag {
    color: #666;
    cursor: default;
    padding: 0.2em 0.5em;
    background-color: #f5f5f5;
    border-radius: 4px;
    white-space: nowrap;
  }

  .dynamic-link {
    position: relative;
    font-size: 1.4em;
    text-decoration: none;
    transition: color 0.3s;
    padding: 10px;
    display: inline-block;
    border: 2px solid transparent;
    color: #0066cc;
  }

  .dynamic-link::before,
  .dynamic-link::after {
    content: '';
    position: absolute;
    width: 10%;
    height: 2px;
    background-color: #0066cc;
    opacity: 0;
    transition: opacity 0.3s, transform 0.3s;
  }

  .dynamic-link::before {
    top: 0;
    left: 0;
    transform: translateY(-100%);
  }

  .dynamic-link::after {
    bottom: 0;
    right: 0;
    transform: translateY(100%);
  }

  .dynamic-link:hover::before,
  .dynamic-link:hover::after {
    opacity: 1;
    transform: translateY(0);
  }

  .footer_class {
    width: 60vw;
    border-style: groove;
    border-color: #ffffff;
  }

  .line {
    width: 60vw;
    height: 0;
    border-top: 1px solid var(--el-border-color);
    --el-border-color: black;
  }

  .information_class {
    width: 15vw;
    left: 65vw;
  }

  .notice_class {
    width: 15vw;
    left: 65vw;
    top: 3vh;
  }
}

.pagination-custom {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1em;
  margin-top: 1.5em;
  padding: 0.5em 0;
}

.page-btn {
  padding: 0.5em 1em;
  border: none;
  border-radius: 20px;
  font-size: 1em;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.prev-btn {
  background-color: #e0e0e0;
  color: #666;
}

.prev-btn.active {
  background-color: #0066cc;
  color: white;
}

.next-btn {
  background-color: #0066cc;
  color: white;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 1em;
  color: #333;
  font-weight: 500;
}

/* 点赞状态样式 */
.starred {
  color: #ff6b6b;
  font-weight: bold;
}

.star-clickable {
  cursor: pointer;

  &:hover {
    cursor: pointer;
  }
}

.el-icon.starred {
  color: #ff6b6b;
}

/* 移动端样式 */
@media (max-width: 767px) {
  .contentClass {
    .content_body {
      padding: 0.5em;
    }

    .article-item {
      padding: 0.8em 0;
      gap: 1em;
    }

    .article-content {
      padding-right: 0;
    }

    .article-title {
      font-size: 1.2em;
      margin-bottom: 0.3em;
    }

    .article-summary {
      font-size: 0.9em;
      line-height: 1.4;
      margin: 0.3em 0;
    }

    .dynamic-link {
      font-size: 1.2em;
      padding: 5px;
    }
  }

  /* 移动端元信息样式 */
  .article-meta.mobile-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5em;
    font-size: 0.7em;
    margin: 0.3em 0;
  }

  .article-meta.mobile-meta .meta-item {
    display: flex;
    align-items: center;
    gap: 0.2em;
  }

  .meta-text {
    white-space: nowrap;
  }

  /* 移动端标签样式 */
  .tags-container.mobile-tags {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 0.3em;
    margin-top: 0.5em;
    font-size: 0.7em;
  }

  .tag.mobile-tag {
    text-align: center;
    padding: 0.2em 0.3em;
    font-size: 0.8em;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  /* 移动端分页样式 */
  .pagination-custom.mobile-pagination {
    gap: 0.5em;
    margin-top: 1em;
  }

  .page-btn {
    padding: 0.4em 0.8em;
    font-size: 0.9em;
  }

  .page-info {
    font-size: 0.9em;
  }

  .empty-state {
    padding: 1em;
    font-size: 1em;
  }
}

/* 平板端样式 */
@media (min-width: 768px) and (max-width: 1023px) {
  .contentClass {
    .article-title {
      font-size: 1.3em;
    }

    .article-summary {
      font-size: 0.95em;
    }

    .article-meta {
      font-size: 0.85em;
    }

    .tags-container {
      font-size: 0.85em;
    }
  }
}
</style>