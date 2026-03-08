import { createRouter, createWebHistory } from 'vue-router'

const LayoutVue = () => import('../views/Layout.vue')
const HomePageVue = () => import('@/views/homepage/HomePage.vue')
const WritingVue = () => import('@/views/writing/Writing.vue')
const LookWritingVue = () => import('@/views/writing/LookWriting.vue')
const TagVue = () => import('@/views/tag/tag.vue')

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            component: LayoutVue,
            redirect: '/homePage',
            children: [
                { path: '/homePage', component: HomePageVue },
                { path: '/writing', component: WritingVue },
                { path: '/lookWriting', component: LookWritingVue },
                {path: '/tag/:tagName', component:TagVue }
            ]
        }
    ]
})

export default router
