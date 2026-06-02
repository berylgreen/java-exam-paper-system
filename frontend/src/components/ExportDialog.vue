<template>
  <el-dialog v-model="visible" title="导出选项" width="400px" @closed="onClosed">
    <el-form label-width="100px">
      <el-form-item label="导出格式">
        <el-checkbox-group v-model="exportTypes">
          <el-checkbox label="docx">Word (.docx)</el-checkbox>
          <el-checkbox label="pdf">PDF (.pdf)</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="包含答案" v-if="showAnswerOption">
        <el-switch v-model="withAnswer" active-text="是" inactive-text="否" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="confirm" :disabled="exportTypes.length === 0">确认导出</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: Boolean,
  showAnswerOption: { type: Boolean, default: false },
  defaultWithAnswer: { type: Boolean, default: false }
})

const emit = defineEmits(['update:modelValue', 'confirm'])

const visible = ref(props.modelValue)
const exportTypes = ref(['docx', 'pdf'])
const withAnswer = ref(props.defaultWithAnswer)

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    withAnswer.value = props.defaultWithAnswer
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const confirm = () => {
  emit('confirm', {
    types: exportTypes.value,
    withAnswer: withAnswer.value
  })
  visible.value = false
}

const onClosed = () => {
  // reset state if needed
}
</script>
