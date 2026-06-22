import axios from 'axios'

const api = axios.create({ baseURL: '/api', timeout: 60000 })

// ===== 题目 API =====
export const questionApi = {
    list: (params) => api.get('/questions', { params }),
    getById: (id) => api.get(`/questions/${id}`),
    create: (data) => api.post('/questions', data),
    update: (id, data) => api.put(`/questions/${id}`, data),
    delete: (id) => api.delete(`/questions/${id}`),
    batchDelete: (ids) => api.delete('/questions/batch', { data: ids }),
    batchUpdateScore: (ids, score) => api.put('/questions/batch/score', { ids, score }),
    batchOptimize: (ids, prompt) => api.post('/questions/batch/optimize', { ids, prompt }),
    toggleFavorite: (id, favorite) => api.put(`/questions/${id}/favorite?favorite=${favorite}`),
    chapters: () => api.get('/questions/chapters'),
    sources: () => api.get('/questions/sources'),
    stats: () => api.get('/questions/stats'),
    optimizePreview: (data) => api.post('/questions/optimize-preview', data),
    getProjectCode: (id, type = 'project') => api.get(`/questions/${id}/project-code`, { params: { type } }),
    exportUrl: '/api/questions/export',
    importFile: (file) => {
        const formData = new FormData()
        formData.append('file', file)
        return api.post('/questions/import', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        })
    },
}

export const paperApi = {
    list: () => api.get('/papers'),
    getById: (id) => api.get(`/papers/${id}`),
    create: (data) => api.post('/papers', data),
    autoGenerate: (data) => api.post('/papers/auto-generate', data),
    previewGenerate: (data) => api.post('/papers/preview-generate', data),
    saveGenerated: (data) => api.post('/papers/save-generated', data),
    delete: (id) => api.delete(`/papers/${id}`),
    batchDelete: (ids) => api.delete('/papers/batch', { data: ids }),
    replaceQuestion: (id, data) => api.put(`/papers/${id}/replace-question`, data),
    reorderQuestions: (id, data) => api.put(`/papers/${id}/reorder`, data),
    updateTitle: (id, title) => api.put(`/papers/${id}/title`, { title }),
    exportUrl: (id, withAnswer = false, types = ['docx', 'pdf'], answerSheetType = 'generate') => `/api/papers/${id}/export?withAnswer=${withAnswer}&types=${types.join(',')}&answerSheetType=${answerSheetType}`,
}

export default api
