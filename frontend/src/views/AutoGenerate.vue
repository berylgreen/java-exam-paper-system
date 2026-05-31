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
          <span style="color:#bbb;margin-left:20px;font-size:14px">
            默认考到第 <el-input-number v-model="form.maxChapter" :min="1" :max="20" size="small" style="width:100px;margin:0 8px" @change="applyMaxChapter"/> 章
          </span>
        </div>
        <el-checkbox-group v-model="form.chapters" @change="handleChapterChange">
          <el-checkbox v-for="c in chapters" :key="c" :label="c" :value="c" style="color:#bbb;margin-bottom:6px"/>
        </el-checkbox-group>
        <div style="margin-top:8px;color:#999;font-size:12px">不选则从全部章节抽取</div>
      </div>

      <h3 style="color:#fff;margin:20px 0 12px;font-size:15px">⚙️ 高级设置</h3>
      <div style="margin-bottom:24px;background:rgba(255,255,255,0.02);padding:16px;border-radius:8px;border:1px solid rgba(255,255,255,0.05)">
        <div style="margin-bottom:16px">
          <el-switch
            v-model="form.mustIncludeProject"
            active-text="必须包含项目题"
            style="--el-switch-on-color: #6dd49e;"
          />
          <div style="margin-top:8px;color:#999;font-size:12px">开启后，组卷时将强制确保试卷中至少有一道带附加工程项目的题目。</div>
        </div>

        <div>
          <el-switch
            v-model="form.specificProgrammingChapters"
            active-text="分别指定每一道编程题的章节"
            style="--el-switch-on-color: #6dd49e;"
          />
          <div style="margin-top:8px;color:#999;font-size:12px">开启后，可以为每一道编程题单独指定出题章节。</div>
          
          <div v-if="form.specificProgrammingChapters && form.programmingCount > 0" style="margin-top: 12px; display: flex; flex-direction: column; gap: 8px;">
            <div v-for="i in form.programmingCount" :key="i" style="display: flex; align-items: center; gap: 12px;">
              <span style="color:#bbb;font-size:14px;width:70px">第 {{ i }} 题:</span>
              <el-select v-model="form.programmingQuestionChapters[i-1]" placeholder="选择章节" size="small" style="width: 150px">
                <el-option v-for="c in chapters" :key="c" :label="c" :value="c" />
              </el-select>
            </div>
          </div>
        </div>
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

      <div class="paper-preview" style="padding: 24px;">
        <PaperViewer :paper="previewData" :show-answer="false" allow-replace @replace-question="handleReplace" />
      </div>
    </div>

    <!-- 已保存结果 -->
    <div v-if="savedResult" class="glass-card" style="margin-top:24px">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
        <h3 style="color:#6dd49e">💾 已保存到数据库！</h3>
        <div>
          <el-button type="primary" @click="$router.push(`/papers/${savedResult.id}`)">预览试卷</el-button>
          <el-button type="success" @click="exportPaper">导出试卷</el-button>
        </div>
      </div>
      <p style="color:#aaa">试卷：{{ savedResult.title }} | 总分：{{ savedResult.totalScore }}分 | 共{{ savedResult.questions?.length }}题</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { paperApi, questionApi } from '../api'
import { ElMessage } from 'element-plus'
import PaperViewer from '../components/PaperViewer.vue'

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
  fillBlankCount: 5, shortAnswerCount: 0, codeReadingCount: 2, programmingCount: 3,
  chapters: [], maxChapter: 7, easyPercent: 30, mediumPercent: 50, hardPercent: 20,
  textbookPercent: 80, networkPercent: 20, mustIncludeProject: true,
  specificProgrammingChapters: true, programmingQuestionChapters: []
})

watch(() => form.programmingCount, (newVal) => {
  if (newVal === null || newVal === undefined) return;
  const currentLength = form.programmingQuestionChapters.length;
  if (newVal > currentLength) {
    for (let i = currentLength; i < newVal; i++) {
      form.programmingQuestionChapters.push(chapters.value.length > 0 ? chapters.value[0] : '');
    }
  } else if (newVal < currentLength) {
    form.programmingQuestionChapters.splice(newVal);
  }
}, { immediate: true })

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
         (form.fillBlankCount || 0) * 2 +
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

const handleReplace = ({ oldPq, newQuestion }) => {
  const idx = previewData.value.questions.findIndex(pq => pq.question.id === oldPq.question.id)
  if (idx !== -1) {
    previewData.value.questions[idx].question = newQuestion
    ElMessage.success('换题成功')
  }
}

const exportPaper = () => { if (savedResult.value) window.open(paperApi.exportUrl(savedResult.value.id), '_blank') }

const applyMaxChapter = () => {
  if (!chapters.value || chapters.value.length === 0) return;
  form.chapters = chapters.value.filter(c => {
    const match = c.match(/^第(\d+)章/);
    return match && parseInt(match[1]) >= 1 && parseInt(match[1]) <= form.maxChapter;
  });
  handleChapterChange(form.chapters);
}

onMounted(async () => {
  try { 
    chapters.value = (await questionApi.chapters()).data;
    applyMaxChapter();
    
    if (form.programmingQuestionChapters.length >= 1) {
      form.programmingQuestionChapters[0] = chapters.value.find(c => c.startsWith('第2章')) || chapters.value[0] || '';
    }
    if (form.programmingQuestionChapters.length >= 2) {
      form.programmingQuestionChapters[1] = chapters.value.find(c => c.startsWith('第3章')) || chapters.value[0] || '';
    }
    if (form.programmingQuestionChapters.length >= 3) {
      form.programmingQuestionChapters[2] = chapters.value.find(c => c.startsWith('第7章')) || chapters.value[0] || '';
    }
  } catch {}
})
</script>
