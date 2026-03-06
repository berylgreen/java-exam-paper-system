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
      <div v-if="paper" class="paper-header">
        <h1>{{ paper.title }}</h1>
        <p>总分: {{ paper.totalScore }}分 &nbsp;|&nbsp; 时间: {{ paper.durationMinutes }}分钟 &nbsp;|&nbsp; 共{{ paper.questions?.length }}题</p>
      </div>

      <template v-if="paper">
        <div v-for="(section, idx) in sections" :key="idx">
          <div class="section-title">{{ sectionNum(idx) }}、{{ section.typeLabel }} (共{{ section.questions.length }}题，共{{ section.totalScore }}分)</div>
          <div v-for="(pq, qi) in section.questions" :key="pq.id" class="question-item">
            <div class="question-content">{{ qi + 1 }}. ({{ pq.score }}分) {{ pq.question.content }}</div>
            <!-- 选项 -->
            <div v-if="pq.question.options" class="question-options">
              <div v-for="opt in parseOpts(pq.question.options)" :key="opt.label" class="option">{{ opt.label }}. {{ opt.text }}</div>
            </div>
            <!-- 答案 -->
            <div v-if="showAnswer" class="answer-block">
              <div><b>答案: </b>{{ pq.question.answer }}</div>
              <div v-if="pq.question.explanation" style="margin-top:4px"><b>解析: </b>{{ pq.question.explanation }}</div>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { paperApi } from '../api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const paper = ref(null)
const loading = ref(false)
const showAnswer = ref(false)

const typeOrder = ['SINGLE_CHOICE','MULTIPLE_CHOICE','TRUE_FALSE','FILL_BLANK','SHORT_ANSWER','PROGRAMMING']
const typeLabels = { SINGLE_CHOICE:'单选题', MULTIPLE_CHOICE:'多选题', TRUE_FALSE:'判断题', FILL_BLANK:'填空题', SHORT_ANSWER:'简答题', PROGRAMMING:'编程题' }
const sectionNums = ['一','二','三','四','五','六']
const sectionNum = (i) => i < sectionNums.length ? sectionNums[i] : String(i + 1)
const parseOpts = (s) => { try { return JSON.parse(s) } catch { return [] } }

const sections = computed(() => {
  if (!paper.value?.questions) return []
  const grouped = {}
  for (const pq of paper.value.questions) {
    const t = pq.question.type
    if (!grouped[t]) grouped[t] = []
    grouped[t].push(pq)
  }
  return typeOrder.filter(t => grouped[t]).map(t => ({
    type: t,
    typeLabel: typeLabels[t],
    questions: grouped[t],
    totalScore: grouped[t].reduce((s, pq) => s + pq.score, 0)
  }))
})

const handleExport = () => { window.open(paperApi.exportUrl(route.params.id), '_blank') }

onMounted(async () => {
  loading.value = true
  try { paper.value = (await paperApi.getById(route.params.id)).data }
  catch { ElMessage.error('加载失败') }
  loading.value = false
})
</script>
