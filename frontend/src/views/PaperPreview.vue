<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px">
      <div style="display:flex;align-items:center;gap:12px">
        <el-button @click="$router.back()" circle><el-icon><ArrowLeft /></el-icon></el-button>
        <h2 class="page-title" style="margin-bottom:0">试卷预览</h2>
      </div>
      <div style="display:flex;gap:8px;align-items:center">
        <el-switch v-model="showAnswer" active-text="显示答案" inactive-text="隐藏答案" style="--el-switch-on-color:#667eea"/>
        <el-button type="success" @click="handleExportClick"><el-icon><Download /></el-icon> 导出试卷</el-button>
      </div>
    </div>

    <div v-loading="loading" class="paper-preview">
      <PaperViewer v-if="paper" :paper="paper" :show-answer="showAnswer" allow-replace allow-edit allow-reorder @replace-question="handleReplace" @edit-question="handleEdit" @reorder="handleReorder" @update-title="handleUpdateTitle" />
    </div>

    <!-- 题目编辑对话框 -->
    <QuestionEditDialog 
      v-if="showEditDialog"
      v-model="showEditDialog"
      :is-edit="true"
      :question-data="editQuestionData"
      :chapters="chapters"
      :sources="sources"
      :types="types"
      @saved="loadPaper"
    />

    <ExportDialog
      v-model="exportDialogVisible"
      :show-answer-option="true"
      :default-with-answer="showAnswer"
      @confirm="onExportConfirm"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { paperApi, questionApi } from '../api'
import { ElMessage } from 'element-plus'
import PaperViewer from '../components/PaperViewer.vue'
import QuestionEditDialog from '../components/QuestionEditDialog.vue'
import ExportDialog from '../components/ExportDialog.vue'

const route = useRoute()
const paper = ref(null)
const loading = ref(false)
const showAnswer = ref(false)
const exportDialogVisible = ref(false)

// 编辑题目相关数据
const showEditDialog = ref(false)
const editQuestionData = ref({})
const chapters = ref([])
const sources = ref([])
const types = [
  {value:'SINGLE_CHOICE',label:'单选题'},{value:'MULTIPLE_CHOICE',label:'多选题'},
  {value:'TRUE_FALSE',label:'判断题'},{value:'FILL_BLANK',label:'填空题'},
  {value:'SHORT_ANSWER',label:'简答题'},{value:'CODE_READING',label:'程序分析题'},{value:'PROGRAMMING',label:'编程题'}
]

const handleExportClick = () => {
  exportDialogVisible.value = true
}

const onExportConfirm = ({ types, withAnswer, answerSheetType }) => {
  window.open(paperApi.exportUrl(route.params.id, withAnswer, types, answerSheetType), '_blank')
}

const loadPaper = async () => {
  loading.value = true
  try { paper.value = (await paperApi.getById(route.params.id)).data }
  catch { ElMessage.error('加载试卷失败') }
  loading.value = false
}

const loadMeta = async () => {
  try {
    chapters.value = (await questionApi.chapters()).data
    sources.value = (await questionApi.sources()).data
  } catch {}
}

const handleReplace = async ({ oldPq, newQuestion }) => {
  try {
    await paperApi.replaceQuestion(route.params.id, {
      oldQuestionId: oldPq.question.id,
      newQuestionId: newQuestion.id
    })
    ElMessage.success('换题成功')
    await loadPaper()
  } catch (e) {
    ElMessage.error('换题失败: ' + (e.response?.data?.message || e.message))
  }
}

const handleEdit = (question) => {
  editQuestionData.value = { ...question }
  showEditDialog.value = true
}

const handleReorder = async (newQuestions) => {
  try {
    const reqData = {
      questions: newQuestions.map(pq => ({
        questionId: pq.question.id,
        questionOrder: pq.questionOrder
      }))
    }
    await paperApi.reorderQuestions(route.params.id, reqData)
    ElMessage.success('题序已自动保存')
    await loadPaper()
  } catch (e) {
    ElMessage.error('保存题序失败: ' + (e.response?.data?.message || e.message))
    await loadPaper() // 恢复原状
  }
}

const handleUpdateTitle = async (newTitle) => {
  try {
    await paperApi.updateTitle(route.params.id, newTitle)
    ElMessage.success('标题修改成功')
    paper.value.title = newTitle
  } catch (e) {
    ElMessage.error('修改标题失败: ' + (e.response?.data?.message || e.message))
  }
}

onMounted(() => {
  loadPaper()
  loadMeta()
})
</script>
