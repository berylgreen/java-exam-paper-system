import { describe, expect, it, vi } from 'vitest'
import { optimizeQuestionDraft } from './questionOptimize'

describe('optimizeQuestionDraft', () => {
  it('rejects empty content', async () => {
    const ElMessage = { error: vi.fn(), success: vi.fn() }
    const questionApi = { optimizePreview: vi.fn() }
    const draft = { content: '   ' }

    const result = await optimizeQuestionDraft({ draft, prompt: '优化一下', questionApi, ElMessage })

    expect(result).toBe(false)
    expect(ElMessage.error).toHaveBeenCalledWith('请先输入题目内容')
    expect(questionApi.optimizePreview).not.toHaveBeenCalled()
  })

  it('rejects empty prompt', async () => {
    const ElMessage = { error: vi.fn(), success: vi.fn() }
    const questionApi = { optimizePreview: vi.fn() }
    const draft = { content: '原题' }

    const result = await optimizeQuestionDraft({ draft, prompt: '   ', questionApi, ElMessage })

    expect(result).toBe(false)
    expect(ElMessage.error).toHaveBeenCalledWith('请输入优化要求')
    expect(questionApi.optimizePreview).not.toHaveBeenCalled()
  })

  it('fills optimized fields after success', async () => {
    const ElMessage = { error: vi.fn(), success: vi.fn() }
    const questionApi = {
      optimizePreview: vi.fn().mockResolvedValue({
        data: {
          optimizedQuestion: {
            content: '优化后的题干',
            options: '[{"label":"A","text":"优化后的选项"}]',
            answer: 'A',
            explanation: '优化后的解析'
          }
        }
      })
    }
    const draft = {
      content: '原题',
      options: '原选项',
      answer: '原答案',
      explanation: '原解析',
      type: 'SINGLE_CHOICE'
    }

    const result = await optimizeQuestionDraft({ draft, prompt: '请优化题干和答案', questionApi, ElMessage })

    expect(result).toBe(true)
    expect(questionApi.optimizePreview).toHaveBeenCalledWith({
      question: expect.objectContaining({ content: '原题', answer: '原答案' }),
      prompt: '请优化题干和答案'
    })
    expect(draft.content).toBe('优化后的题干')
    expect(draft.options).toBe('[{"label":"A","text":"优化后的选项"}]')
    expect(draft.answer).toBe('A')
    expect(draft.explanation).toBe('优化后的解析')
    expect(ElMessage.success).toHaveBeenCalledWith('AI 优化完成，请确认后保存')
  })

  it('shows backend error message on failure', async () => {
    const ElMessage = { error: vi.fn(), success: vi.fn() }
    const questionApi = {
      optimizePreview: vi.fn().mockRejectedValue({
        response: { data: { message: '上游服务超时' } }
      })
    }
    const draft = { content: '原题' }

    const result = await optimizeQuestionDraft({ draft, prompt: '优化一下', questionApi, ElMessage })

    expect(result).toBe(false)
    expect(ElMessage.error).toHaveBeenCalledWith('AI 优化失败: 上游服务超时')
  })
})
