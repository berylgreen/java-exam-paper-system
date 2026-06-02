export const optimizeQuestionDraft = async ({ draft, prompt, questionApi, ElMessage }) => {
  if (!draft.content?.trim()) {
    ElMessage.error('请先输入题目内容')
    return false
  }
  if (!prompt?.trim()) {
    ElMessage.error('请输入优化要求')
    return false
  }

  try {
    const res = await questionApi.optimizePreview({ question: { ...draft }, prompt: prompt.trim() })
    Object.assign(draft, res.data.optimizedQuestion || {})
    ElMessage.success('AI 优化完成，请确认后保存')
    return true
  } catch (e) {
    ElMessage.error('AI 优化失败: ' + (e.response?.data?.message || e.message))
    return false
  }
}
