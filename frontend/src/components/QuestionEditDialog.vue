<template>
  <el-dialog v-model="visible" :title="isEdit ? '修改题目' : '新增题目'" width="750px" @close="handleClose">
    <el-form :model="formData" label-width="80px">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="题型">
            <el-select v-model="formData.type" style="width:100%">
              <el-option v-for="t in types" :key="t.value" :label="t.label" :value="t.value"/>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="难度">
            <el-select v-model="formData.difficulty" style="width:100%">
              <el-option label="简单" value="EASY"/><el-option label="中等" value="MEDIUM"/><el-option label="困难" value="HARD"/>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="分值">
            <el-input-number v-model="formData.defaultScore" :min="1" :max="50" style="width:100%"/>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="章节">
            <el-select v-model="formData.chapterName" style="width:100%" filterable allow-create>
              <el-option v-for="c in chapters" :key="c.id" :label="c.name" :value="c.name"/>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="来源">
            <el-select v-model="formData.source" style="width:100%" filterable allow-create>
              <el-option v-for="s in sources" :key="s" :label="s" :value="s"/>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="题目"><el-input v-model="formData.content" type="textarea" :rows="2"/></el-form-item>
      
      <el-form-item label="选项" v-if="formData.type==='SINGLE_CHOICE'||formData.type==='MULTIPLE_CHOICE'">
        <el-input v-model="formData.options" type="textarea" :rows="2" placeholder='JSON 格式: [{"label":"A","text":"xxx"},...]'/>
      </el-form-item>
      
      <el-form-item label="关联工程" v-if="formData.type === 'PROGRAMMING' || formData.type === 'CODE_READING'">
        <el-input v-model="formData.projectPath" placeholder="如: projects/payment-system-question"/>
      </el-form-item>
      <el-form-item label="答案工程" v-if="formData.type === 'PROGRAMMING' || formData.type === 'CODE_READING'">
        <el-input v-model="formData.answerProjectPath" placeholder="如: answer-projects/payment-system-answer"/>
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="答案"><el-input v-model="formData.answer" type="textarea" :rows="2"/></el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="解析"><el-input v-model="formData.explanation" type="textarea" :rows="2"/></el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="优化要求">
        <el-input v-model="optimizePrompt" type="textarea" :rows="1" placeholder="如：请将题干表述更清晰，并补充更严谨的答案与解析"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible=false">取消</el-button>
      <el-button type="success" 
                 :loading="syncing" 
                 :disabled="syncing || !formData.answerProjectPath || !formData.answer" 
                 v-if="formData.type === 'PROGRAMMING' || formData.type === 'CODE_READING'"
                 @click="syncAnswerToProject">同步至答案工程</el-button>
      <el-button type="warning" :loading="optimizing" :disabled="optimizing" @click="optimizeQ">AI 优化</el-button>
      <el-button type="primary" @click="saveQ">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { questionApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { optimizeQuestionDraft } from '../utils/questionOptimize'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  isEdit: { type: Boolean, default: false },
  questionData: { type: Object, default: () => ({}) },
  chapters: { type: Array, default: () => [] },
  sources: { type: Array, default: () => [] },
  types: { type: Array, default: () => [] }
})

const emit = defineEmits(['update:modelValue', 'saved'])

const visible = ref(props.modelValue)
const formData = ref({})
const optimizing = ref(false)
const syncing = ref(false)
const optimizePrompt = ref('')

watch(() => props.modelValue, (newVal) => {
  visible.value = newVal
  if (newVal) {
    formData.value = { ...props.questionData }
    optimizePrompt.value = ''
  }
}, { immediate: true })

watch(() => visible.value, (newVal) => {
  emit('update:modelValue', newVal)
})

const handleClose = () => {
  visible.value = false
}

const saveQ = async () => {
  try {
    if (props.isEdit) {
      await questionApi.update(formData.value.id, formData.value)
      ElMessage.success('已修改')
    } else {
      await questionApi.create(formData.value)
      ElMessage.success('已添加')
    }
    visible.value = false
    emit('saved', formData.value)
  } catch (e) {
    ElMessage.error((props.isEdit ? '修改' : '添加') + '失败: ' + (e.response?.data?.message || e.message))
  }
}

const optimizeQ = async () => {
  if (!optimizePrompt.value?.trim()) {
    optimizePrompt.value = '请将题干表述更清晰，并补充更严谨的答案与解析'
  }
  optimizing.value = true
  try {
    await optimizeQuestionDraft({
      draft: formData.value,
      prompt: optimizePrompt.value,
      questionApi,
      ElMessage
    })
  } finally {
    optimizing.value = false
  }
}

const syncAnswerToProject = async () => {
  if (!formData.value.answerProjectPath) {
    ElMessage.warning('请先填写答案工程路径')
    return
  }
  if (!formData.value.answer) {
    ElMessage.warning('答案内容为空，无法同步')
    return
  }
  
  try {
    await ElMessageBox.confirm('此操作将根据答案内容自动覆盖本地答案工程中的相关文件，是否确认继续？', '警告', {
      confirmButtonText: '确认同步',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return
  }

  syncing.value = true
  try {
    const res = await questionApi.syncAnswerToProject({
      answerText: formData.value.answer,
      answerProjectPath: formData.value.answerProjectPath
    })
    const files = res.data.updatedFiles || []
    ElMessage.success(`同步成功！已更新 ${files.length} 个文件。\n${files.join('\n')}`)
  } catch (e) {
    ElMessage.error('同步失败: ' + (e.response?.data?.message || e.message))
  } finally {
    syncing.value = false
  }
}
</script>
