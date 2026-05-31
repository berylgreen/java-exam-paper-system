<template>
  <div>
    <div v-if="paper" class="paper-header">
      <h1>{{ paper.title }}</h1>
      <p>总分: {{ paper.totalScore }}分 &nbsp;|&nbsp; 时间: {{ paper.durationMinutes }}分钟 &nbsp;|&nbsp; 共{{ paper.questions?.length }}题</p>
    </div>

    <template v-if="paper">
      <div v-for="(section, idx) in sections" :key="idx">
        <div class="section-title">{{ sectionNum(idx) }}、{{ section.typeLabel }} (共{{ section.questions.length }}题，共{{ section.totalScore }}分)</div>
        <div v-for="(pq, qi) in section.questions" :key="pq.id || qi" class="question-item">
          <div class="question-content" style="white-space: pre-wrap; line-height: 1.6; font-family: inherit;">{{ qi + 1 }}. ({{ pq.score }}分) {{ pq.question.content }}</div>
          <div v-if="pq.question.projectPath" style="margin-top:8px; padding:8px; background:#f5f7fa; border-radius:4px; font-size: 13px; color: #409EFF; border: 1px solid #d9ecff;">
            <b>📁 关联代码工程:</b> {{ pq.question.projectPath }}
          </div>
          <!-- 选项 -->
          <div v-if="pq.question.options" class="question-options">
            <div v-for="opt in parseOpts(pq.question.options)" :key="opt.label" class="option">{{ opt.label }}. {{ opt.text }}</div>
          </div>
          <!-- 答案 -->
          <div v-if="showAnswer" class="answer-block">
            <div style="white-space: pre-wrap; line-height: 1.6; font-family: inherit;"><b>答案: </b>{{ pq.question.answer }}</div>
            <div v-if="pq.question.explanation" style="margin-top:4px; white-space: pre-wrap; line-height: 1.6; font-family: inherit;"><b>解析: </b>{{ pq.question.explanation }}</div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  paper: {
    type: Object,
    required: true
  },
  showAnswer: {
    type: Boolean,
    default: false
  }
})

const typeOrder = ['SINGLE_CHOICE','MULTIPLE_CHOICE','TRUE_FALSE','FILL_BLANK','SHORT_ANSWER','CODE_READING','PROGRAMMING']
const typeLabels = { SINGLE_CHOICE:'单选题', MULTIPLE_CHOICE:'多选题', TRUE_FALSE:'判断题', FILL_BLANK:'填空题', SHORT_ANSWER:'简答题', CODE_READING:'程序分析题', PROGRAMMING:'编程题' }
const sectionNums = ['一','二','三','四','五','六','七']
const sectionNum = (i) => i < sectionNums.length ? sectionNums[i] : String(i + 1)
const parseOpts = (s) => {
  if (!s) return []
  try {
    let parsed = JSON.parse(s)
    if (typeof parsed === 'string') {
      parsed = JSON.parse(parsed)
    }
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

const sections = computed(() => {
  if (!props.paper?.questions) return []
  const grouped = {}
  for (const pq of props.paper.questions) {
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
</script>
