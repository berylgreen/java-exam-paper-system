<template>
  <div>
    <h2 class="page-title">📋 试卷列表</h2>
    <div class="stat-cards" v-if="papers.length">
      <div class="stat-card">
        <div class="stat-value">{{ papers.length }}</div>
        <div class="stat-label">试卷总数</div>
      </div>
    </div>
    
    <div class="filter-bar" v-if="papers.length > 0" style="display: flex; justify-content: flex-end; margin-bottom: 20px; align-items: center; gap: 10px;">
      <el-button type="danger" :disabled="selectedPaperIds.length === 0" @click="handleBatchDelete">
        <el-icon><Delete /></el-icon> 批量删除 ({{ selectedPaperIds.length }})
      </el-button>
      <span style="flex: 1"></span>
      <span style="font-size: 14px; color: var(--el-text-color-regular);">排序方式:</span>
      <el-select v-model="sortBy" style="width: 120px" size="default">
        <el-option label="创建时间" value="createdAt" />
        <el-option label="试卷名" value="title" />
      </el-select>
      <el-button @click="toggleSortOrder" size="default">
        <el-icon><Top v-if="sortOrder === 'asc'" /><Bottom v-else /></el-icon>
        {{ sortOrder === 'asc' ? '升序' : '降序' }}
      </el-button>
    </div>

    <div v-loading="loading" style="min-height:200px">
      <el-checkbox-group v-model="selectedPaperIds">
        <div v-for="p in sortedPapers" :key="p.id" class="glass-card paper-card" @click="$router.push(`/papers/${p.id}`)">
          <div class="card-title">
            <el-checkbox :value="p.id" :label="p.id" @click.stop>&nbsp;</el-checkbox>
            {{ p.title }}
          </div>
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
            <el-icon><Download /></el-icon> 导出试卷
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(p.id)">
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
        </div>
      </el-checkbox-group>
      <el-empty v-if="!loading && papers.length===0" description="暂无试卷" />
    </div>

    <ExportDialog
      v-model="exportDialogVisible"
      :show-answer-option="false"
      @confirm="onExportConfirm"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { paperApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import ExportDialog from '../components/ExportDialog.vue'

const papers = ref([])
const loading = ref(false)
const exportDialogVisible = ref(false)
const currentExportId = ref(null)
const selectedPaperIds = ref([])

// 排序状态
const sortBy = ref('createdAt')
const sortOrder = ref('desc') // 'desc' 或 'asc'

const toggleSortOrder = () => {
  sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
}

const sortedPapers = computed(() => {
  return [...papers.value].sort((a, b) => {
    if (sortBy.value === 'title') {
      const titleA = a.title || '';
      const titleB = b.title || '';
      return sortOrder.value === 'asc' 
        ? titleA.localeCompare(titleB, 'zh') 
        : titleB.localeCompare(titleA, 'zh');
    } else {
      const valA = new Date(a.createdAt || 0).getTime();
      const valB = new Date(b.createdAt || 0).getTime();
      if (valA === valB) return 0;
      return sortOrder.value === 'asc' ? (valA > valB ? 1 : -1) : (valA < valB ? 1 : -1);
    }
  });
})

const load = async () => {
  loading.value = true
  try { papers.value = (await paperApi.list()).data } catch (e) { ElMessage.error('加载失败') }
  loading.value = false
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除此试卷？', '提示', { type: 'warning' })
  await paperApi.delete(id)
  ElMessage.success('已删除')
  selectedPaperIds.value = selectedPaperIds.value.filter(pid => pid !== id)
  load()
}

const handleBatchDelete = async () => {
  if (selectedPaperIds.value.length === 0) return
  await ElMessageBox.confirm(`确定删除选中的 ${selectedPaperIds.value.length} 份试卷吗？`, '提示', { type: 'warning' })
  await paperApi.batchDelete(selectedPaperIds.value)
  ElMessage.success('批量删除成功')
  selectedPaperIds.value = []
  load()
}

const handleExport = (id) => {
  currentExportId.value = id
  exportDialogVisible.value = true
}

const onExportConfirm = ({ types, withAnswer }) => {
  window.open(paperApi.exportUrl(currentExportId.value, withAnswer, types), '_blank')
}

const formatTime = (t) => t ? t.substring(0, 16).replace('T', ' ') : ''

onMounted(load)
</script>
