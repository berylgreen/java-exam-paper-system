import axios from 'axios'

const api = axios.create({ baseURL: '/api', timeout: 10000 })

// ===== 题目 API =====
export const questionApi = {
    list: (params) => api.get('/questions', { params }),
    getById: (id) => api.get(`/questions/${id}`),
    create: (data) => api.post('/questions', data),
    update: (id, data) => api.put(`/questions/${id}`, data),
    delete: (id) => api.delete(`/questions/${id}`),
    batchDelete: (ids) => api.delete('/questions/batch', { data: ids }),
    chapters: () => api.get('/questions/chapters'),
    sources: () => api.get('/questions/sources'),
    stats: () => api.get('/questions/stats'),
    optimizePreview: (data) => api.post('/questions/optimize-preview', data),
    exportUrl: '/api/questions/export',
    importFile: (file) => {
        const formData = new FormData()
        formData.append('file', file)
        return api.post('/questions/import', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        })
    },
}

// ===== 试卷 API =====
export const paperApi = {
    list: () => api.get('/papers'),
    getById: (id) => api.get(`/papers/${id}`),
    create: (data) => api.post('/papers', data),
    autoGenerate: (data) => api.post('/papers/auto-generate', data),
    previewGenerate: (data) => api.post('/papers/preview-generate', data),
    saveGenerated: (data) => api.post('/papers/save-generated', data),
    delete: (id) => api.delete(`/papers/${id}`),
    exportUrl: (id, withAnswer = false) => `/api/papers/${id}/export${withAnswer ? '?withAnswer=true' : ''}`,
}

export default api
