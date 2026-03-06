<template>
  <div>
    <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px">
      <h2 class="page-title" style="margin-bottom:0">📚 题库管理</h2>
      <el-button type="primary" @click="showAdd = true"><el-icon><Plus /></el-icon> 新增题目</el-button>
    </div>

    <!-- 统计 -->
    <div class="stat-cards" v-if="stats.total">
      <div class="stat-card">
        <div class="stat-value">{{ stats.total }}</div><div class="stat-label">题目总数</div>
      </div>
      <div class="stat-card" v-for="(v,k) in stats.byType" :key="k">
        <div class="stat-value">{{ v }}</div><div class="stat-label">{{ k }}</div>
      </div>
    </div>

    <!-- 筛选 -->
    <div class="filter-bar">
      <el-select v-model="filter.type" placeholder="题型" clearable style="width:130px" @change="loadQ">
        <el-option v-for="t in types" :key="t.value" :label="t.label" :value="t.value" />
      </el-select>
      <el-select v-model="filter.chapter" placeholder="章节" clearable style="width:180px" @change="loadQ">
        <el-option v-for="c in chapters" :key="c" :label="c" :value="c" />
      </el-select>
      <el-select v-model="filter.difficulty" placeholder="难度" clearable style="width:110px" @change="loadQ">
        <el-option label="简单" value="EASY"/><el-option label="中等" value="MEDIUM"/><el-option label="困难" value="HARD"/>
      </el-select>
    </div>

    <!-- 题目列表 -->
    <el-table :data="questions" v-loading="loading" stripe style="width:100%">
      <el-table-column label="ID" prop="id" width="55" align="center"/>
      <el-table-column label="题型" width="90" align="center">
        <template #default="{row}"><span class="type-tag" :class="typeClass(row.type)">{{ typeLabel(row.type) }}</span></template>
      </el-table-column>
      <el-table-column label="章节" prop="chapter" width="150"/>
      <el-table-column label="难度" width="75" align="center">
        <template #default="{row}"><el-tag :type="diffColor(row.difficulty)" size="small" effect="dark">{{ diffLabel(row.difficulty) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="题目内容" prop="content" min-width="280">
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
        <p><b>题型：</b>{{ typeLabel(detailQ.type) }} | <b>章节：</b>{{ detailQ.chapter }} | <b>难度：</b>{{ diffLabel(detailQ.difficulty) }}</p>
        <p style="margin:12px 0"><b>题目：</b>{{ detailQ.content }}</p>
        <div v-if="detailQ.options" style="margin:8px 0">
          <b>选项：</b>
          <div v-for="opt in parseOpts(detailQ.options)" :key="opt.label" style="margin-left:16px">{{ opt.label }}. {{ opt.text }}</div>
        </div>
        <p style="color:#6dd49e"><b>答案：</b>{{ detailQ.answer }}</p>
        <p v-if="detailQ.explanation" style="color:#aaa;margin-top:8px"><b>解析：</b>{{ detailQ.explanation }}</p>
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
          <el-select v-model="newQ.chapter" style="width:100%" filterable allow-create>
            <el-option v-for="c in chapters" :key="c" :label="c" :value="c"/>
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="newQ.difficulty" style="width:100%">
            <el-option label="简单" value="EASY"/><el-option label="中等" value="MEDIUM"/><el-option label="困难" value="HARD"/>
          </el-select>
        </el-form-item>
        <el-form-item label="题目"><el-input v-model="newQ.content" type="textarea" :rows="3"/></el-form-item>
        <el-form-item label="选项" v-if="newQ.type==='SINGLE_CHOICE'||newQ.type==='MULTIPLE_CHOICE'">
          <el-input v-model="newQ.options" type="textarea" :rows="3" placeholder='JSON 格式: [{"label":"A","text":"xxx"},...]'/>
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

const types = [
  {value:'SINGLE_CHOICE',label:'单选题'},{value:'MULTIPLE_CHOICE',label:'多选题'},
  {value:'TRUE_FALSE',label:'判断题'},{value:'FILL_BLANK',label:'填空题'},
  {value:'SHORT_ANSWER',label:'简答题'},{value:'PROGRAMMING',label:'编程题'}
]
const typeLabel = (t) => types.find(x=>x.value===t)?.label || t
const typeClass = (t) => t==='SINGLE_CHOICE'?'tag-easy':t==='MULTIPLE_CHOICE'?'tag-medium':t==='PROGRAMMING'?'tag-hard':'tag-easy'
const diffLabel = (d) => ({EASY:'简单',MEDIUM:'中等',HARD:'困难'})[d] || d
const diffColor = (d) => ({EASY:'success',MEDIUM:'warning',HARD:'danger'})[d] || ''
const parseOpts = (s) => { try { return JSON.parse(s) } catch { return [] } }

const questions = ref([])
const loading = ref(false)
const chapters = ref([])
const stats = ref({})
const filter = reactive({ type: null, chapter: null, difficulty: null })
const page = ref(1)
const pageSize = 20
const total = ref(0)
const showDetail = ref(false)
const detailQ = ref({})
const showAdd = ref(false)
const newQ = reactive({ type:'SINGLE_CHOICE', chapter:'', difficulty:'EASY', content:'', options:'', answer:'', explanation:'', defaultScore:2 })

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
    stats.value = (await questionApi.stats()).data
  } catch {}
}

const viewQ = (row) => { detailQ.value = row; showDetail.value = true }
const delQ = async (id) => {
  await ElMessageBox.confirm('确定删除？','提示',{type:'warning'})
  await questionApi.delete(id); ElMessage.success('已删除'); loadQ(); loadMeta()
}
const addQ = async () => {
  try {
    await questionApi.create(newQ); ElMessage.success('已添加'); showAdd.value = false; loadQ(); loadMeta()
  } catch (e) { ElMessage.error('添加失败: ' + (e.response?.data?.message||e.message)) }
}

onMounted(() => { loadQ(); loadMeta() })
</script>
