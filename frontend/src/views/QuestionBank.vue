<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px">
      <h2 class="page-title" style="margin-bottom:0">📚 题库管理</h2>
      <div style="display:flex;gap:10px">
        <el-button type="danger" @click="batchDelQ" :disabled="selectedIds.length === 0"><el-icon><Delete /></el-icon> 批量删除</el-button>
        <el-button type="success" @click="exportQ"><el-icon><Download /></el-icon> 导出题库</el-button>
        <el-button type="warning" @click="triggerImport"><el-icon><Upload /></el-icon> 导入题库</el-button>
        <input ref="importInput" type="file" accept=".json" style="display:none" @change="importQ" />
        <el-button type="primary" @click="showAdd = true"><el-icon><Plus /></el-icon> 新增题目</el-button>
      </div>
    </div>

    <!-- 统计 -->
    <div v-if="stats.total" style="margin-bottom: 24px; background: rgba(255,255,255,0.02); padding: 16px; border-radius: 12px; border: 1px solid rgba(255,255,255,0.05);">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
        <div style="font-size: 16px; font-weight: 600; color: #e0e0e0; display: flex; align-items: center; gap: 10px;">
          <span>📊 题库统计</span>
          <el-tag size="small" type="primary" effect="dark" round>总题目数: {{ stats.total }}</el-tag>
        </div>
        <el-radio-group v-model="activeStat" size="small">
          <el-radio-button value="source">按来源</el-radio-button>
          <el-radio-button value="type">按题型</el-radio-button>
          <el-radio-button value="difficulty">按难度</el-radio-button>
        </el-radio-group>
      </div>
      
      <div v-if="activeStat === 'type'">
        <div class="stat-cards" style="margin-bottom: 0; grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));">
          <div class="stat-card" v-for="(v,k) in stats.byType" :key="'type-'+k">
            <div class="stat-value" style="font-size: 24px;">{{ v }}</div><div class="stat-label">{{ k }}</div>
          </div>
        </div>
      </div>

      <div v-if="activeStat === 'difficulty'">
        <div class="stat-cards" style="margin-bottom: 0; grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));">
          <div class="stat-card" v-for="(v,k) in stats.byDifficulty" :key="'diff-'+k" style="border-top: 2px solid #e6a23c;">
            <div class="stat-value" style="font-size: 24px;">{{ v }}</div><div class="stat-label">{{ k }}</div>
          </div>
        </div>
      </div>

      <div v-if="activeStat === 'source'">
        <div class="stat-cards" style="margin-bottom: 0; grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));">
          <div class="stat-card" v-for="(v,k) in stats.bySource" :key="'src-'+k" style="border-top: 2px solid #4ecdc4;">
            <div class="stat-value" style="font-size: 24px;">{{ v }}</div><div class="stat-label">{{ k }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 筛选 -->
    <div class="filter-bar">
      <el-select v-model="filter.type" placeholder="题型" clearable style="width:130px" @change="loadQ">
        <el-option v-for="t in types" :key="t.value" :label="t.label" :value="t.value" />
      </el-select>
      <el-select v-model="filter.chapterId" placeholder="章节" clearable style="width:180px" @change="loadQ">
        <el-option v-for="c in chapters" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-select v-model="filter.difficulty" placeholder="难度" clearable style="width:110px" @change="loadQ">
        <el-option label="简单" value="EASY"/><el-option label="中等" value="MEDIUM"/><el-option label="困难" value="HARD"/>
      </el-select>
      <el-select v-model="filter.source" placeholder="来源" clearable style="width:160px" @change="loadQ">
        <el-option v-for="s in sources" :key="s" :label="s" :value="s" />
      </el-select>
    </div>

    <!-- 题目列表 -->
    <el-table :data="questions" v-loading="loading" stripe style="width:100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" prop="id" width="55" align="center"/>
      <el-table-column label="题型" width="90" align="center">
        <template #default="{row}"><span class="type-tag" :class="typeClass(row.type)">{{ typeLabel(row.type) }}</span></template>
      </el-table-column>
      <el-table-column label="章节" prop="chapterName" width="150"/>
      <el-table-column label="难度" width="75" align="center">
        <template #default="{row}"><el-tag :type="diffColor(row.difficulty)" size="small" effect="dark">{{ diffLabel(row.difficulty) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="来源" prop="source" width="130">
        <template #default="{row}"><el-tag size="small" :type="row.source==='课后习题原题'?'warning':'info'" effect="plain">{{ row.source || '未知' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="题目内容" prop="content" min-width="250">
        <template #default="{row}"><span style="display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden;line-height:1.6">{{ row.content }}</span></template>
      </el-table-column>
      <el-table-column label="分值" prop="defaultScore" width="55" align="center"/>
      <el-table-column label="操作" width="120" align="center">
        <template #default="{row}">
          <el-button link type="primary" @click="viewQ(row)">查看</el-button>
          <el-button link type="danger" @click="delQ(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:center" background
      layout="prev, pager, next, total" :total="total" :page-size="pageSize"
      v-model:current-page="page" @current-change="loadQ"/>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="showDetail" :title="detailQ.content?.substring(0,30)+'...'" width="600px">
      <div v-if="detailQ.id">
        <p><b>题型：</b>{{ typeLabel(detailQ.type) }} | <b>章节：</b>{{ detailQ.chapterName }} | <b>难度：</b>{{ diffLabel(detailQ.difficulty) }} | <b>来源：</b>{{ detailQ.source || '未知' }}</p>
        <div style="margin:12px 0; display:flex;">
          <b style="white-space:nowrap;margin-right:4px;">题目：</b>
          <div class="markdown-body" style="flex:1;" v-html="renderMarkdown(detailQ.content)"></div>
        </div>
        <p v-if="detailQ.projectPath" style="margin:12px 0; color:#409EFF; display:flex; align-items:center; gap:10px">
          <span><b>📁 关联工程：</b>{{ detailQ.projectPath }}</span>
          <el-button type="primary" size="small" @click="downloadProject(detailQ.id)">
            <el-icon><Download /></el-icon> 下载工程
          </el-button>
        </p>
        <div v-if="detailQ.options" style="margin:8px 0">
          <b>选项：</b>
          <div v-for="opt in parseOpts(detailQ.options)" :key="opt.label" style="margin-left:16px">{{ opt.label }}. {{ opt.text }}</div>
        </div>
        <div style="color:#6dd49e; display:flex;">
          <b style="white-space:nowrap;margin-right:4px;">答案：</b>
          <div class="markdown-body" style="flex:1;" v-html="renderMarkdown(detailQ.answer)"></div>
        </div>
        <div v-if="detailQ.explanation" style="color:#aaa;margin-top:8px; display:flex;">
          <b style="white-space:nowrap;margin-right:4px;">解析：</b>
          <div class="markdown-body" style="flex:1;" v-html="renderMarkdown(detailQ.explanation)"></div>
        </div>
      </div>
    </el-dialog>

    <!-- 新增对话框 -->
    <el-dialog v-model="showAdd" title="新增题目" width="600px">
      <el-form :model="newQ" label-width="80px">
        <el-form-item label="题型">
          <el-select v-model="newQ.type" style="width:100%">
            <el-option v-for="t in types" :key="t.value" :label="t.label" :value="t.value"/>
          </el-select>
        </el-form-item>
        <el-form-item label="章节">
          <el-select v-model="newQ.chapterName" style="width:100%" filterable allow-create>
            <el-option v-for="c in chapters" :key="c.id" :label="c.name" :value="c.name"/>
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="newQ.difficulty" style="width:100%">
            <el-option label="简单" value="EASY"/><el-option label="中等" value="MEDIUM"/><el-option label="困难" value="HARD"/>
          </el-select>
        </el-form-item>
        <el-form-item label="来源">
          <el-select v-model="newQ.source" style="width:100%" filterable allow-create>
            <el-option v-for="s in sources" :key="s" :label="s" :value="s"/>
          </el-select>
        </el-form-item>
        <el-form-item label="题目"><el-input v-model="newQ.content" type="textarea" :rows="3"/></el-form-item>
        <el-form-item label="选项" v-if="newQ.type==='SINGLE_CHOICE'||newQ.type==='MULTIPLE_CHOICE'">
          <el-input v-model="newQ.options" type="textarea" :rows="3" placeholder='JSON 格式: [{"label":"A","text":"xxx"},...]'/>
        </el-form-item>
        <el-form-item label="工程路径" v-if="newQ.type==='PROGRAMMING'">
          <el-input v-model="newQ.projectPath" placeholder="如: projects/payment-system-question"/>
        </el-form-item>
        <el-form-item label="答案"><el-input v-model="newQ.answer" type="textarea" :rows="2"/></el-form-item>
        <el-form-item label="解析"><el-input v-model="newQ.explanation" type="textarea" :rows="2"/></el-form-item>
        <el-form-item label="分值"><el-input-number v-model="newQ.defaultScore" :min="1" :max="50"/></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAdd=false">取消</el-button>
        <el-button type="primary" @click="addQ">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { questionApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { marked } from 'marked'

const renderMarkdown = (text) => {
  if (!text) return ''
  return marked.parse(text)
}

const types = [
  {value:'SINGLE_CHOICE',label:'单选题'},{value:'MULTIPLE_CHOICE',label:'多选题'},
  {value:'TRUE_FALSE',label:'判断题'},{value:'FILL_BLANK',label:'填空题'},
  {value:'SHORT_ANSWER',label:'简答题'},{value:'CODE_READING',label:'程序分析题'},{value:'PROGRAMMING',label:'编程题'}
]
const typeLabel = (t) => types.find(x=>x.value===t)?.label || t
const typeClass = (t) => t==='SINGLE_CHOICE'?'tag-easy':t==='MULTIPLE_CHOICE'?'tag-medium':(t==='PROGRAMMING'||t==='CODE_READING')?'tag-hard':'tag-easy'
const diffLabel = (d) => ({EASY:'简单',MEDIUM:'中等',HARD:'困难'})[d] || d
const diffColor = (d) => ({EASY:'success',MEDIUM:'warning',HARD:'danger'})[d] || ''
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

const questions = ref([])
const loading = ref(false)
const chapters = ref([])
const sources = ref([])
const stats = ref({})
const activeStat = ref('source')
const filter = reactive({ type: null, chapterId: null, difficulty: null, source: null })
const page = ref(1)
const pageSize = 20
const total = ref(0)
const selectedIds = ref([])
const showDetail = ref(false)
const detailQ = ref({})
const showAdd = ref(false)
const importInput = ref(null)
const newQ = reactive({ type:'SINGLE_CHOICE', chapterName:'', difficulty:'EASY', content:'', options:'', answer:'', explanation:'', defaultScore:2, source:'网络2026年1月', projectPath:'' })

const loadQ = async () => {
  loading.value = true
  try {
    const params = { page: page.value - 1, size: pageSize, ...filter }
    Object.keys(params).forEach(k => { if (!params[k] && params[k] !== 0) delete params[k] })
    const res = (await questionApi.list(params)).data
    questions.value = res.content || []
    total.value = res.totalElements || 0
  } catch { ElMessage.error('加载失败') }
  loading.value = false
}

const loadMeta = async () => {
  try {
    chapters.value = (await questionApi.chapters()).data
    sources.value = (await questionApi.sources()).data
    stats.value = (await questionApi.stats()).data
  } catch {}
}

const viewQ = (row) => { detailQ.value = row; showDetail.value = true }
const delQ = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除？该题目将从所有试卷中移除。','提示',{type:'warning'})
    await questionApi.delete(id); ElMessage.success('已删除'); loadQ(); loadMeta()
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') {
      ElMessage.error('删除失败: ' + (e.response?.data?.message || e.message))
    }
  }
}

const handleSelectionChange = (val) => {
  selectedIds.value = val.map(item => item.id)
}

const batchDelQ = async () => {
  if (!selectedIds.value.length) return
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 道题目？该操作将从所有相关试卷中移除这些题目。`, '⚠️ 批量删除', {type: 'warning', confirmButtonText: '确定删除', cancelButtonText: '取消'})
    await questionApi.batchDelete(selectedIds.value)
    ElMessage.success('批量删除成功')
    loadQ()
    loadMeta()
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') {
      ElMessage.error('批量删除失败: ' + (e.response?.data?.message || e.message))
    }
  }
}

const addQ = async () => {
  try {
    await questionApi.create(newQ); ElMessage.success('已添加'); showAdd.value = false; loadQ(); loadMeta()
  } catch (e) { ElMessage.error('添加失败: ' + (e.response?.data?.message||e.message)) }
}

const exportQ = () => {
  window.open(questionApi.exportUrl, '_blank')
}

const downloadProject = (id) => {
  window.open(`/api/questions/${id}/download-project`, '_blank')
}

const triggerImport = () => {
  importInput.value.click()
}

const importQ = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  try {
    await ElMessageBox.confirm(
      '导入将替换当前全部题库数据，确定继续？',
      '⚠️ 导入题库',
      { type: 'warning', confirmButtonText: '确定导入', cancelButtonText: '取消' }
    )
    const res = await questionApi.importFile(file)
    ElMessage.success(res.data.message || '导入成功')
    loadQ(); loadMeta()
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') {
      ElMessage.error('导入失败: ' + (e.response?.data?.message || e.message))
    }
  }
  event.target.value = ''
}

onMounted(() => { loadQ(); loadMeta() })
</script>
