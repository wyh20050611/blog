<script setup>
import {
  CaretBottom,
  User,
  SwitchButton,
  Search,
  House,
  Remove,
  Discount,
  Collection,
  ScaleToOriginal,
  Sunny,
  Moon,
  FolderOpened,
  Document,
  ChatLineSquare,
  Calendar, Clock,
  View,
  Star
} from '@element-plus/icons-vue'
// 添加菜单图标
import { Menu } from '@element-plus/icons-vue'
import avatar from '@/assets/img_2.jpg';
import { ref, onMounted, watch, computed } from "vue";
import router from "@/router/index.js";
import { logoutService, selectByUserIdService } from "@/api/user.js";
import { useUserIdStore } from "@/stores/userId.js";
import { ElMessage } from "element-plus";
import { useImageStore } from "@/stores/userImage.js";
import { usePasswordStore } from "@/stores/password.js";
import { useWritingIdStore } from "@/stores/writingId.js";
import { useIsSunnyStore } from '@/stores/isSunny';
import { selectByTitleService, selectHotWritingService, selectWritingCountService } from "@/api/writing.js";

const isSunnyStore = useIsSunnyStore();
const userIdStore = useUserIdStore();
const imageStore = useImageStore();
const passwordStore = usePasswordStore();
const isTagMenuOpen = ref(false); // 控制"文章随笔"是否展开
const isLogin = ref(false);

// 添加移动端菜单控制
const showMobileMenu = ref(false);

// 添加一个响应式变量来存储当前文章ID
const currentArticleId = ref(null)

const goToHome = () => {
  router.push('/homePage')
}

// 退出登录
const logout = async () => {
  await logoutService(userIdStore.id);
  ElMessage.success("退出登录成功");
  userIdStore.clearUserId();
  imageStore.clearImage();
  passwordStore.clearPassword();
  reAuthor();
  userData.value = {
    id: '',
    username: '',
    password: '',
    email: '',
    status: '',
    createTime: '',
    updateTime: '',
    image: ''
  }
  await router.push('/homePage');
  window.location.reload();
}

const isAuthor = ref(false);

// 判断是不是作者
const reAuthor = () => {
  if (userIdStore.id === 1) {
    isAuthor.value = true;
  }
}
reAuthor();

const userData = ref({
  "id": '',
  "username": '',
  "password": '',
  "email": '',
  "status": '',
  "createTime": '',
  "updateTime": '',
  "image": ''
})

const hotArticles = ref([]);
const tags = ref([]);
const showIcon = ref(false)

// 获取分类列表
const fetchTags = async () => {
  const result = await selectALLService();
  tags.value = result.data;
};

//siteInfo假数据
const siteInfo = ref({
  articleCount: '',
  visitCount: '',
  starCount: '',
  runDays: '',
});

// 获取站点信息
const fetchSiteInfo = async () => {
  const articleCount = await selectWritingCountService();
  const starCount = await selectStarCountService();
  const visitCount = await selectVisitorCountService();
  const runDays = await selectRunDaysService();
  siteInfo.value.articleCount = articleCount.data;
  siteInfo.value.starCount = starCount.data;
  siteInfo.value.visitCount = visitCount.data.totalUniqueVisitors;
  siteInfo.value.runDays = runDays.data.runningDays;
};

// 获取最热文章
const fetchHotArticles = async () => {
  const result = await selectHotWritingService();
  hotArticles.value = result.data.map(article => ({
    ...article,
    hovered: false  // 添加 hover 状态
  }));
};

// 初始化所有数据
onMounted(async () => {
  await fetchTags();
  await fetchSiteInfo();
  await fetchHotArticles();
  // 检查当前路由是否包含 tag，如果是则展开文章随笔菜单
  updateMenuState();
});

const getUserData = async () => {
  if (userIdStore.id !== '') {
    let result = await selectByUserIdService(userIdStore.id);
    userData.value = result.data;
    isLogin.value = true;
    isSunnyStore.setSunny(true);
    imageStore.setImage(userData.value.image);
  }
}
getUserData();

const direction = ref(false);
const inputData = ref("");
const searchData = ref()
let length = ref(0);

const search = async () => {
  if (!inputData.value) {
    searchData.value = '';
    length = 0;
    return;
  }
  let result = await selectByTitleService(inputData.value);
  searchData.value = result.data;
  length = result.data.length;
}

const writingIdStore = useWritingIdStore();

// 在LookWriting函数中设置当前文章ID
const LookWriting = (writingId) => {
  writingIdStore.setWritingId(writingId)
  router.push({
    path: '/lookWriting',
    query: {
      id: writingId
    }
  })
  direction.value = false
  handleClose()
  currentArticleId.value = writingId
  // 移动端点击文章后关闭菜单
  if (isMobile.value) {
    showMobileMenu.value = false;
  }
}

// 搜索框关闭事件
const handleClose = () => {
  inputData.value = '';
  searchData.value = '';
  length = 0;
  direction.value = false;
}

// 响应式屏幕检测
const isLargeScreen = ref(window.innerWidth >= 950);
const isMobile = computed(() => !isLargeScreen.value);

window.addEventListener('resize', () => {
  isLargeScreen.value = window.innerWidth >= 950;
});

// 修复：使用计算属性来动态计算 activeIndex
import { useRoute } from 'vue-router'
import { selectALLService } from '@/api/label';
import { selectEvaluateTotalCountService } from '@/api/evaluate';
import { selectRunDaysService, selectVisitorCountService } from '@/api/statistics';

const route = useRoute()

import { selectStarCountService } from '@/api/star';

// 修复：使用计算属性来精确匹配路由
const activeIndex = computed(() => {
  const path = route.path;

  // 精确匹配首页相关路由
  if (path === '/' || path === '/homePage' || path.startsWith('/homePage')) {
    return '/homePage';
  }

  // 精确匹配关于页面
  if (path === '/about') {
    return '/about';
  }

  // 精确匹配标签页面
  if (path.startsWith('/tag/')) {
    return `/tag/${route.params.tagName || ''}`;
  }

  return path;
});

const currentTagName = computed(() => {
  if (route.params.tagName) {
    return route.params.tagName;
  }
  return null;
});

// 更新菜单状态函数
const updateMenuState = () => {
  const currentPath = route.path;

  // 检查是否是标签相关路由
  if (currentPath.startsWith('/tag')) {
    isTagMenuOpen.value = true;
  } else {
    isTagMenuOpen.value = false;
  }
};

// 修复：菜单项点击处理
const handleMenuItemClick = (path) => {
  router.push(path);
  // 移动端点击菜单项后关闭菜单
  if (isMobile.value) {
    showMobileMenu.value = false;
  }
};

// 添加一个响应式变量来控制右侧边栏内容的显示
const showRightAsideContent = ref(true);

// 监听路由变化，重置文章状态
watch(() => route.path, (newPath) => {
  // 更新菜单状态
  updateMenuState();

  // 当进入文章详情页时隐藏右侧边栏内容
  showRightAsideContent.value = newPath !== '/lookWriting'

  // 重置所有文章的hovered状态
  if (newPath === '/lookWriting') {
    hotArticles.value.forEach(article => {
      article.hovered = false
    })
  }

  // 更新当前文章ID
  if (newPath.startsWith('/lookWriting')) {
    const params = new URLSearchParams(newPath.split('?')[1])
    currentArticleId.value = params.get('id')
  } else {
    currentArticleId.value = null
  }
}, { immediate: true })

const handleTagClick = (tagName) => {
  router.push(`/tag/${tagName}`);
  // 移动端点击标签后关闭菜单
  if (isMobile.value) {
    showMobileMenu.value = false;
  }
};

// 检查标签是否激活的函数
const isTagActive = (tagName) => {
  return currentTagName.value === tagName;
};

// 移动端菜单切换
const toggleMobileMenu = () => {
  showMobileMenu.value = !showMobileMenu.value;
};

// 点击遮罩层关闭菜单
const closeMobileMenu = () => {
  showMobileMenu.value = false;
};
</script>

<template>
  <!-- 顶部标题：横跨全屏 -->
  <div class="top-title">
    <!-- 移动端菜单按钮 -->
    <div class="mobile-menu-btn" v-if="isMobile" @click="toggleMobileMenu">
      <el-icon>
        <Menu />
      </el-icon>
    </div>
    <div class="site-header" @click="goToHome">
      <el-icon>
        <Planet />
      </el-icon>
      <span class="site-name">cyjx的技术驿站</span>
    </div>
  </div>

  <!-- 移动端遮罩层 -->
  <div class="mobile-mask" v-if="isMobile && showMobileMenu" @click="closeMobileMenu"></div>

  <!-- 主体容器 -->
  <el-container class="main-container">
    <!-- 左侧边栏（固定定位） -->
    <el-aside width="200px" class="left-aside fixed-aside" :class="{ 'mobile-menu': isMobile && showMobileMenu }">
      <div class="avatar-section">
        <img :src="avatar" alt="avatar" class="avatar-img" />
        <div class="username">cyjx</div>
        <div class="slogan">人生海海 山山而川 不过尔尔</div>
      </div>

      <!-- 修复：使用计算属性 activeIndex -->
      <el-menu class="menu-list" mode="vertical" :default-active="activeIndex">
        <el-menu-item index="/homePage" @click="handleMenuItemClick('/homePage')">
          <el-icon>
            <House />
          </el-icon>
          <span>本站首页</span>
        </el-menu-item>

        <!-- 修复：移除了内联样式，使用CSS控制 -->
        <el-sub-menu index="/tag" :opened="isTagMenuOpen">
          <template #title>
            <el-icon>
              <Document />
            </el-icon>
            <span>文章随笔</span>
          </template>
          <el-menu-item v-for="tag in tags" :key="tag.labelName" :index="`/tag/${tag.labelName}`"
            @click="handleTagClick(tag.labelName)" :class="{ 'active-tag': isTagActive(tag.labelName) }"
            style="min-height: 36px; width: calc(100% - 10px); margin: 0 5px; box-sizing: border-box;">
            <el-icon>
              <Collection />
            </el-icon>
            <span>{{ tag.labelName }}</span>
            <span class="tag-count-badge">{{ tag.count }}</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="main-container">
      <el-main class="main-content" :class="{ 'mobile-main': isMobile }">
        <router-view></router-view>
      </el-main>
    </el-container>

    <div class="right-aside" v-show="showRightAsideContent && !isMobile">
      <!-- 本站信息 -->
      <div class="sidebar-card" v-if="showRightAsideContent">
        <h3>本站信息</h3>
        <ul class="site-info-list">
          <li>
            <el-icon>
              <Document />
            </el-icon>
            <span class="info-label">文章数目</span>
            <span class="info-value">{{ siteInfo.articleCount }} 篇</span>
          </li>

          <li>
            <el-icon>
              <User />
            </el-icon>
            <span class="info-label">访问总数</span>
            <span class="info-value">{{ siteInfo.visitCount }} 次</span>
          </li>

          <li>
            <el-icon>
              <Star />
            </el-icon>
            <span class="info-label">点赞总数</span>
            <span class="info-value">{{ siteInfo.starCount }} 次</span>
          </li>

          <li>
            <el-icon>
              <Calendar />
            </el-icon>
            <span class="info-label">运行天数</span>
            <span class="info-value">{{ siteInfo.runDays }} 天</span>
          </li>
        </ul>
      </div>

      <!-- 最热文章 -->
      <div class="sidebar-card" v-if="showRightAsideContent">
        <h3>最热文章</h3>
        <ul>
          <li v-for="article in hotArticles" :key="article.id" @mouseenter="article.hovered = true"
            @mouseleave="article.hovered = false" class="hot-article-item"
            :style="{ color: article.hovered ? '#0066cc' : '#666' }" @click="LookWriting(article.id)">
            {{ article.title }}
            <span class="view-count">{{ article.viewCount }}</span>
            <el-icon v-if="article.hovered" class="view-icon">
              <View />
            </el-icon>
          </li>
        </ul>
      </div>

      <!-- 文章标签 -->
      <div class="sidebar-card" v-if="showRightAsideContent">
        <h3>文章标签</h3>
        <div class="tag-list">
          <span v-for="tag in tags" :key="tag.id" class="tag" @click="handleTagClick(tag.labelName)">{{ tag.labelName
          }}</span>
        </div>
      </div>
    </div>
  </el-container>
</template>

<style scoped lang="less">
.top-title {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 50px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #ddd;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
}

/* 移动端菜单按钮 */
.mobile-menu-btn {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  z-index: 1001;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

.mobile-menu-btn:hover {
  background-color: #e6f7ff;
}

.mobile-menu-btn .el-icon {
  font-size: 1.2em;
  color: #0066cc;
}

.site-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1.3em;
  color: #0066cc;
  font-weight: bold;
  cursor: pointer;
  transition: opacity 0.3s ease;
}

.site-header:hover {
  opacity: 0.8;
}

.site-name {
  margin: 0;
  padding: 0;
  display: inline-block;
  background-color: transparent;
  box-shadow: none;
}

/* 移动端遮罩层 */
.mobile-mask {
  position: fixed;
  top: 50px;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 998;
}

.main-container {
  display: flex;
  min-height: calc(100vh - 50px);
}

.left-aside {
  position: fixed;
  top: 50px;
  left: 0;
  width: 350px;
  height: calc(100vh - 50px);
  background-color: #f9f9f9;
  border-right: 1px solid #ddd;
  padding: 20px 0;
  overflow-y: auto;
  z-index: 999;
  transition: transform 0.3s ease;
}

/* 移动端左侧菜单样式 */
@media (max-width: 949px) {
  .left-aside {
    transform: translateX(-100%);
    width: 65%;
  }

  .left-aside.mobile-menu {
    transform: translateX(0);
  }

  .main-content.mobile-main {
    margin-left: 0 !important;
    margin-right: 0 !important;
    width: 100%;
  }

  .top-title {
    justify-content: center;
  }

  .site-header {
    justify-content: center;
  }
}

/* 桌面端样式 */
@media (min-width: 950px) {
  .left-aside {
    transform: translateX(0) !important;
  }

  .main-content {
    margin-left: 350px;
    margin-right: 250px;
  }

  .mobile-menu-btn {
    display: none;
  }
}

.avatar-section {
  text-align: center;
  margin-bottom: 20px;
}

.avatar-img {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 4px solid #0066cc;
  object-fit: cover;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.username {
  font-size: 1.4em;
  color: #0066cc;
  margin: 10px 0;
  font-weight: bold;
}

.slogan {
  font-size: 0.9em;
  color: #666;
  margin-bottom: 20px;
}

.menu-list {
  background-color: transparent;
  border: none;
}

.menu-list .el-menu-item {
  padding-left: 30px;
  color: #333;
  display: flex;
  align-items: center;
}

.menu-list .el-menu-item .el-icon {
  color: #0066cc;
  margin-right: 8px;
}

.menu-list .el-menu-item:hover {
  background-color: #e6f7ff;
}

/* 激活的菜单项样式 */
.menu-list .el-menu-item.is-active {
  background-color: #e6f7ff;
  color: #0066cc;
  border-right: 3px solid #0066cc;
}

.menu-list .el-menu-item.is-active .el-icon {
  color: #0066cc;
}

/* 激活的标签样式 */
.menu-list .el-menu-item.active-tag {
  background-color: #e6f7ff;
  color: #0066cc;
  border-right: 3px solid #0066cc;
}

.menu-list .el-menu-item.active-tag .el-icon {
  color: #0066cc;
}

/* 修复：移除子菜单的多余滚动条 */
.menu-list .el-sub-menu .el-menu {
  background-color: transparent;
  border: none;
  max-height: none !important;
  overflow-y: visible !important;
}

.menu-list .el-sub-menu .el-menu--inline {
  overflow: visible !important;
  max-height: none !important;
}

.menu-list .el-sub-menu .el-menu-item {
  min-height: 36px;
  height: auto;
  padding: 8px 20px 8px 40px;
  line-height: 1.4;
  margin: 0;
  box-sizing: border-box;
  width: 100%;
}

.main-content {
  padding: 20px;
  background-color: #fff;
  transition: margin 0.3s ease;
}

.right-aside {
  position: fixed;
  top: 50px;
  right: 0;
  width: 250px;
  height: calc(100vh - 50px);
  background-color: #f9f9f9;
  border-left: 1px solid #ddd;
  padding: 20px;
  overflow-y: auto;
  scrollbar-width: none;
  scrollbar-color: #ccc transparent;
}

.right-aside::-webkit-scrollbar {
  display: none;
}

.right-aside::-webkit-scrollbar-track {
  background: transparent;
}

.right-aside::-webkit-scrollbar-thumb {
  background-color: #ccc;
  border-radius: 3px;
}

.right-aside::-webkit-scrollbar-thumb:hover {
  background-color: #999;
}

.sidebar-card {
  background-color: white;
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.sidebar-card h3 {
  margin-top: 0;
  color: #333;
  font-size: 1.1em;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
  margin-bottom: 12px;
}

.sidebar-card ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sidebar-card li {
  padding: 8px 0;
  color: #666;
  font-size: 0.9em;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hot-articles span {
  float: right;
  color: #0066cc;
  font-size: 0.8em;
}

.hot-article-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  cursor: pointer;
  transition: color 0.3s ease;
  background-color: #ffffff;
}

.hot-article-item .view-count {
  font-size: 0.9em;
  color: #666;
  margin-left: 8px;
}

.hot-article-item .view-icon {
  margin-left: 8px;
  color: #0066cc;
  font-size: 14px;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 0;
  margin: 0;
  list-style: none;
}

.tag {
  display: inline-block;
  padding: 4px 8px;
  background-color: #e0e0e0;
  color: #333;
  border-radius: 4px;
  font-size: 0.8em;
  white-space: nowrap;
  transition: all 0.3s ease;
  max-width: 100%;
}

.tag:hover {
  background-color: #0066cc;
  color: white;
}

.site-info-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;

  li {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 0.9em;
    color: #666;
  }

  .info-label {
    flex: 1;
    margin-left: 8px;
  }

  .info-value {
    font-weight: bold;
    color: #0066cc;
    white-space: nowrap;
  }

  .el-icon {
    margin-right: 8px;
    color: #0066cc;
  }
}

.tag-button {
  display: inline-block;
  padding: 4px 12px;
  background-color: #e0e0e0;
  color: #333;
  border-radius: 6px;
  font-size: 0.85em;
  margin: 4px 0;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #ccc;
}

.tag-button:hover {
  background-color: #0066cc;
  color: white;
  transform: translateY(-1px);
}

.tag-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  cursor: pointer;
  font-size: 0.9em;
  color: #666;
  transition: background-color 0.3s ease;

  &:hover {
    background-color: #e6f7ff;
    color: #0066cc;
  }

  .el-icon {
    margin-right: 8px;
    color: #0066cc;
    width: 16px;
    height: 16px;
  }

  .tag-name {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    margin-left: 0;
  }
}

.tag-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  cursor: pointer;
  font-size: 0.9em;
  color: #666;
  transition: background-color 0.3s ease;

  &:hover {
    background-color: #e6f7ff;
    color: #0066cc;
  }

  .el-icon {
    margin-right: 8px;
    color: #0066cc;
    width: 16px;
    height: 16px;
  }

  .tag-name {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    margin-left: 0;
  }

  .article-count {
    min-width: 24px;
    height: 20px;
    border-radius: 50%;
    background-color: #0066cc;
    color: white;
    font-size: 0.75em;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-left: 8px;
    flex-shrink: 0;
  }
}

.tag-item .el-icon {
  margin-left: 0;
}

.tag-item-sub .el-icon {
  margin-left: 16px;
}

.tag-count-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 20px;
  height: 20px;
  padding: 0 6px;
  background-color: #e5f2ff;
  color: #0066cc;
  font-size: 12px;
  border-radius: 4px;
  margin-left: 8px;
  flex-shrink: 0;
}
</style>