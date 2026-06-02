<template>
  <div>
    <div v-if="paper" class="paper-header">
      <h1>{{ paper.title }}</h1>
      <p>总分: {{ paper.totalScore }}分 &nbsp;|&nbsp; 时间: {{ paper.durationMinutes }}分钟 &nbsp;|&nbsp; 共{{ paper.questions ? paper.questions.length : 0 }}题</p>
    </div>

    <template v-if="paper">
      <div v-for="(section, idx) in sections" :key="idx">
        <div class="section-title">{{ sectionNum(idx) }}、{{ section.typeLabel }} (共{{ section.questions.length }}题，共{{ section.totalScore }}分)</div>
        <div v-for="(pq, qi) in section.questions" :key="pq.id || qi" class="question-item">
          <div class="question-content" style="display:flex;align-items:flex-start;">
            <span style="margin-right: 4px;">{{ qi + 1 }}. ({{ pq.score }}分) </span>
            <div class="markdown-body" style="display:inline-block;flex:1;" v-html="renderMarkdown(pq.question.content)"></div>
            <div style="display: flex; gap: 8px; margin-left: 8px;">
              <el-button v-if="allowEdit" size="small" type="warning" plain @click="emit('edit-question', pq.question)">
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-button v-if="allowReplace" size="small" type="primary" plain @click="openReplaceDialog(pq)">
                <el-icon><Refresh /></el-icon> 换题
              </el-button>
            </div>
          </div>
          <div v-if="pq.question.projectPath" style="margin-top:8px; padding:8px; background:#f5f7fa; border-radius:4px; font-size: 13px; color: #409EFF; border: 1px solid #d9ecff;">
            <b>📁 关联代码工程:</b> {{ pq.question.projectPath }}
          </div>
          <!-- 选项 -->
          <div v-if="pq.question.options" class="question-options">
            <div v-for="opt in parseOpts(pq.question.options)" :key="opt.label" class="option">{{ opt.label }}. {{ opt.text }}</div>
          </div>
          <!-- 答案 -->
          <div v-if="showAnswer" class="answer-block">
            <div style="display:flex;">
              <b style="white-space:nowrap;margin-right:4px;">答案: </b>
              <div class="markdown-body" style="flex:1;" v-html="renderMarkdown(pq.question.answer)"></div>
            </div>
            <div v-if="pq.question.explanation" style="margin-top:4px; display:flex;">
              <b style="white-space:nowrap;margin-right:4px;">解析: </b>
              <div class="markdown-body" style="flex:1;" v-html="renderMarkdown(pq.question.explanation)"></div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- 替换题目弹窗 -->
    <el-dialog v-model="replaceDialog.visible" title="选择替换题目" width="80%" top="5vh" append-to-body>
      <div style="margin-bottom: 16px; display: flex; gap: 16px; align-items: center;">
        <span style="font-weight:bold;color:#606266;">当前题型：{{ typeLabels[replaceDialog.targetType] }}</span>
        <el-select v-model="replaceDialog.searchForm.chapter" placeholder="筛选章节" clearable size="small" style="width: 150px">
          <el-option v-for="c in chapters" :key="c" :label="c" :value="c" />
        </el-select>
        <el-select v-model="replaceDialog.searchForm.difficulty" placeholder="筛选难度" clearable size="small" style="width: 120px">
          <el-option label="简单" value="EASY" />
          <el-option label="中等" value="MEDIUM" />
          <el-option label="困难" value="HARD" />
        </el-select>
        <el-button type="primary" size="small" @click="searchAlternatives" :loading="replaceDialog.loading">搜索</el-button>
      </div>
      
      <el-table :data="replaceDialog.candidates" v-loading="replaceDialog.loading" max-height="500px">
        <el-table-column label="题目内容" min-width="300">
          <template #default="{ row }">
            <div class="markdown-body" v-html="renderMarkdown(row.content)"></div>
            <div v-if="row.options" style="margin-top: 8px; color: #666; font-size: 13px;">
              <span v-for="opt in parseOpts(row.options)" :key="opt.label" style="margin-right: 12px;">{{ opt.label }}. {{ opt.text }}</span>
            </div>
            <div v-if="row.projectPath" style="margin-top: 4px; color: #409EFF; font-size: 12px;">
              📁 {{ row.projectPath }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="chapter" label="章节" width="120" />
        <el-table-column prop="difficulty" label="难度" width="80">
          <template #default="{ row }">
            <el-tag :type="row.difficulty === 'EASY' ? 'success' : row.difficulty === 'MEDIUM' ? 'warning' : 'danger'">
              {{ row.difficulty === 'EASY' ? '简单' : row.difficulty === 'MEDIUM' ? '中等' : '困难' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="confirmReplace(row)" :disabled="row.id === replaceDialog.targetPq?.question?.id">替换</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px; text-align: right;">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="replaceDialog.total"
          :page-size="replaceDialog.searchForm.size"
          v-model:current-page="replaceDialog.searchForm.page"
          @current-change="searchAlternatives"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { marked } from 'marked'
import { questionApi } from '../api'

const renderMarkdown = (text) => {
  if (!text) return ''
  return marked.parse(text)
}

const emit = defineEmits(['replace-question', 'edit-question'])

const props = defineProps({
  paper: {
    type: Object,
    required: true
  },
  showAnswer: {
    type: Boolean,
    default: false
  },
  allowReplace: {
    type: Boolean,
    default: false
  },
  allowEdit: {
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

// === 换题逻辑 ===
const chapters = ref([])
const replaceDialog = ref({
  visible: false,
  targetPq: null,
  targetType: '',
  loading: false,
  candidates: [],
  total: 0,
  searchForm: {
    chapter: '',
    difficulty: '',
    page: 1,
    size: 10
  }
})

const openReplaceDialog = async (pq) => {
  replaceDialog.value.targetPq = pq
  replaceDialog.value.targetType = pq.question.type
  replaceDialog.value.searchForm.chapter = pq.question.chapter || ''
  replaceDialog.value.searchForm.difficulty = pq.question.difficulty || ''
  replaceDialog.value.searchForm.page = 1
  replaceDialog.value.visible = true
  await searchAlternatives()
}

const searchAlternatives = async () => {
  replaceDialog.value.loading = true
  try {
    const res = await questionApi.list({
      type: replaceDialog.value.targetType,
      chapter: replaceDialog.value.searchForm.chapter || undefined,
      difficulty: replaceDialog.value.searchForm.difficulty || undefined,
      page: replaceDialog.value.searchForm.page - 1, // 后端分页从0开始
      size: replaceDialog.value.searchForm.size
    })
    replaceDialog.value.candidates = res.data.content || []
    replaceDialog.value.total = res.data.totalElements || 0
  } catch (e) {
    console.error('搜索替换题目失败', e)
  }
  replaceDialog.value.loading = false
}

const confirmReplace = (newQuestion) => {
  emit('replace-question', { oldPq: replaceDialog.value.targetPq, newQuestion })
  replaceDialog.value.visible = false
}

onMounted(async () => {
  try {
    chapters.value = (await questionApi.chapters()).data || []
  } catch (e) {
    console.error('加载章节失败', e)
  }
})
</script>
