import { describe, it, expect, vi } from 'vitest'
import api, { questionApi, paperApi } from './index'

vi.mock('axios', () => {
  const mockApi = {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  }
  return {
    default: {
      create: vi.fn(() => mockApi)
    }
  }
})

describe('API Utils', () => {
  it('questionApi.list should make GET request with params', () => {
    questionApi.list({ page: 1 })
    expect(api.get).toHaveBeenCalledWith('/questions', { params: { page: 1 } })
  })

  it('questionApi.getById should make GET request', () => {
    questionApi.getById(123)
    expect(api.get).toHaveBeenCalledWith('/questions/123')
  })

  it('paperApi.list should make GET request', () => {
    paperApi.list()
    expect(api.get).toHaveBeenCalledWith('/papers')
  })

  it('paperApi.autoGenerate should make POST request', () => {
    const payload = { title: 'Test Paper' }
    paperApi.autoGenerate(payload)
    expect(api.post).toHaveBeenCalledWith('/papers/auto-generate', payload)
  })
})
