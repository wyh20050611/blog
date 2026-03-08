<script setup>
import { ref, onMounted, watch } from "vue";
import { useRoute } from 'vue-router';
import { selectByLabelNameService } from "@/api/label.js";
import { useWritingIdStore } from "@/stores/writingId.js";
import router from "@/router/index.js";
import { addStarService, decreaseStarService, selectStatusService } from "@/api/star.js";
import { v4 as uuidv4 } from 'uuid';
import {
  Calendar,
  Document,
  Star,
  ChatLineRound,
} from "@element-plus/icons-vue";

const route = useRoute();
const writingData = ref([]);
const page = ref(1); // 当前页
const total = ref(0); // 总条数
const pageSize = ref(10); // 每页条数

// 响应式屏幕检测
const isMobile = ref(window.innerWidth < 768);

// 监听窗口大小变化
window.addEventListener('resize', () => {
  isMobile.value = window.innerWidth < 768;
});

// 点赞相关数据
const isStar = ref({});
const starCount = ref({});
const deviceId = ref('');

// 安全的属性访问辅助函数
const safeGet = (obj, key, defaultValue = null) => {
  return obj && obj[key] !== undefined ? obj[key] : defaultValue;
};

// 查询文章点赞状态
const selectStarStatus = async (writingId) => {
  if (!deviceId.value || !writingId) return;

  try {
    let result = await selectStatusService(deviceId.value, writingId);
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

// 修改 selectByLabelName 函数，在处理文章数据时初始化点赞数据
const selectByLabelName = async (labelName) => {
  const result = await selectByLabelNameService(labelName, page.value, pageSize.value);

  // 处理文章数据，添加格式化时间等
  for (let i = 0; i < result.data.records.length; i++) {
    const date = new Date(result.data.records[i].createTime);
    result.data.records[i].createTime = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;

    if (result.data.records[i].star === null) result.data.records[i].star = 0;
    if (result.data.records[i].labelName !== null) {
      result.data.records[i].labelName = result.data.records[i].labelName
        .slice(1, result.data.records[i].labelName.length - 1)
        .split(', ')
        .map(tag => tag.trim());
    }

    // 初始化点赞数据
    const recordId = result.data.records[i].id;
    starCount.value[recordId] = result.data.records[i].star;
    isStar.value[recordId] = false; // 默认设为false，后续查询会更新

    // 查询点赞状态
    selectStarStatus(recordId);
  }

  writingData.value = result.data.records;
  total.value = result.data.total;
};

// 分页处理
const handleCurrentChange = (num) => {
  page.value = num;
  const tagName = route.params.tagName;
  if (tagName) {
    selectByLabelName(tagName);
  }
};

// 组件挂载时初始化
onMounted(() => {
  // 设备ID初始化
  let storedDeviceId = localStorage.getItem('deviceId');
  if (!storedDeviceId) {
    storedDeviceId = 'device_' + Math.random().toString(36).substr(2, 9);
    localStorage.setItem('deviceId', storedDeviceId);
  }
  deviceId.value = storedDeviceId;

  const tagNameFromParams = route.params.tagName;
  if (tagNameFromParams) {
    selectByLabelName(tagNameFromParams);
  }
});

// 监听 params 中的 tagName 变化
watch(
  () => route.params.tagName,
  (newTagName) => {
    page.value = 1; // 切换标签时重置为第一页
    if (newTagName) {
      selectByLabelName(newTagName);
    }
  },
  { immediate: false }
);

const adjustWriting = (id) => {
  useWritingIdStore().setWritingId(id);
  router.push({
    path: '/lookWriting',
    query: {
      id: id
    }
  });
};
</script>

<template>
  <div class="tag-container">
    <el-card class="contentClass" shadow="hover">
      <div class="tag-content">
        <!-- 标签页头 -->
        <div class="tag-header">
          <h2>{{ route.params.tagName }}</h2>
          <p>共 {{ total }} 篇文章</p>
        </div>

        <!-- 文章列表 -->
        <div v-if="writingData.length > 0" class="content_body">
          <div v-for="writing in writingData" :key="writing.id" class="article-item">
            <div class="article-content">
              <h3 class="article-title">
                <router-link class="dynamic-link" @click="adjustWriting(writing.id)"
                  :to="`/lookWriting?id=${writing.id}`">
                  {{ writing.title }}
                </router-link>
              </h3>
              <p class="article-summary">{{ writing.summary }}</p>
              <div class="article-meta" :class="{ 'mobile-meta': isMobile }">
                <span class="meta-item">
                  <el-icon>
                    <Calendar />
                  </el-icon>
                  <span class="meta-text">发布于 {{ writing.createTime }}</span>
                </span>

                <span class="meta-item">
                  <el-icon>
                    <Document />
                  </el-icon>
                  <span class="meta-text">{{ (writing.viewCount) / 2 || 0 }} 阅读</span>
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
              <div class="tags-container" :class="{ 'mobile-tags': isMobile }">
                <span v-for="label in writing.labelName" :key="label" class="tag" :class="{ 'mobile-tag': isMobile }">
                  @{{ label }}
                </span>
              </div>
            </div>
          </div>

          <!-- 分页组件 - 与HomePage.vue保持一致 -->
          <div class="pagination-custom" :class="{ 'mobile-pagination': isMobile }">
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

        <div v-else class="no-data">暂无相关文章</div>
      </div>
    </el-card>
  </div>
</template>

<style scoped lang="less">
.tag-container {
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
}

.tag-content {
  padding: 20px;
  width: 100%;
}

.tag-header {
  margin-bottom: 20px;
  text-align: center;
}

.tag-header h2 {
  font-size: 1.8em;
  color: #0066cc;
}

.tag-header p {
  color: #666;
  font-size: 1em;
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

  .meta-text {
    white-space: nowrap;
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
}

/* 分页组件样式 - 与HomePage.vue保持一致 */
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

.no-data {
  text-align: center;
  padding: 40px;
  color: #999;
  font-size: 1.2em;
}

/* 移动端样式 */
@media (max-width: 767px) {
  .tag-container {
    padding: 0 10px;
  }
  
  .tag-content {
    padding: 15px;
  }
  
  .content_body {
    padding: 0.5em;
  }
  
  .article-item {
    flex-direction: column;
    gap: 0.8em;
    padding: 0.8em 0;
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
  
  .tag-header h2 {
    font-size: 1.5em;
  }
}

/* 超小屏幕优化 */
@media (max-width: 480px) {
  .article-meta.mobile-meta {
    font-size: 0.65em;
  }
  
  .tags-container.mobile-tags {
    font-size: 0.65em;
  }
  
  .tag.mobile-tag {
    font-size: 0.75em;
  }
  
  .tag-header h2 {
    font-size: 1.3em;
  }
  
  .tag-header p {
    font-size: 0.9em;
  }
}

/* 平板端样式 */
@media (min-width: 768px) and (max-width: 1023px) {
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
</style>