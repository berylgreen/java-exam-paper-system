import { mount } from '@vue/test-utils'
import { describe, it, expect, vi } from 'vitest'
import PaperViewer from './PaperViewer.vue'

vi.mock('../api', () => ({
  questionApi: {
    chapters: vi.fn(() => Promise.resolve({ data: [] }))
  }
}))

describe('PaperViewer.vue', () => {
  it('renders paper title and stats when paper prop is provided', () => {
    const mockPaper = {
      title: '2024 Java Final',
      totalScore: 100,
      durationMinutes: 120,
      questions: []
    }

    const wrapper = mount(PaperViewer, {
      props: {
        paper: mockPaper,
        showAnswer: false,
        allowReplace: false
      },
      global: {
        stubs: {
          'el-button': true,
          'el-icon': true,
          'Refresh': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-select': true,
          'el-option': true,
          'el-pagination': true
        }
      }
    })

    expect(wrapper.text()).toContain('2024 Java Final')
    expect(wrapper.text()).toContain('总分: 100分')
    expect(wrapper.text()).toContain('时间: 120分钟')
  })

  it('renders questions properly grouped by type', () => {
    const mockPaper = {
      title: 'Test Paper',
      totalScore: 10,
      durationMinutes: 60,
      questions: [
        {
          id: 1,
          score: 5,
          question: {
            id: 1,
            type: 'SINGLE_CHOICE',
            content: 'What is Java?',
            options: '[{"label":"A","text":"Language"}]',
            answer: 'A'
          }
        },
        {
          id: 2,
          score: 5,
          question: {
            id: 2,
            type: 'SHORT_ANSWER',
            content: 'Explain OOP.',
            answer: 'Object Oriented Programming'
          }
        }
      ]
    }

    const wrapper = mount(PaperViewer, {
      props: {
        paper: mockPaper,
        showAnswer: true,
        allowReplace: false
      },
      global: {
        stubs: {
          'el-button': true,
          'el-icon': true,
          'Refresh': true,
          'el-dialog': true,
          'el-form': true,
          'el-form-item': true,
          'el-select': true,
          'el-option': true,
          'el-pagination': true
        }
      }
    })

    expect(wrapper.text()).toContain('单选题')
    expect(wrapper.text()).toContain('简答题')
    expect(wrapper.text()).toContain('What is Java?')
    expect(wrapper.text()).toContain('Explain OOP.')
    // answers should be visible because showAnswer is true
    expect(wrapper.text()).toContain('答案:')
  })
})
