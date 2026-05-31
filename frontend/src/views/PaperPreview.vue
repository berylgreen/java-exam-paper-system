<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px">
      <div style="display:flex;align-items:center;gap:12px">
        <el-button @click="$router.back()" circle><el-icon><ArrowLeft /></el-icon></el-button>
        <h2 class="page-title" style="margin-bottom:0">试卷预览</h2>
      </div>
      <div style="display:flex;gap:8px;align-items:center">
        <el-switch v-model="showAnswer" active-text="显示答案" inactive-text="隐藏答案" style="--el-switch-on-color:#667eea"/>
        <el-button type="success" @click="handleExport"><el-icon><Download /></el-icon> 导出Word</el-button>
      </div>
    </div>

    <div v-loading="loading" class="paper-preview">
      <PaperViewer v-if="paper" :paper="paper" :show-answer="showAnswer" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { paperApi } from '../api'
import { ElMessage } from 'element-plus'
import PaperViewer from '../components/PaperViewer.vue'

const route = useRoute()
const paper = ref(null)
const loading = ref(false)
const showAnswer = ref(false)

const handleExport = () => { window.open(paperApi.exportUrl(route.params.id), '_blank') }

onMounted(async () => {
  loading.value = true
  try { paper.value = (await paperApi.getById(route.params.id)).data }
  catch { ElMessage.error('加载失败') }
  loading.value = false
})
</script>
