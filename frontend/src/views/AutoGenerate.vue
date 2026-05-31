<template>
  <div>
    <h2 class="page-title">🎲 自动组卷</h2>
    <div class="glass-card">
      <el-form :model="form" label-width="100px">
        <el-form-item label="试卷标题">
          <el-input v-model="form.title" placeholder="例：Java期末考试A卷"/>
        </el-form-item>
        <el-form-item label="考试时长(分)">
          <div style="display:flex;align-items:center;gap:20px;">
            <el-input-number v-model="form.durationMinutes" :min="30" :max="300" :step="10"/>
            <span style="color:#6dd49e;font-size:16px;font-weight:bold;">预计总分：{{ currentTotalScore }} 分</span>
          </div>
        </el-form-item>
      </el-form>

      <h3 style="color:#fff;margin:20px 0 12px;font-size:15px">📝 题型配置</h3>
      <div class="config-grid">
        <div class="config-item"><label>单选题数量</label><el-input-number v-model="form.singleChoiceCount" :min="0" :max="30" size="small"/></div>
        <div class="config-item"><label>多选题数量</label><el-input-number v-model="form.multipleChoiceCount" :min="0" :max="20" size="small"/></div>
        <div class="config-item"><label>判断题数量</label><el-input-number v-model="form.trueFalseCount" :min="0" :max="20" size="small"/></div>
        <div class="config-item"><label>填空题数量</label><el-input-number v-model="form.fillBlankCount" :min="0" :max="20" size="small"/></div>
        <div class="config-item"><label>简答题数量</label><el-input-number v-model="form.shortAnswerCount" :min="0" :max="10" size="small"/></div>
        <div class="config-item"><label>程序分析题数量</label><el-input-number v-model="form.codeReadingCount" :min="0" :max="5" size="small"/></div>
        <div class="config-item"><label>编程题数量</label><el-input-number v-model="form.programmingCount" :min="0" :max="5" size="small"/></div>
      </div>

      <h3 style="color:#fff;margin:20px 0 12px;font-size:15px">⚡ 难度比例</h3>
      <div class="config-grid">
        <div class="config-item"><label>简单 {{ form.easyPercent }}%</label><el-slider v-model="form.easyPercent" :max="100"/></div>
        <div class="config-item"><label>中等 {{ form.mediumPercent }}%</label><el-slider v-model="form.mediumPercent" :max="100"/></div>
        <div class="config-item"><label>困难 {{ form.hardPercent }}%</label><el-slider v-model="form.hardPercent" :max="100"/></div>
      </div>

      <h3 style="color:#fff;margin:20px 0 12px;font-size:15px">📗 来源比例</h3>
      <div class="config-grid">
        <div class="config-item"><label>📖 课后习题原题 {{ form.textbookPercent }}%</label><el-slider v-model="form.textbookPercent" :max="100" @input="onTextbookChange"/></div>
        <div class="config-item"><label>🌐 网络来源 {{ form.networkPercent }}%</label><el-slider v-model="form.networkPercent" :max="100" @input="onNetworkChange"/></div>
      </div>
      <div style="color:#999;font-size:12px;margin-bottom:16px">来源比例总和应为100%，当前总和：{{ form.textbookPercent + form.networkPercent }}%</div>

      <h3 style="color:#fff;margin:20px 0 12px;font-size:15px">📖 章节范围</h3>
      <div style="margin-bottom:24px">
        <div style="display:flex;align-items:center;gap:12px;margin-bottom:12px;padding-bottom:10px;border-bottom:1px solid rgba(255,255,255,0.1)">
          <el-checkbox
            v-model="checkAll"
            :indeterminate="isIndeterminate"
            @change="handleCheckAllChange"
            style="color:#bbb"
          >全选</el-checkbox>
        </div>
        <el-checkbox-group v-model="form.chapters" @change="handleChapterChange">
          <el-checkbox v-for="c in chapters" :key="c" :label="c" :value="c" style="color:#bbb;margin-bottom:6px"/>
        </el-checkbox-group>
        <div style="margin-top:8px;color:#999;font-size:12px">不选则从全部章节抽取</div>
      </div>

      <h3 style="color:#fff;margin:20px 0 12px;font-size:15px">⚙️ 高级设置</h3>
      <div style="margin-bottom:24px;background:rgba(255,255,255,0.02);padding:16px;border-radius:8px;border:1px solid rgba(255,255,255,0.05)">
        <el-switch
          v-model="form.mustIncludeProject"
          active-text="必须包含项目题"
          style="--el-switch-on-color: #6dd49e;"
        />
        <div style="margin-top:8px;color:#999;font-size:12px">开启后，组卷时将强制确保试卷中至少有一道带附加工程项目的题目。</div>
      </div>

      <div style="text-align:center">
        <el-button type="primary" size="large" :loading="generating" @click="generate" style="padding:12px 48px;font-size:16px">
          <el-icon><MagicStick /></el-icon> 一键组卷
        </el-button>
      </div>
    </div>

    <!-- 预览区域 -->
    <div v-if="previewData" class="glass-card" style="margin-top:24px">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
        <h3 style="color:#6dd49e;margin:0">✅ 组卷完成！以下为预览（尚未保存）</h3>
        <div style="display:flex;gap:8px">
          <el-button type="warning" @click="generate" :loading="generating">
            <el-icon><RefreshRight /></el-icon> 重新组卷
          </el-button>
          <el-button type="success" @click="savePaper" :loading="saving">
            <el-icon><FolderChecked /></el-icon> 保存到数据库
          </el-button>
        </div>
      </div>

      <p style="color:#aaa;margin-bottom:16px">
        试卷：{{ previewData.title }} | 总分：{{ previewData.totalScore }}分 | 共{{ previewData.questions?.length }}题
      </p>

      <!-- 内嵌题目预览 -->
      <div class="preview-sections">
        <div v-for="(section, idx) in previewSections" :key="idx" style="margin-bottom:16px">
          <div style="color:#667eea;font-weight:bold;font-size:14px;margin-bottom:8px">
            {{ sectionNum(idx) }}、{{ section.typeLabel }} (共{{ section.questions.length }}题，共{{ section.totalScore }}分)
          </div>
          <div v-for="(pq, qi) in section.questions" :key="qi"
               style="color:#ccc;font-size:13px;padding:6px 0;border-bottom:1px solid rgba(255,255,255,0.05)">
            <span>{{ qi + 1 }}. ({{ pq.score }}分) {{ pq.question.content }}</span>
            <div v-if="pq.question.projectPath" style="margin-top:8px; padding:6px; background:rgba(64,158,255,0.1); border-radius:4px; font-size: 12px; color: #409EFF; border: 1px solid rgba(64,158,255,0.2);">
              <b>📁 关联代码工程:</b> {{ pq.question.projectPath }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 已保存结果 -->
    <div v-if="savedResult" class="glass-card" style="margin-top:24px">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
        <h3 style="color:#6dd49e">💾 已保存到数据库！</h3>
        <div>
          <el-button type="primary" @click="$router.push(`/papers/${savedResult.id}`)">预览试卷</el-button>
          <el-button type="success" @click="exportPaper">导出Word</el-button>
        </div>
      </div>
      <p style="color:#aaa">试卷：{{ savedResult.title }} | 总分：{{ savedResult.totalScore }}分 | 共{{ savedResult.questions?.length }}题</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { paperApi, questionApi } from '../api'
import { ElMessage } from 'element-plus'

const chapters = ref([])
const generating = ref(false)
const saving = ref(false)
const previewData = ref(null)
const savedResult = ref(null)

const generateDefaultTitle = () => {
  const d = new Date()
  const pad = (n) => n.toString().padStart(2, '0')
  return `自动组卷-${d.getFullYear()}${pad(d.getMonth() + 1)}${pad(d.getDate())}${pad(d.getHours())}${pad(d.getMinutes())}`
}

const form = reactive({
  title: generateDefaultTitle(), durationMinutes: 120,
  singleChoiceCount: 10, multipleChoiceCount: 0, trueFalseCount: 0,
  fillBlankCount: 5, shortAnswerCount: 2, codeReadingCount: 2, programmingCount: 1,
  chapters: [], easyPercent: 30, mediumPercent: 50, hardPercent: 20,
  textbookPercent: 80, networkPercent: 20, mustIncludeProject: true
})

const onTextbookChange = (val) => { form.networkPercent = Math.max(0, 100 - val) }
const onNetworkChange = (val) => { form.textbookPercent = Math.max(0, 100 - val) }

const currentTotalScore = computed(() => {
  const pCount = form.programmingCount || 0;
  let programmingScore = 0;
  if (pCount === 1) programmingScore = 20;
  else if (pCount === 2) programmingScore = 30;
  else if (pCount >= 3) programmingScore = 40 + (pCount - 2) * 10;

  return (form.singleChoiceCount || 0) * 2 +
         (form.multipleChoiceCount || 0) * 4 +
         (form.trueFalseCount || 0) * 2 +
         (form.fillBlankCount || 0) * 4 +
         (form.shortAnswerCount || 0) * 10 +
         (form.codeReadingCount || 0) * 10 +
         programmingScore;
})

const checkAll = ref(false)
const isIndeterminate = computed(() => {
  const len = form.chapters.length
  return len > 0 && len < chapters.value.length
})
const handleCheckAllChange = (val) => {
  form.chapters = val ? [...chapters.value] : []
}
const handleChapterChange = (val) => {
  checkAll.value = val.length === chapters.value.length
}

// 题型分组预览
const typeOrder = ['SINGLE_CHOICE','MULTIPLE_CHOICE','TRUE_FALSE','FILL_BLANK','SHORT_ANSWER','CODE_READING','PROGRAMMING']
const typeLabels = { SINGLE_CHOICE:'单选题', MULTIPLE_CHOICE:'多选题', TRUE_FALSE:'判断题', FILL_BLANK:'填空题', SHORT_ANSWER:'简答题', CODE_READING:'程序分析题', PROGRAMMING:'编程题' }
const sectionNums = ['一','二','三','四','五','六','七']
const sectionNum = (i) => i < sectionNums.length ? sectionNums[i] : String(i + 1)

const previewSections = computed(() => {
  if (!previewData.value?.questions) return []
  const grouped = {}
  for (const pq of previewData.value.questions) {
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

/** 组卷预览 (不保存) */
const generate = async () => {
  if (!form.title) { ElMessage.warning('请输入试卷标题'); return }
  generating.value = true
  savedResult.value = null
  try {
    const data = { ...form, chapters: form.chapters.length ? form.chapters : null }
    previewData.value = (await paperApi.previewGenerate(data)).data
    ElMessage.success('组卷完成，请预览确认后点击"保存到数据库"')
  } catch (e) {
    ElMessage.error('组卷失败: ' + (e.response?.data?.error || e.response?.data?.message || e.message))
  }
  generating.value = false
}

/** 确认保存到数据库 */
const savePaper = async () => {
  if (!previewData.value) return
  saving.value = true
  try {
    // 从预览数据构建保存请求
    const saveReq = {
      title: previewData.value.title,
      durationMinutes: previewData.value.durationMinutes,
      description: previewData.value.description,
      questions: previewData.value.questions.map(pq => ({
        questionId: pq.question.id,
        questionOrder: pq.questionOrder,
        score: pq.score
      }))
    }
    savedResult.value = (await paperApi.saveGenerated(saveReq)).data
    previewData.value = null
    ElMessage.success('试卷已保存到数据库！')
  } catch (e) {
    ElMessage.error('保存失败: ' + (e.response?.data?.error || e.response?.data?.message || e.message))
  }
  saving.value = false
}

const exportPaper = () => { if (savedResult.value) window.open(paperApi.exportUrl(savedResult.value.id), '_blank') }

onMounted(async () => {
  try { chapters.value = (await questionApi.chapters()).data } catch {}
})
</script>
