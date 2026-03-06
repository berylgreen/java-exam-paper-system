import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    { path: '/', redirect: '/papers' },
    { path: '/questions', name: 'QuestionBank', component: () => import('./views/QuestionBank.vue'), meta: { title: '题库管理' } },
    { path: '/papers', name: 'PaperList', component: () => import('./views/PaperList.vue'), meta: { title: '试卷列表' } },
    { path: '/papers/:id', name: 'PaperPreview', component: () => import('./views/PaperPreview.vue'), meta: { title: '试卷预览' } },
    { path: '/auto-generate', name: 'AutoGenerate', component: () => import('./views/AutoGenerate.vue'), meta: { title: '自动组卷' } },
]

const router = createRouter({ history: createWebHistory(), routes })
export default router
