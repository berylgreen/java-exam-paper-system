import axios from 'axios'

const api = axios.create({ baseURL: '/api', timeout: 10000 })

// ===== 题目 API =====
export const questionApi = {
    list: (params) => api.get('/questions', { params }),
    getById: (id) => api.get(`/questions/${id}`),
    create: (data) => api.post('/questions', data),
    update: (id, data) => api.put(`/questions/${id}`, data),
    delete: (id) => api.delete(`/questions/${id}`),
    chapters: () => api.get('/questions/chapters'),
    sources: () => api.get('/questions/sources'),
    stats: () => api.get('/questions/stats'),
}

// ===== 试卷 API =====
export const paperApi = {
    list: () => api.get('/papers'),
    getById: (id) => api.get(`/papers/${id}`),
    create: (data) => api.post('/papers', data),
    autoGenerate: (data) => api.post('/papers/auto-generate', data),
    delete: (id) => api.delete(`/papers/${id}`),
    exportUrl: (id) => `/api/papers/${id}/export`,
}

export default api
