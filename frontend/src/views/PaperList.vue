<template>
  <div>
    <h2 class="page-title">📋 试卷列表</h2>
    <div class="stat-cards" v-if="papers.length">
      <div class="stat-card">
        <div class="stat-value">{{ papers.length }}</div>
        <div class="stat-label">试卷总数</div>
      </div>
    </div>
    <div v-loading="loading" style="min-height:200px">
      <div v-for="p in papers" :key="p.id" class="glass-card paper-card" @click="$router.push(`/papers/${p.id}`)">
        <div class="card-title">{{ p.title }}</div>
        <div class="card-meta">
          <span><el-icon><Timer /></el-icon> {{ p.durationMinutes }}分钟</span>
          <span><el-icon><Document /></el-icon> {{ p.questions?.length || 0 }}题</span>
          <span><el-icon><TrophyBase /></el-icon> {{ p.totalScore }}分</span>
          <span>{{ formatTime(p.createdAt) }}</span>
        </div>
        <div class="card-actions" @click.stop>
          <el-button size="small" type="primary" @click="$router.push(`/papers/${p.id}`)">
            <el-icon><View /></el-icon> 预览
          </el-button>
          <el-button size="small" type="success" @click="handleExport(p.id)">
            <el-icon><Download /></el-icon> 导出Word
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(p.id)">
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
      </div>
      <el-empty v-if="!loading && papers.length===0" description="暂无试卷" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { paperApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const papers = ref([])
const loading = ref(false)

const load = async () => {
  loading.value = true
  try { papers.value = (await paperApi.list()).data } catch (e) { ElMessage.error('加载失败') }
  loading.value = false
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除此试卷？', '提示', { type: 'warning' })
  await paperApi.delete(id)
  ElMessage.success('已删除')
  load()
}

const handleExport = (id) => {
  window.open(paperApi.exportUrl(id), '_blank')
}

const formatTime = (t) => t ? t.substring(0, 16).replace('T', ' ') : ''

onMounted(load)
</script>
