<template>
  <div>
    <h2 class="page-title">🎲 自动组卷</h2>
    <div class="glass-card">
      <el-form :model="form" label-width="100px">
        <el-form-item label="试卷标题">
          <el-input v-model="form.title" placeholder="例：Java期末考试A卷"/>
        </el-form-item>
        <el-form-item label="考试时长(分)">
          <el-input-number v-model="form.durationMinutes" :min="30" :max="300" :step="10"/>
        </el-form-item>
      </el-form>

      <h3 style="color:#fff;margin:20px 0 12px;font-size:15px">📝 题型配置</h3>
      <div class="config-grid">
        <div class="config-item"><label>单选题数量</label><el-input-number v-model="form.singleChoiceCount" :min="0" :max="30" size="small"/></div>
        <div class="config-item"><label>多选题数量</label><el-input-number v-model="form.multipleChoiceCount" :min="0" :max="20" size="small"/></div>
        <div class="config-item"><label>判断题数量</label><el-input-number v-model="form.trueFalseCount" :min="0" :max="20" size="small"/></div>
        <div class="config-item"><label>填空题数量</label><el-input-number v-model="form.fillBlankCount" :min="0" :max="20" size="small"/></div>
        <div class="config-item"><label>简答题数量</label><el-input-number v-model="form.shortAnswerCount" :min="0" :max="10" size="small"/></div>
        <div class="config-item"><label>编程题数量</label><el-input-number v-model="form.programmingCount" :min="0" :max="5" size="small"/></div>
      </div>

      <h3 style="color:#fff;margin:20px 0 12px;font-size:15px">⚡ 难度比例</h3>
      <div class="config-grid">
        <div class="config-item"><label>简单 {{ form.easyPercent }}%</label><el-slider v-model="form.easyPercent" :max="100"/></div>
        <div class="config-item"><label>中等 {{ form.mediumPercent }}%</label><el-slider v-model="form.mediumPercent" :max="100"/></div>
        <div class="config-item"><label>困难 {{ form.hardPercent }}%</label><el-slider v-model="form.hardPercent" :max="100"/></div>
      </div>

      <h3 style="color:#fff;margin:20px 0 12px;font-size:15px">📖 章节范围</h3>
      <div style="margin-bottom:24px">
        <el-checkbox-group v-model="form.chapters">
          <el-checkbox v-for="c in chapters" :key="c" :label="c" :value="c" style="color:#bbb;margin-bottom:6px"/>
        </el-checkbox-group>
        <div style="margin-top:8px;color:#999;font-size:12px">不选则从全部章节抽取</div>
      </div>

      <div style="text-align:center">
        <el-button type="primary" size="large" :loading="generating" @click="generate" style="padding:12px 48px;font-size:16px">
          <el-icon><MagicStick /></el-icon> 一键组卷
        </el-button>
      </div>
    </div>

    <!-- 预览 -->
    <div v-if="result" class="glass-card" style="margin-top:24px">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
        <h3 style="color:#6dd49e">✅ 组卷成功！</h3>
        <div>
          <el-button type="primary" @click="$router.push(`/papers/${result.id}`)">预览试卷</el-button>
          <el-button type="success" @click="exportPaper">导出Word</el-button>
        </div>
      </div>
      <p style="color:#aaa">试卷：{{ result.title }} | 总分：{{ result.totalScore }}分 | 共{{ result.questions?.length }}题</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { paperApi, questionApi } from '../api'
import { ElMessage } from 'element-plus'

const chapters = ref([])
const generating = ref(false)
const result = ref(null)

const form = reactive({
  title: '', durationMinutes: 120,
  singleChoiceCount: 10, multipleChoiceCount: 5, trueFalseCount: 5,
  fillBlankCount: 5, shortAnswerCount: 2, programmingCount: 1,
  chapters: [], easyPercent: 30, mediumPercent: 50, hardPercent: 20
})

const generate = async () => {
  if (!form.title) { ElMessage.warning('请输入试卷标题'); return }
  generating.value = true
  try {
    const data = { ...form, chapters: form.chapters.length ? form.chapters : null }
    result.value = (await paperApi.autoGenerate(data)).data
    ElMessage.success('组卷成功！')
  } catch (e) {
    ElMessage.error('组卷失败: ' + (e.response?.data?.message || e.message))
  }
  generating.value = false
}

const exportPaper = () => { if (result.value) window.open(paperApi.exportUrl(result.value.id), '_blank') }

onMounted(async () => {
  try { chapters.value = (await questionApi.chapters()).data } catch {}
})
</script>
