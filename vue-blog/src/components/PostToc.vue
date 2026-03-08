<template>
    <div class="toc-sidebar" v-if="tocItems.length > 0">
        <div class="toc">
            <h3>文章目录</h3>
            <ul>
                <li v-for="(item, index) in tocItems" :key="index" :style="{ marginLeft: item.marginLeft + 'px' }">
                    <a :href="'#' + item.id" :class="{ active: item.id === activeHeadingId }"
                        @click.prevent="scrollToHeading(item.id)">
                        {{ item.title }}
                    </a>
                </li>
            </ul>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

const props = defineProps({
    contentSelector: {
        type: String,
        default: '#preview'
    }
})

const tocItems = ref([])
const activeHeadingId = ref('')

// 解析目录结构
const parseToc = () => {
    const contentElement = document.querySelector(props.contentSelector)
    if (!contentElement) return

    const headings = contentElement.querySelectorAll('h1, h2, h3')
    const items = []

    headings.forEach(heading => {
        // 如果标题没有id，生成一个
        if (!heading.id) {
            heading.id = 'heading-' + Math.random().toString(36).substr(2, 9)
        }

        const item = {
            id: heading.id,
            title: heading.textContent.trim(),
            tagName: heading.tagName,
            marginLeft: heading.tagName === 'H2' ? 15 : heading.tagName === 'H3' ? 30 : 0
        }
        items.push(item)
    })

    tocItems.value = items
}

// 滚动到指定标题
const scrollToHeading = (id) => {
    const element = document.getElementById(id)
    if (element) {
        element.scrollIntoView({ behavior: 'smooth' })
        activeHeadingId.value = id
    }
}

// 滚动事件处理
const handleScroll = () => {
    const contentElement = document.querySelector(props.contentSelector)
    if (!contentElement) return

    const headings = contentElement.querySelectorAll('h1, h2, h3')
    let currentHeading = null

    headings.forEach(heading => {
        const rect = heading.getBoundingClientRect()
        if (rect.top >= 0 && rect.top <= window.innerHeight / 2) {
            currentHeading = heading
        }
    })

    if (currentHeading) {
        activeHeadingId.value = currentHeading.id
    }
}

// 初始化目录
onMounted(() => {
    // 延迟执行以确保内容已渲染
    setTimeout(() => {
        parseToc()
        window.addEventListener('scroll', handleScroll)
    }, 100)
})

onBeforeUnmount(() => {
    window.removeEventListener('scroll', handleScroll)
})

// 提供给父组件的方法
defineExpose({
    parseToc
})
</script>

<style scoped>
.toc-sidebar {
    width: 100%;
    background-color: white;
    border: 1px solid #e6e6e6;
    border-radius: 8px;
    padding: 15px; /* 减少内边距 */
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
    /* 防止水平滚动条 */
    overflow-x: hidden;
    overflow-y: auto;
    /* 确保内容不会被遮挡 */
    box-sizing: border-box;
}

.toc {
    font-size: 14px;
    /* 确保内容不会导致水平滚动 */
    max-width: 100%;
    /* 移除可能的外边距 */
    margin: 0;
    padding: 0;
}

.toc h3 {
    margin: 0 0 12px 0; /* 减少底部外边距 */
    padding-bottom: 8px;
    border-bottom: 1px solid #e6e6e6;
    font-size: 16px;
    color: #333;
    font-weight: 600;
    /* 防止标题过长导致水平滚动 */
    word-wrap: break-word;
    overflow-wrap: break-word;
    /* 确保标题不会被遮挡 */
    padding-left: 0;
    padding-right: 0;
}

.toc ul {
    list-style-type: none;
    padding-left: 0;
    margin: 0;
    /* 确保ul不会导致水平滚动 */
    max-width: 100%;
}

.toc li {
    margin: 6px 0; /* 减少上下外边距 */
    transition: all 0.3s ease;
    /* 确保列表项不会超出容器 */
    max-width: 100%;
    overflow-wrap: break-word;
    /* 防止水平滚动 */
    overflow-x: hidden;
    /* 确保左侧不会被遮挡 */
    padding-left: 0;
}

.toc a {
    text-decoration: none;
    color: #666;
    cursor: pointer;
    transition: all 0.3s ease;
    display: block;
    padding: 5px 8px; /* 减少上下内边距 */
    border-radius: 4px;
    line-height: 1.4;
    font-size: 13px;
    /* 关键：确保文本换行 */
    white-space: normal;
    /* 允许换行 */
    word-wrap: break-word;
    /* 允许在单词内换行 */
    overflow-wrap: break-word;
    /* 更现代的属性 */
    word-break: break-word;
    /* 确保长单词也能换行 */
    /* 防止水平滚动 */
    overflow-x: hidden;
    /* 确保链接不会超出容器 */
    max-width: 100%;
    /* 确保左侧有足够空间 */
    margin-left: 0;
    padding-left: 8px;
}

.toc a:hover {
    color: #0066cc;
    background-color: #f0f8ff;
}

.toc a.active {
    color: #fff;
    background-color: #0066cc;
    font-weight: 500;
}

/* 针对不同层级的目录项调整样式 */
.toc li[style*="margin-left: 0px"] a {
    font-weight: 600;
    /* 一级标题加粗 */
    padding-left: 8px; /* 确保一级标题也有左侧内边距 */
}

.toc li[style*="margin-left: 15px"] a {
    font-size: 12.5px;
    /* 二级标题稍小 */
    padding-left: 8px; /* 确保二级标题也有左侧内边距 */
}

.toc li[style*="margin-left: 30px"] a {
    font-size: 12px;
    /* 三级标题更小 */
    color: #888;
    padding-left: 8px; /* 确保三级标题也有左侧内边距 */
}

/* 响应式调整 */
@media (max-width: 1024px) {
    .toc-sidebar {
        padding: 12px;
    }

    .toc a {
        font-size: 14px;
        /* 在移动设备上稍微增大字体 */
    }
}

/* 针对超小屏幕的额外调整 */
@media (max-width: 480px) {
    .toc-sidebar {
        padding: 10px;
    }

    .toc h3 {
        font-size: 15px;
    }

    .toc a {
        font-size: 13px;
        padding: 4px 6px;
        line-height: 1.5;
        /* 增加行高提高可读性 */
    }
}
</style>